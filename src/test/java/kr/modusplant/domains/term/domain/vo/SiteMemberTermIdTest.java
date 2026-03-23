package kr.modusplant.domains.term.domain.vo;

import kr.modusplant.domains.term.domain.exception.EmptySiteMemberTermIdException;
import kr.modusplant.domains.term.domain.exception.enums.TermErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static kr.modusplant.domains.term.common.util.domain.vo.SiteMemberTermIdTestUtils.testSiteMemberTermId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SiteMemberTermIdTest {

    @Test
    @DisplayName("fromUuid로 SiteMemberTermId 반환")
    void testFromUuid_givenValidValue_willReturnSiteMemberTermId() {
        assertNotNull(SiteMemberTermId.fromUuid(UUID.randomUUID()).getValue());
    }

    @Test
    @DisplayName("null로 fromUuid를 호출하여 오류 발생")
    void testFromUuid_givenNull_willThrowException() {
        EmptySiteMemberTermIdException exception = assertThrows(EmptySiteMemberTermIdException.class, () -> SiteMemberTermId.fromUuid(null));
        assertThat(exception.getErrorCode()).isEqualTo(TermErrorCode.EMPTY_SITE_MEMBER_TERM_ID);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_givenSameObject_willReturnTrue() {
        //noinspection EqualsWithItself
        assertEquals(testSiteMemberTermId, testSiteMemberTermId);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testSiteMemberTermId, "notASiteMemberTermId");
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void useEqual_givenObjectContainingDifferentProperty_willReturnFalse() {
        assertNotEquals(testSiteMemberTermId, SiteMemberTermId.fromUuid(UUID.randomUUID()));
    }
}
