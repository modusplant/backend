package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.domains.member.domain.exception.EmptyTargetCommentPathException;
import kr.modusplant.domains.member.domain.exception.EmptyTargetPostIdException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TargetCommentId {
    private final TargetPostId targetPostId;
    private final TargetCommentPath targetCommentPath;

    public static TargetCommentId create(TargetPostId targetPostId, TargetCommentPath targetCommentPath) {
        if (targetPostId == null) {
            throw new EmptyTargetPostIdException();
        } else if (targetCommentPath == null) {
            throw new EmptyTargetCommentPathException();
        }
        return new TargetCommentId(targetPostId, targetCommentPath);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof TargetCommentId memberId)) return false;

        return new EqualsBuilder().append(getTargetCommentPath(), memberId.getTargetCommentPath()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getTargetCommentPath()).toHashCode();
    }
}
