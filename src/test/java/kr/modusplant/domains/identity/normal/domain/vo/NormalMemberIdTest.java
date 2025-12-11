package kr.modusplant.domains.identity.normal.domain.vo;

import kr.modusplant.domains.identity.normal.common.util.domain.vo.MemberIdTestUtils;
import kr.modusplant.domains.identity.normal.domain.exception.EmptyValueException;
import kr.modusplant.domains.identity.normal.domain.exception.enums.NormalIdentityErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberAuthConstant.MEMBER_AUTH_BASIC_ADMIN_UUID;
import static org.junit.jupiter.api.Assertions.*;

public class NormalMemberIdTest implements MemberIdTestUtils {

    @Test
    @DisplayName("null로 사용자 아이디 생성")
    public void testCreate_givenNullVersion_willThrowEmptyValueException() {
        // given
        EmptyValueException result = assertThrows(EmptyValueException.class, () ->
                NormalMemberId.create(null));

        // when & then
        assertEquals(NormalIdentityErrorCode.EMPTY_MEMBER_ID, result.getErrorCode());
    }

    @Test
    @DisplayName("동일한 객체로 동등성 비교")
    void testEquals_givenSameObject_willReturnTrue() {
        // given
        NormalMemberId id = TEST_NORMAL_MEMBER_ID;

        // when & then
        assertEquals(id, TEST_NORMAL_MEMBER_ID);
    }

    @Test
    @DisplayName("다른 객체로 동등성 비교")
    void testEquals_givenDifferentObject_willReturnFalse() {
        // given & when & then
        EmptyValueException different = new EmptyValueException(NormalIdentityErrorCode.EMPTY_NICKNAME);
        assertNotEquals(TEST_NORMAL_MEMBER_ID, different);
    }

    @Test
    @DisplayName("동일하고 다른 프로퍼티를 지닌 객체로 동등성 비교")
    void testEquals_givenDifferentProperty_willReturnFalse() {
        // given
        NormalMemberId different = NormalMemberId.create(MEMBER_AUTH_BASIC_ADMIN_UUID);

        // when & then
        assertNotEquals(TEST_NORMAL_MEMBER_ID, different);
    }

}
