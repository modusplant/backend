package kr.modusplant.domains.identity.social.domain.vo;

import kr.modusplant.domains.identity.social.common.util.domain.vo.UserPayloadTestUtils;
import kr.modusplant.infrastructure.security.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SocialAccountPayloadTest implements UserPayloadTestUtils {

    @Test
    @DisplayName("유효한 MemberId, Nickname, Email Role로 UserPayload를 생성")
    void testCreate_givenValidParameters_willReturnUserPayload() {
        // when
        SocialAccountPayload socialAccountPayload = SocialAccountPayload.create(testSocialKakaoMemberId, testKakaoUserNickname, testKakaoUserEmail, Role.USER);

        // then
        assertNotNull(socialAccountPayload);
        assertEquals(testSocialKakaoMemberId, socialAccountPayload.getMemberId());
        assertEquals(testKakaoUserNickname, socialAccountPayload.getNickname());
        assertEquals(testKakaoUserEmail, socialAccountPayload.getEmail());
        assertEquals(Role.USER, socialAccountPayload.getRole());
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        assertEquals(TEST_SOCIAL_KAKAO_SOCIAL_ACCOUNT_PAYLOAD, TEST_SOCIAL_KAKAO_SOCIAL_ACCOUNT_PAYLOAD);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        assertNotEquals(TEST_SOCIAL_KAKAO_SOCIAL_ACCOUNT_PAYLOAD,testSocialKakaoMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 가진 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(TEST_SOCIAL_KAKAO_SOCIAL_ACCOUNT_PAYLOAD, TEST_SOCIAL_GOOGLE_SOCIAL_ACCOUNT_PAYLOAD);
    }

}