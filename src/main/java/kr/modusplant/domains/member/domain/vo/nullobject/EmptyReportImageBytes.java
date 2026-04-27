package kr.modusplant.domains.member.domain.vo.nullobject;

import kr.modusplant.domains.member.domain.vo.ReportImageBytes;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmptyReportImageBytes extends ReportImageBytes {

    public static EmptyReportImageBytes create() {
        return new EmptyReportImageBytes();
    }

    @Override
    public byte[] getValue() {
        return null;
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
