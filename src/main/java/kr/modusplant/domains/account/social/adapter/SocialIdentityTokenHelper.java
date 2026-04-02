package kr.modusplant.domains.account.social.adapter;

import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
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

    public String generateTempToken(String email, String providerId, SocialProvider socialProvider, String socialAccessToken,long durationMs) {
        Map<String,String> claims = new HashMap<>();
        claims.put("email",email);
        claims.put("providerId",providerId);
        claims.put("socialProvider",socialProvider.name());
        claims.put("socialAccessToken",socialAccessToken);
        return jwtTokenProvider.generateToken(providerId,claims,durationMs);
    }




}
