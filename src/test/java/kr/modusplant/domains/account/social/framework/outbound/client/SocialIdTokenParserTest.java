package kr.modusplant.domains.account.social.framework.outbound.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.modusplant.domains.account.social.domain.vo.enums.SocialProvider;
import kr.modusplant.domains.account.social.framework.outbound.client.dto.IdTokenInfo;
import kr.modusplant.shared.exception.InvalidValueException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SocialIdTokenParserTest {
    private SocialIdTokenParser socialIdTokenParser;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        socialIdTokenParser = new SocialIdTokenParser(objectMapper);
    }

    private String createIdToken(Map<String, Object> claims) throws JsonProcessingException {
        String header = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"alg\":\"RS256\"}".getBytes());
        String payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(objectMapper.writeValueAsString(claims).getBytes());
        return header + "." + payload + ".signature";
    }

    @Test
    @DisplayName("카카오 id_token 파싱 성공")
    void testParse_givenKakaoIdToken_willReturnIdTokenInfo() throws JsonProcessingException {
        // given
        Map<String, Object> claims = Map.of(
                "sub", "12345",
                "email", "test@kakao.com",
                "nickname", "kakaoNickname"
        );
        String idToken = createIdToken(claims);

        // when
        IdTokenInfo result = socialIdTokenParser.parse(idToken, SocialProvider.KAKAO);

        // then
        assertThat(result.id()).isEqualTo("12345");
        assertThat(result.email()).isEqualTo("test@kakao.com");
        assertThat(result.nickname()).isEqualTo("kakaoNickname");
    }

    @Test
    @DisplayName("구글 id_token 파싱 성공")
    void testParse_givenGoogleIdToken_willReturnIdTokenInfo() throws JsonProcessingException {
        // given
        Map<String, Object> claims = Map.of(
                "sub", "67890",
                "email", "test@google.com",
                "name", "googleName"
        );
        String idToken = createIdToken(claims);

        // when
        IdTokenInfo result = socialIdTokenParser.parse(idToken, SocialProvider.GOOGLE);

        // then
        assertThat(result.id()).isEqualTo("67890");
        assertThat(result.email()).isEqualTo("test@google.com");
        assertThat(result.nickname()).isEqualTo("googleName");
    }

    @Test
    @DisplayName("잘못된 형식의 id_token 파싱 시 예외 발생")
    void testParse_givenInvalidIdToken_willThrowException() {
        // given
        String invalidIdToken = "invalid-token";

        // when & then
        assertThatThrownBy(() -> socialIdTokenParser.parse(invalidIdToken, SocialProvider.KAKAO))
                .isInstanceOf(InvalidValueException.class);
    }



}