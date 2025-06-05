package kr.modusplant.domains.communication.qna.persistence.entity.compositekey;

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
public class QnaCommentCompositeKey implements Serializable {

    private final String postUlid;
    private final String path;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QnaCommentCompositeKey that)) return false;

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

    public static QnaCommentCompositeKeyBuilder builder() {
        return new QnaCommentCompositeKeyBuilder();
    }

    public static final class QnaCommentCompositeKeyBuilder {
        private String postUlid;
        private String path;

        public QnaCommentCompositeKeyBuilder postUlid(final String postUlid) {
            this.postUlid = postUlid;
            return this;
        }

        public QnaCommentCompositeKeyBuilder path(final String path) {
            this.path = path;
            return this;
        }

        public QnaCommentCompositeKeyBuilder QnaCommentCompositeKey(final QnaCommentCompositeKey compositeKey) {
            this.postUlid = compositeKey.postUlid;
            this.path = compositeKey.getPath();
            return this;
        }

        public QnaCommentCompositeKey build() {
            return new QnaCommentCompositeKey(this.postUlid, this.path);
        }
    }
}
