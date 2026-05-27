package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.shared.exception.EmptyValueException;
import kr.modusplant.shared.exception.InvalidValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.LocalDateTime;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.EMPTY_REPORT_TIME;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.REPORT_TIME_ON_FUTURE;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportTime {
    private final LocalDateTime value;

    public static ReportTime create(LocalDateTime value) {
        if (value == null) {
            throw new EmptyValueException(EMPTY_REPORT_TIME, "reportTime");
        } else if (value.isAfter(LocalDateTime.now())) {
            throw new InvalidValueException(REPORT_TIME_ON_FUTURE, "reportTime");
        }
        return new ReportTime(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ReportTime reportTime)) return false;

        return new EqualsBuilder().append(getValue(), reportTime.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
