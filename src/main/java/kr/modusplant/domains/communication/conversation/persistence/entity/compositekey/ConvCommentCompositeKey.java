package kr.modusplant.domains.communication.conversation.persistence.entity.compositekey;

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
public class ConvCommentCompositeKey implements Serializable {

    private final String postUlid;
    private final String path;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConvCommentCompositeKey that)) return false;

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

    public static ConvCommentCompositeKeyBuilder builder() {
        return new ConvCommentCompositeKeyBuilder();
    }

    public static final class ConvCommentCompositeKeyBuilder {
        private String postUlid;
        private String path;

        public ConvCommentCompositeKeyBuilder postUlid(final String postUlid) {
            this.postUlid = postUlid;
            return this;
        }

        public ConvCommentCompositeKeyBuilder path(final String path) {
            this.path = path;
            return this;
        }

        public ConvCommentCompositeKeyBuilder ConvCommentCompositeKey(final ConvCommentCompositeKey compositeKey) {
            this.postUlid = compositeKey.postUlid;
            this.path = compositeKey.getPath();
            return this;
        }

        public ConvCommentCompositeKey build() {
            return new ConvCommentCompositeKey(this.postUlid, this.path);
        }
    }
}
