package kr.modusplant.domains.term.domain.aggregate;

import kr.modusplant.domains.term.common.util.domain.aggregate.TermTestUtils;
import kr.modusplant.domains.term.domain.vo.TermContent;
import kr.modusplant.domains.term.domain.vo.TermName;
import kr.modusplant.domains.term.domain.vo.TermVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.term.common.constant.TermConstant.*;
import static kr.modusplant.domains.term.common.util.domain.vo.TermIdTestUtils.testTermId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TermTest implements TermTestUtils {

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        Term term = createTerm();

        //noinspection EqualsWithItself
        assertEquals(term, term);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectOfDifferentClass_willReturnFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(testTermId, createTerm());
    }

    @Test
    @DisplayName("식별자가 없어도 같은 약관명을 갖는 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingSameTermNameWithoutId_willReturnTrue() {
        Term newTerm = Term.create(
                TermName.create(TEST_TERMS_OF_USE_NAME),
                TermContent.create(TEST_TERMS_OF_USE_CONTENT),
                TermVersion.create(TEST_TERMS_OF_USE_VERSION)
        );

        assertEquals(createTerm(), newTerm);
    }

    @Test
    @DisplayName("다른 약관명을 갖는 인스턴스에 대한 equals 호출")
    void testEquals_givenObjectContainingDifferentTermName_willReturnFalse() {
        Term otherTerm = Term.create(
                TermName.create(TEST_TERMS_OF_USE_NAME + "2"),
                TermContent.create(TEST_TERMS_OF_USE_CONTENT),
                TermVersion.create(TEST_TERMS_OF_USE_VERSION)
        );

        assertNotEquals(createTerm(), otherTerm);
    }

    @Test
    @DisplayName("같은 약관명을 갖는 인스턴스에 대한 hashcode 동일성 보장")
    void testHashCode_givenObjectContainingSameTermName_willReturnSameHashCode() {
        assertEquals(createTerm().hashCode(), createTerm().hashCode());
    }
}
