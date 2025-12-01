package kr.modusplant.infrastructure.jwt.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import kr.modusplant.infrastructure.jwt.exception.InvalidTokenException;
import kr.modusplant.infrastructure.jwt.exception.TokenKeyCreationException;
import kr.modusplant.infrastructure.jwt.exception.TokenKeyStorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * 순수 JWT 제어 Provider
 * <p>
 * 기능 : 순수 JWT 암호화 토큰 발급/검증/추출만 담당 (비즈니스 로직 없음)
 * 사용법 : TokenService 로 해결되지 않는 세밀한 토큰 제어가 필요할 때 사용
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {
    @Value("${jwt.iss}")
    private String iss;

    @Value("${jwt.aud}")
    private String aud;

    @Value("${jwt.access_duration}")
    private long accessDuration;

    @Value("${jwt.refresh_duration}")
    private long refreshDuration;

    @Value("${keystore.key-store-password}")
    private String keyStorePassword;

    @Value("${keystore.key-store-type}")
    private String keyStoreType;

    @Value("${keystore.key-alias}")
    private String keyAlias;

    @Value("${keystore.key-store-filename}")
    private String keyStoreFilename;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    public void init() throws Exception {
        Path keyStorePath = Paths.get(System.getProperty("user.home")).resolve(keyStoreFilename);
        try {
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            char[] password = keyStorePassword.toCharArray();
            if (Files.exists(keyStorePath)) {
                InputStream inputStream = Files.newInputStream(keyStorePath);
                keyStore.load(inputStream, password);
                KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)
                        keyStore.getEntry(keyAlias, new KeyStore.PasswordProtection(password));
                privateKey = privateKeyEntry.getPrivateKey();
                publicKey = privateKeyEntry.getCertificate().getPublicKey();
            } else {
                // ECDSA 키 생성
                KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
                keyGen.initialize(256);
                KeyPair keyPair = keyGen.generateKeyPair();
                privateKey = keyPair.getPrivate();
                publicKey = keyPair.getPublic();

                X509Certificate selfSignedCert = generateSelfSignedCertificate(keyPair, "SHA256withECDSA");
                Certificate[] certChain = new Certificate[]{selfSignedCert};

                keyStore.load(null, password);
                keyStore.setKeyEntry(keyAlias, privateKey, password, certChain);

                OutputStream outputStream = Files.newOutputStream(keyStorePath);
                keyStore.store(outputStream, password);
            }
        } catch (KeyStoreException e) {
            throw new TokenKeyStorageException();
        } catch (NoSuchAlgorithmException e) {
            throw new TokenKeyCreationException();
        }
    }

    // Access RefreshToken 생성
    public String generateAccessToken(UUID uuid, Map<String, String> privateClaims) {
        Date now = new Date();
        Date iat = new Date(now.getTime());
        Date exp = new Date(iat.getTime() + accessDuration);

        return Jwts.builder()
                .claims(privateClaims)
                .issuer(iss)
                .subject(String.valueOf(uuid))
                .audience().add(aud).and()
                .issuedAt(iat)
                .expiration(exp)
                .signWith(privateKey)
                .compact();
    }

    // Refresh RefreshToken 생성
    public String generateRefreshToken(UUID uuid) {
        Date now = new Date();
        Date iat = new Date(now.getTime());
        Date exp = new Date(iat.getTime() + refreshDuration);

        return Jwts.builder()
                .issuer(iss)
                .subject(String.valueOf(uuid))
                .audience().add(aud).and()
                .issuedAt(iat)
                .expiration(exp)
                .signWith(privateKey)
                .compact();
    }

    // 토큰 검증하기
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(publicKey)
                    .clockSkewSeconds(3) // iat / exp 에 대해서 3초의 오차 허용
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        } catch (JwtException e) {
            throw new InvalidTokenException();
        }
    }

    // 토큰에서 정보 가져오기
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(publicKey)
                    .clockSkewSeconds(3) // iat / exp 에 대해서 3초의 오차 허용
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new InvalidTokenException();
        }
    }

    public UUID getMemberUuidFromToken(String token) {
        return UUID.fromString(getClaimsFromToken(token).getSubject());
    }

    public Date getIssuedAtFromToken(String token) {
        return getClaimsFromToken(token).getIssuedAt();
    }

    public Date getExpirationFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    // 자가 서명된 인증서 만들기
    public X509Certificate generateSelfSignedCertificate(KeyPair keyPair, String signatureAlgorithm) throws Exception {
        long now = System.currentTimeMillis();
        Date startDate = new Date(now);
        Date endDate = new Date(now + (1000L * 60 * 60 * 24 * 365)); // 1년 간 유효함

        X500NameBuilder nameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        nameBuilder.addRDN(BCStyle.CN, "ModusPlant Server");    // Common Name
        nameBuilder.addRDN(BCStyle.O, "ModusPlant");            // Organization
        nameBuilder.addRDN(BCStyle.C, "KR");                    // Country
        nameBuilder.addRDN(BCStyle.L, "Seoul");                 // Locality
        X500Name x500Name = nameBuilder.build();

        SecureRandom random = new SecureRandom();
        int byteCount = 20;
        byte[] bytes = new byte[byteCount];
        random.nextBytes(bytes);
        BigInteger serialNumber = new BigInteger(1, bytes);

        X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
                x500Name,            // 발행자
                serialNumber,        // 시리얼 넘버
                startDate,           // 시작일
                endDate,             // 종료일
                x500Name,            // 대상
                keyPair.getPublic()  // 대상 공공 키 정보
        );

        ContentSigner contentSigner = new JcaContentSignerBuilder(signatureAlgorithm).build(keyPair.getPrivate()); // 개인 키로 인증서 서명

        return new JcaX509CertificateConverter().getCertificate(builder.build(contentSigner));
    }

    // TODO: EMAIL 인증 관련 JWT 추가 필요
}
