package kr.modusplant.global.middleware.security.handler;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.modules.jwt.app.service.RefreshTokenApplicationService;
import kr.modusplant.modules.jwt.app.service.TokenApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class JwtClearingLogoutHandler implements LogoutHandler {

//    private final TokenApplicationService tokenApplicationService;
    private final RefreshTokenApplicationService refreshTokenApplicationService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String refreshToken = request.getHeader("Authorization").substring(7);

        // TODO: DB에서 꺼낸 문자열 값 public key를 사용한다고 가정한다.
        // public key가 PEM format으로 저장되어 있다고 가정한다.
        String pemPublicKey = """
                -----BEGIN PUBLIC KEY-----
                MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu1SU1LfVLPHCozMxH2Mo
                4lgOEePzNm0tRgeLezV6ffAt0gunVTLw7onLRnrq0/IzW7yWR7QkrmBL7jTKEn5u
                +qKhbwKfBstIs+bMY2Zkp18gnTxKLxoS2tFczGkPLPgizskuemMghRniWaoLcyeh
                kd3qqGElvW/VDL5AaWTg0nLVkjRo9z+40RQzuVaE8AkAFmxZzow3x+VJYKdjykkJ
                0iT9wCS0DRTXu269V264Vf/3jvredZiKRkgwlL9xNAwxXFg0x/XFw005UWVRIkdg
                cKWTjpBP2dPwVZ4WWC+9aGVd+Gyn1o0CLelf4rEjGoXbAAEgAqeGUxrcIlbjXfbc
                mwIDAQAB
                -----END PUBLIC KEY-----
                """;
        PublicKey publicKey = getPublicKey(pemPublicKey);

        Jws<Claims> parsedRefreshToken = Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(refreshToken);

        refreshTokenApplicationService.removeByUuid(
                UUID.fromString(parsedRefreshToken.getPayload().getSubject())
        );

    }

    @SneakyThrows
    private PublicKey getPublicKey(String pemPublicKey) {

        String publicKeyString = pemPublicKey
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("\\s", "")
                .trim();

        byte[] keyBites = Decoders.BASE64.decode(publicKeyString);

        return KeyFactory.getInstance("EC")
                .generatePublic(new X509EncodedKeySpec(keyBites));
    }
}
