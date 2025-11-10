package kr.modusplant.domains.normalidentity.normal.domain.vo;

import kr.modusplant.domains.normalidentity.normal.domain.exception.EmptyValueException;
import kr.modusplant.domains.normalidentity.normal.domain.exception.InvalidValueException;
import kr.modusplant.domains.normalidentity.normal.domain.exception.enums.IdentityErrorCode;
import kr.modusplant.shared.constant.Regex;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AgreedTermsOfVersion {
    private final String version;

    public static AgreedTermsOfVersion create(String version) {
        AgreedTermsOfVersion.validateSource(version);
        return new AgreedTermsOfVersion(version);
    }

    public static void validateSource(String input) {
        if(input == null || input.isBlank()) { throw new EmptyValueException(IdentityErrorCode.EMPTY_AGREED_TERMS_OF_VERSION); }
        if(!input.matches(Regex.REGEX_VERSION)) {
            throw new InvalidValueException(IdentityErrorCode.INVALID_AGREED_TERMS_OF_VERSION);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof AgreedTermsOfVersion agreed)) return false;

        return new EqualsBuilder()
                .append(getVersion(), agreed.getVersion())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getVersion()).toHashCode();
    }
}
