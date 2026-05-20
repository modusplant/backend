package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.vo.nullobject.EmptyReportImageBytes;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportImageBytes {
    private final byte[] value;

    public static ReportImageBytes create(byte[] value) {
        if (value == null) {
            return EmptyReportImageBytes.create();
        }
        return new ReportImageBytes(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ReportImageBytes reportImageBytes)) return false;

        return new EqualsBuilder().append(getValue(), reportImageBytes.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }
}
