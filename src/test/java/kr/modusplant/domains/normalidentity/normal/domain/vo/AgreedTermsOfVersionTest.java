package kr.modusplant.domains.normalidentity.normal.domain.vo;

import kr.modusplant.domains.identity.normal.domain.vo.AgreedTermsOfVersion;
import kr.modusplant.domains.normalidentity.normal.common.util.domain.vo.AgreedTermsOfVersionTestUtils;
import kr.modusplant.domains.identity.normal.domain.exception.EmptyValueException;
import kr.modusplant.domains.identity.normal.domain.exception.InvalidValueException;
import kr.modusplant.domains.identity.normal.domain.exception.enums.IdentityErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AgreedTermsOfVersionTest implements AgreedTermsOfVersionTestUtils {

    @Test
    @DisplayName("null인 버전 값으로 동의된 약관 버전 생성")
    public void testCreate_givenNullVersion_willThrowEmptyValueException() {
        // given
        EmptyValueException result = assertThrows(EmptyValueException.class, () ->
                AgreedTermsOfVersion.create(null));

        // when & then
        assertEquals(IdentityErrorCode.EMPTY_AGREED_TERMS_OF_VERSION, result.getErrorCode());
    }

    @Test
    @DisplayName("형식에 맞지 않는 버전 값으로 동의된 약관 버전 생성")
    public void testCreate_givenInvalidVersionFormat_willThrowInvalidValueException() {
        // given
        InvalidValueException result = assertThrows(InvalidValueException.class, () ->
                AgreedTermsOfVersion.create("va11223"));

        // when & then
        assertEquals(IdentityErrorCode.INVALID_AGREED_TERMS_OF_VERSION, result.getErrorCode());
    }

    @Test
    @DisplayName("동일한 객체로 동등성 비교")
    void testEquals_givenSameObject_willReturnTrue() {
        // given
        AgreedTermsOfVersion version = testAgreedTermsOfVersion;

        // when & then
        assertEquals(version, version);
    }

    @Test
    @DisplayName("다른 객체로 동등성 비교")
    void testEquals_givenDifferentObject_willReturnFalse() {
        EmptyValueException different = new EmptyValueException(IdentityErrorCode.EMPTY_NICKNAME);
        assertNotEquals(testAgreedTermsOfVersion, different);
    }

    @Test
    @DisplayName("동일하고 다른 프로퍼티를 지닌 객체로 동등성 비교")
    void testEquals_givenDifferentProperty_willReturnFalse() {
        // given
        AgreedTermsOfVersion version = AgreedTermsOfVersion.create("v9.3.2");

        assertNotEquals(testAgreedTermsOfVersion, version);
    }

}
