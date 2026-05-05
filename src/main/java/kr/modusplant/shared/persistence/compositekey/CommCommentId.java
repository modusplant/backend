package kr.modusplant.shared.persistence.compositekey;

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
public class CommCommentId implements Serializable {
    private final String post;
    private final String path;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommCommentId that)) return false;

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

    public static CommCommCommentIdBuilder builder() {
        return new CommCommCommentIdBuilder();
    }

    public static final class CommCommCommentIdBuilder {
        private String post;
        private String path;

        public CommCommCommentIdBuilder post(final String post) {
            this.post = post;
            return this;
        }

        public CommCommCommentIdBuilder path(final String path) {
            this.path = path;
            return this;
        }

        public CommCommCommentIdBuilder commCommCommentId(final CommCommentId compositeKey) {
            this.post = compositeKey.getPost();
            this.path = compositeKey.getPath();
            return this;
        }

        public CommCommentId build() {
            return new CommCommentId(this.post, this.path);
        }
    }
}
