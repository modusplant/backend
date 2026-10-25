package kr.modusplant.domains.post.domain.vo;

import kr.modusplant.domains.post.domain.exception.InvalidValueException;
import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PostLikeCount {
    private final int value;

    public static PostLikeCount zero() {
        return new PostLikeCount(0);
    }

    public static PostLikeCount create(int value) {
        if (value < 0) {
            throw new InvalidValueException(PostErrorCode.INVALID_LIKE_COUNT);
        }
        return new PostLikeCount(value);
    }

    public PostLikeCount increment() {
        return new PostLikeCount(this.value + 1);
    }

    public PostLikeCount decrement() {
        return new PostLikeCount(Math.max(value - 1, 0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof PostLikeCount postLikeCount)) return false;

        return new EqualsBuilder().append(getValue(), postLikeCount.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }

}
