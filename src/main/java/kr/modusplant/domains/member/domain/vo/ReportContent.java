package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.EMPTY_REPORT_CONTENT;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.REPORT_CONTENT_OVER_LENGTH;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportContent {
    private final String value;

    public static ReportContent create(String value) {
        if (StringUtils.isBlank(value)) {
            throw new EmptyValueException(EMPTY_REPORT_CONTENT, "reportContent");
        } else if (value.length() > 600) {
            throw new InvalidValueException(REPORT_CONTENT_OVER_LENGTH, "reportContent");
        }
        return new ReportContent(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ReportContent reportContent)) return false;

        return new EqualsBuilder().append(getValue(), reportContent.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
