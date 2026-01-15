package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.shared.exception.EmptyValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.EMPTY_TARGET_COMMENT_PATH;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.EMPTY_TARGET_POST_ID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TargetCommentId {
    private final TargetPostId targetPostId;
    private final TargetCommentPath targetCommentPath;

    public static TargetCommentId create(TargetPostId targetPostId, TargetCommentPath targetCommentPath) {
        if (targetPostId == null) {
            throw new EmptyValueException(EMPTY_TARGET_POST_ID, "targetPostId");
        } else if (targetCommentPath == null) {
            throw new EmptyValueException(EMPTY_TARGET_COMMENT_PATH, "targetCommentPath");
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
