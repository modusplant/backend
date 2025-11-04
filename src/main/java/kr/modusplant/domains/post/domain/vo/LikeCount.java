package kr.modusplant.domains.post.domain.vo;

import kr.modusplant.domains.post.domain.exception.InvalidLikeCountException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class LikeCount {
    private final int value;

    public static LikeCount zero() {
        return new LikeCount(0);
    }

    public static LikeCount create(int value) {
        if (value < 0) {
            throw new InvalidLikeCountException();
        }
        return new LikeCount(value);
    }

    public LikeCount increment() {
        return new LikeCount(this.value + 1);
    }

    public LikeCount decrement() {
        return new LikeCount(Math.max(value - 1, 0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof LikeCount likeCount)) return false;

        return new EqualsBuilder().append(getValue(), likeCount.getValue()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getValue()).toHashCode();
    }

}
