package kr.modusplant.domains.account.normal.domain.vo;

import kr.modusplant.domains.account.normal.common.util.domain.vo.AgreedTermVersionTestUtils;
import kr.modusplant.domains.account.normal.domain.exception.EmptyValueException;
import kr.modusplant.domains.account.normal.domain.exception.InvalidValueException;
import kr.modusplant.domains.account.normal.domain.exception.enums.NormalIdentityErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AgreedTermVersionTest implements AgreedTermVersionTestUtils {

    @Test
    @DisplayName("null로 동의된 약관 버전 생성")
    public void testCreate_givenNullVersion_willThrowEmptyValueException() {
        // given
        EmptyValueException result = assertThrows(EmptyValueException.class, () ->
                AgreedTermVersion.create(null));

        // when & then
        assertEquals(NormalIdentityErrorCode.EMPTY_AGREED_TERMS_OF_VERSION, result.getErrorCode());
    }

    @Test
    @DisplayName("형식에 맞지 않는 값으로 동의된 약관 버전 생성")
    public void testCreate_givenInvalidVersionFormat_willThrowInvalidValueException() {
        // given
        InvalidValueException result = assertThrows(InvalidValueException.class, () ->
                AgreedTermVersion.create("va11223"));

        // when & then
        assertEquals(NormalIdentityErrorCode.INVALID_AGREED_TERMS_OF_VERSION, result.getErrorCode());
    }

    @Test
    @DisplayName("동일한 객체로 동등성 비교")
    void testEquals_givenSameObject_willReturnTrue() {
        // given
        AgreedTermVersion version = testAgreedTermsOfUse;

        // when & then
        assertEquals(version, version);
    }

    @Test
    @DisplayName("다른 객체로 동등성 비교")
    void testEquals_givenDifferentObject_willReturnFalse() {
        // given & when & then
        RuntimeException different = new RuntimeException();
        assertNotEquals(testAgreedTermsOfUse, different);
    }

    @Test
    @DisplayName("동일하고 다른 프로퍼티를 지닌 객체로 동등성 비교")
    void testEquals_givenDifferentProperty_willReturnFalse() {
        // given & when & then
        assertNotEquals(testAgreedTermsOfUse, testAgreedAdReceiving);
    }

}
