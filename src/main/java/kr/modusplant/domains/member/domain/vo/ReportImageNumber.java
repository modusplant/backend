package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.shared.exception.EmptyValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.EMPTY_REPORT_IMAGE_NUMBER;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportImageNumber {
    private final Integer value;

    public static ReportImageNumber create(Integer value) {
        if (value == null) {
            throw new EmptyValueException(EMPTY_REPORT_IMAGE_NUMBER, "reportImageNumber");
        }
        return new ReportImageNumber(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ReportImageNumber reportImageNumber)) return false;

        return new EqualsBuilder().append(getValue(), reportImageNumber.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
