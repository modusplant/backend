package kr.modusplant.domains.identity.social.domain.vo;

import kr.modusplant.domains.identity.social.common.util.domain.vo.UserPayloadTestUtils;
import kr.modusplant.infrastructure.security.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserPayloadTest implements UserPayloadTestUtils {

    @Test
    @DisplayName("유효한 MemberId, Nickname, Email Role로 UserPayload를 생성")
    void testCreate_givenValidParameters_willReturnUserPayload() {
        // when
        UserPayload userPayload = UserPayload.create(testSocialKakaoMemberId, testSocialKakaoNickname, testKakaoUserEmail, Role.USER);

        // then
        assertNotNull(userPayload);
        assertEquals(testSocialKakaoMemberId, userPayload.getMemberId());
        assertEquals(testSocialKakaoNickname, userPayload.getNickname());
        assertEquals(testKakaoUserEmail, userPayload.getEmail());
        assertEquals(Role.USER, userPayload.getRole());
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        assertEquals(testSocialKakaoUserPayload,testSocialKakaoUserPayload);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        assertNotEquals(testSocialKakaoUserPayload,testSocialKakaoMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 가진 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testSocialKakaoUserPayload, testSocialGoogleUserPayload);
    }

}