package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.vo.enums.ReportCategoryType;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.EMPTY_REPORT_CONTENT;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.INVALID_REPORT_CATEGORY;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportCategory {
    private final ReportCategoryType type;

    public static ReportCategory create(String value) {
        if (StringUtils.isBlank(value)) {
            throw new EmptyValueException(EMPTY_REPORT_CONTENT, "reportCategory");
        } else if (!value.equals("proposal") && !value.equals("bugReport")) {
            throw new InvalidValueException(INVALID_REPORT_CATEGORY, "reportCategory");
        }
        if (value.equals("proposal")) {
            return new ReportCategory(ReportCategoryType.PROPOSAL);
        } else {
            return new ReportCategory(ReportCategoryType.BUG_REPORT);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ReportCategory reportCategory)) return false;

        return new EqualsBuilder().append(getType(), reportCategory.getType()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getType()).toHashCode();
    }
}
