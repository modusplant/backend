package kr.modusplant.domains.comment.framework.outbound.persistence.jpa.compositekey;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class CommentCompositeKey implements Serializable {
    private final String post;
    private final String path;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommentCompositeKey that)) return false;

        return new EqualsBuilder()
                .append(getPost(), that.getPost())
                .append(getPath(), that.getPath())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getPost())
                .append(getPath())
                .toHashCode();
    }

    public static CommentCompositeKeyBuilder builder() {
        return new CommentCompositeKeyBuilder();
    }

    public static final class CommentCompositeKeyBuilder {
        private String post;
        private String path;

        public CommentCompositeKeyBuilder post(final String post) {
            this.post = post;
            return this;
        }

        public CommentCompositeKeyBuilder path(final String path) {
            this.path = path;
            return this;
        }

        public CommentCompositeKeyBuilder commentCompositeKey(final CommentCompositeKey compositeKey) {
            this.post = compositeKey.getPost();
            this.path = compositeKey.getPath();
            return this;
        }

        public CommentCompositeKey build() {
            return new CommentCompositeKey(this.post, this.path);
        }
    }
}
