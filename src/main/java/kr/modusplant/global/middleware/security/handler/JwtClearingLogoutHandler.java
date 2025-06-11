package kr.modusplant.global.middleware.security.handler;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.modusplant.modules.jwt.app.service.RefreshTokenApplicationService;
import kr.modusplant.modules.jwt.persistence.entity.RefreshTokenEntity;
import kr.modusplant.modules.jwt.persistence.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;

@RequiredArgsConstructor
public class JwtClearingLogoutHandler implements LogoutHandler {

    private final RefreshTokenRepository tokenRepository;
    private final RefreshTokenApplicationService tokenApplicationService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String refreshTokenFromClient = request.getHeader("Authorization").substring(7);

        // TODO: DB에서 가져온 리프레시 토큰 엔티티에서 public key를 가져올 수 있다고 가정한다.
        RefreshTokenEntity refreshTokenEntity = tokenRepository.findByRefreshToken(refreshTokenFromClient)
                .orElseThrow(() -> new EntityNotFoundException("no refresh token with string token"));

        // public key가 PEM format으로 저장되어 있다고 가정한다.
        // 리프레시 토큰 생성 시 사용된 public key가 저장되는 방식에 따라 로직이 변경될 수 있다.
        // String pemPublicKey = refreshTokenEntity.getPublicKey();
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

        Jws<Claims> parsedRefreshToken = Jwts.parser()
                .verifyWith(getPublicKey(pemPublicKey))
                .build()
                .parseSignedClaims(refreshTokenFromClient);

        tokenApplicationService.removeByUuid(
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
