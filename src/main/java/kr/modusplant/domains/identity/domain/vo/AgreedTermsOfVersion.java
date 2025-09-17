package kr.modusplant.domains.identity.domain.vo;

import kr.modusplant.domains.comment.domain.exception.EmptyValueException;
import kr.modusplant.domains.comment.domain.exception.InvalidValueException;
import kr.modusplant.domains.identity.domain.constant.IdentityDataFormat;
import kr.modusplant.domains.identity.domain.exception.enums.IdentityErrorCode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AgreedTermsOfVersion {
    private final String version;

    public static AgreedTermsOfVersion create(String version) {
        AgreedTermsOfVersion.validateSource(version);
        return new AgreedTermsOfVersion(version);
    }

    public static void validateSource(String input) {
        if(input.isBlank()) { throw new EmptyValueException(IdentityErrorCode.EMPTY_AGREED_TERMS_OF_VERSION); }
        if(input.matches(IdentityDataFormat.AGREED_TERMS_OF_VERSION_FORMAT)) {
            throw new InvalidValueException(IdentityErrorCode.INVALID_AGREED_TERMS_OF_VERSION);
        }
    }
}
