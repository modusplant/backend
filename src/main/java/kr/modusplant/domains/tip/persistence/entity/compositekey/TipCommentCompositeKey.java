package kr.modusplant.domains.tip.persistence.entity.compositekey;

import kr.modusplant.domains.tip.persistence.entity.TipCommentEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
public class TipCommentCompositeKey implements Serializable {

    private final String postUlid;
    private final String materializedPath;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TipCommentEntity that)) return false;

        return new EqualsBuilder()
                .append(getPostUlid(), that.getPostUlid())
                .append(getMaterializedPath(), that.getMaterializedPath())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getPostUlid())
                .append(getMaterializedPath())
                .toHashCode();
    }
}
