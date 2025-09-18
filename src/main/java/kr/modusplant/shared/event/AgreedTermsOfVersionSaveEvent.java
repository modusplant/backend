package kr.modusplant.shared.event;

import kr.modusplant.domains.comment.domain.exception.EmptyValueException;
import kr.modusplant.domains.comment.domain.exception.InvalidValueException;
import kr.modusplant.domains.identity.domain.constant.IdentityDataFormat;
import kr.modusplant.domains.identity.domain.exception.enums.IdentityErrorCode;
import kr.modusplant.domains.identity.domain.vo.AgreedTermsOfVersion;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AgreedTermsOfVersionSaveEvent {
    private final UUID memberActiveUuid;
    private final String agreedTermsOfUseVersion;
    private final String agreedPrivacyPolicyVersion;
    private final String agreedAdInfoReceivingVersion;

    public static AgreedTermsOfVersionSaveEvent create(UUID memberActiveUuid,
                                                       String agreedTermsOfUseVersion,
                                                       String agreedPrivacyPolicyVersion,
                                                       String agreedAdInfoReceivingVersion) {
        validateVersion(agreedTermsOfUseVersion);
        validateVersion(agreedPrivacyPolicyVersion);
        validateVersion(agreedAdInfoReceivingVersion);
        return new AgreedTermsOfVersionSaveEvent(memberActiveUuid,
                agreedTermsOfUseVersion,
                agreedPrivacyPolicyVersion,
                agreedAdInfoReceivingVersion);
    }

    private static void validateVersion(String input) {
        if(input.isBlank()) { throw new EmptyValueException(IdentityErrorCode.EMPTY_AGREED_TERMS_OF_VERSION); }
        if(input.matches(IdentityDataFormat.AGREED_TERMS_OF_VERSION_FORMAT)) {
            throw new InvalidValueException(IdentityErrorCode.INVALID_AGREED_TERMS_OF_VERSION);
        }
    }
}
