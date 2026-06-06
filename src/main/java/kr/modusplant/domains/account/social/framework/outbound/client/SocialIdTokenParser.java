package kr.modusplant.domains.account.social.framework.outbound.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.account.social.domain.exception.enums.SocialIdentityErrorCode;
import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.framework.outbound.client.dto.IdTokenInfo;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SocialIdTokenParser {

    private final ObjectMapper objectMapper;

    public IdTokenInfo parse(String idToken, SocialProvider provider) {
        Map<String, Object> claims = decodeClaims(idToken);

        String id = (String) claims.get("sub");
        String email = (String) claims.get("email");
        String nickname = extractNickname(claims,provider);

        return new IdTokenInfo(id,email,nickname);
    }

    private Map<String,Object> decodeClaims(String idToken) {
        try {
            String payload = idToken.split("\\.")[1];
            String decoded = new String(Base64.getUrlDecoder().decode(payload));
            return objectMapper.readValue(decoded, Map.class);
        } catch (Exception e) {
            throw new InvalidValueException(SocialIdentityErrorCode.INVALID_SOCIAL_ID_TOKEN, "socialIdToken");
        }
    }

    private String extractNickname(Map<String, Object> claims, SocialProvider provider) {
        return switch (provider) {
            case KAKAO -> (String) claims.get("nickname");
            case GOOGLE -> (String)  claims.get("name");
        };
    }

}
