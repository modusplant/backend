package kr.modusplant.domains.member.framework.outbound.jpa.compositekey;

import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class CommentAbuseReportDashboardCompositeKey implements Serializable {
    private final String postUlid;
    private final String path;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof CommentAbuseReportDashboardCompositeKey that)) return false;

        return new EqualsBuilder()
                .append(getPostUlid(), that.getPostUlid())
                .append(getPath(), that.getPath())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getPostUlid())
                .append(getPath())
                .toHashCode();
    }
}
