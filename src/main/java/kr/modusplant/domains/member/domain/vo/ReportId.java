package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.framework.jpa.generator.UlidIdGenerator;
import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.generator.EventType;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.EMPTY_REPORT_ID;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.INVALID_REPORT_ID;
import static kr.modusplant.shared.constant.Regex.PATTERN_ULID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportId {
    private final String value;
    private static final UlidIdGenerator generator = new UlidIdGenerator();

    public static ReportId generate() {
        return new ReportId(generator.generate(null, null, null, EventType.INSERT));
    }

    public static ReportId create(String value) {
        if (StringUtils.isBlank(value)) {
            throw new EmptyValueException(EMPTY_REPORT_ID, "reportId");
        } else if (!PATTERN_ULID.matcher(value).matches()) {
            throw new InvalidValueException(INVALID_REPORT_ID, "reportId");
        }
        return new ReportId(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ReportId reportId)) return false;

        return new EqualsBuilder().append(getValue(), reportId.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
