package kr.modusplant.domains.comment.framework.out.persistence.jpa.compositekey;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

import static kr.modusplant.shared.persistence.constant.TableColumnName.PATH;

@AllArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class CommentCompositeKey implements Serializable {

    private final String postUlid;

    @Column(name = PATH, nullable = false, updatable = false)
    private final String path;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommentCompositeKey that)) return false;

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

    public static CommentCompositeKey.CommentCompositeKeyBuilder builder() {
        return new CommentCompositeKey.CommentCompositeKeyBuilder();
    }

    public static final class CommentCompositeKeyBuilder {
        private String postUlid;
        private String path;

        public CommentCompositeKey.CommentCompositeKeyBuilder postUlid(final String postUlid) {
            this.postUlid = postUlid;
            return this;
        }

        public CommentCompositeKey.CommentCompositeKeyBuilder path(final String path) {
            this.path = path;
            return this;
        }

        public CommentCompositeKey.CommentCompositeKeyBuilder CommentCompositeKey(final CommentCompositeKey compositeKey) {
            this.postUlid = compositeKey.postUlid;
            this.path = compositeKey.getPath();
            return this;
        }

        public CommentCompositeKey build() {
            return new CommentCompositeKey(this.postUlid, this.path);
        }
    }
}
