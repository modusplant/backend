package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.EMPTY_REPORT_TITLE;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.REPORT_TITLE_OVER_LENGTH;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportTitle {
    private final String value;

    public static ReportTitle create(String value) {
        if (StringUtils.isBlank(value)) {
            throw new EmptyValueException(EMPTY_REPORT_TITLE, "reportTitle");
        } else if (value.length() > 60) {
            throw new InvalidValueException(REPORT_TITLE_OVER_LENGTH, "reportTitle");
        }
        return new ReportTitle(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ReportTitle reportTitle)) return false;

        return new EqualsBuilder().append(getValue(), reportTitle.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
