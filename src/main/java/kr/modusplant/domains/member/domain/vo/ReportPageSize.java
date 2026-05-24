package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.EMPTY_REPORT_PAGE_SIZE;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.REPORT_PAGE_SIZE_OUT_OF_RANGE;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportPageSize {
    private final Integer value;

    public static ReportPageSize create(Integer value) {
        if (value == null) {
            throw new EmptyValueException(EMPTY_REPORT_PAGE_SIZE, "reportPageSize");
        } else if (value <= 0 || value > 50) {
            throw new InvalidValueException(REPORT_PAGE_SIZE_OUT_OF_RANGE, "reportPageSize");
        }
        return new ReportPageSize(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ReportPageSize reportPageSize)) return false;

        return new EqualsBuilder().append(getValue(), reportPageSize.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
