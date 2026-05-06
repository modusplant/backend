package kr.modusplant.domains.account.social.adapter;

import io.jsonwebtoken.Claims;
import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.usecase.record.SocialPendingResult;
import kr.modusplant.domains.account.social.usecase.record.TempTokenInfo;
import kr.modusplant.infrastructure.jwt.provider.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialIdentityTokenHelper {

    private final JwtTokenProvider jwtTokenProvider;

    public String generateTempToken(SocialPendingResult pendingResult, long durationMs) {
        Map<String,String> claims = new HashMap<>();
        claims.put("email",pendingResult.email());
        claims.put("providerId",pendingResult.providerId());
        claims.put("socialProvider",pendingResult.socialProvider().name());
        claims.put("socialAccessToken",pendingResult.socialAccessToken());
        return jwtTokenProvider.generateToken(pendingResult.providerId(),claims,durationMs);
    }

    public TempTokenInfo getTempTokenInfoFromClaims(String tempToken) {
        Claims claims = jwtTokenProvider.getClaimsFromToken(tempToken);
        String provider = claims.get("socialProvider", String.class);
        return new TempTokenInfo(
                claims.get("email",String.class),
                claims.get("providerId",String.class),
                SocialProvider.valueOf(provider)
        );
    }

    public String getSocialAccessTokenFromClaims(String tempToken) {
        Claims claims = jwtTokenProvider.getClaimsFromToken(tempToken);
        return claims.get("socialAccessToken",String.class);
    }

    public SocialProvider getSocialProviderFromClaims(String tempToken) {
        Claims claims = jwtTokenProvider.getClaimsFromToken(tempToken);
        String provider = claims.get("socialProvider", String.class);
        return SocialProvider.valueOf(provider);
    }


}
