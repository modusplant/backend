package kr.modusplant.domains.comment.domain.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostId {
    private final String id;

    public static PostId create(String ulid) {
        return new PostId(ulid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof PostId postId)) return false;

        return new EqualsBuilder()
                .append(getId(), postId.getId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId()).toHashCode();
    }
}
