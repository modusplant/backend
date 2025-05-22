package kr.modusplant.domains.tip.persistence.entity.compositekey;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@Getter
public class TipCommentCompositeKey implements Serializable {

    private final String postUlid;
    private final String materializedPath;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TipCommentCompositeKey that)) return false;

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

    public static TipCommentCompositeKeyBuilder builder() {
        return new TipCommentCompositeKeyBuilder();
    }

    public static final class TipCommentCompositeKeyBuilder {
        private String postUlid;
        private String materializedPath;

        public TipCommentCompositeKeyBuilder postUlid(final String postUlid) {
            this.postUlid = postUlid;
            return this;
        }

        public TipCommentCompositeKeyBuilder materializedPath(final String materializedPath) {
            this.materializedPath = materializedPath;
            return this;
        }

        public TipCommentCompositeKeyBuilder TipCommentCompositeKey(final TipCommentCompositeKey compositeKey) {
            this.postUlid = compositeKey.postUlid;
            this.materializedPath = compositeKey.getMaterializedPath();
            return this;
        }

        public TipCommentCompositeKey build() {
            return new TipCommentCompositeKey(this.postUlid, this.materializedPath);
        }
    }
}
