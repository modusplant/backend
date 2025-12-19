package kr.modusplant.domains.account.normal.domain.vo;

import kr.modusplant.domains.account.normal.domain.exception.EmptyValueException;
import kr.modusplant.domains.account.normal.domain.exception.InvalidValueException;
import kr.modusplant.domains.account.normal.domain.exception.enums.NormalIdentityErrorCode;
import kr.modusplant.shared.constant.Regex;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AgreedTermVersion {
    private final String value;

    public static AgreedTermVersion create(String version) {
        AgreedTermVersion.validateSource(version);
        return new AgreedTermVersion(version);
    }

    public static void validateSource(String input) {
        if(input == null || input.isBlank()) { throw new EmptyValueException(NormalIdentityErrorCode.EMPTY_AGREED_TERMS_OF_VERSION); }
        if(!input.matches(Regex.REGEX_VERSION)) {
            throw new InvalidValueException(NormalIdentityErrorCode.INVALID_AGREED_TERMS_OF_VERSION);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof AgreedTermVersion agreed)) return false;

        return new EqualsBuilder()
                .append(getValue(), agreed.getValue())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getValue()).toHashCode();
    }
}
