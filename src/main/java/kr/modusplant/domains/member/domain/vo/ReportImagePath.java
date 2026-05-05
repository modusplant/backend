package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.regex.Pattern;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.EMPTY_REPORT_IMAGE_PATH;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.INVALID_REPORT_IMAGE_PATH;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportImagePath {
    private final String value;
    private static final Pattern PATTERN_REPORT_IMAGE_PATH = Pattern.compile(
            "^member/[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}/report/.+\\..+$");

    public static ReportImagePath create(String value) {
        if (StringUtils.isBlank(value)) {
            throw new EmptyValueException(EMPTY_REPORT_IMAGE_PATH, "reportImagePath");
        } else if (!PATTERN_REPORT_IMAGE_PATH.matcher(value).matches()) {
            throw new InvalidValueException(INVALID_REPORT_IMAGE_PATH, "reportImagePath");
        }
        return new ReportImagePath(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ReportImagePath reportImagePath)) return false;

        return new EqualsBuilder().append(getValue(), reportImagePath.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
