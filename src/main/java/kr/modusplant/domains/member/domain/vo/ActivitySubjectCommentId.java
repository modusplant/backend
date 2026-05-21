package kr.modusplant.domains.member.domain.vo;

import kr.modusplant.shared.exception.EmptyValueException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.EMPTY_ACTIVITY_SUBJECT_COMMENT_PATH;
import static kr.modusplant.domains.member.domain.exception.enums.MemberErrorCode.EMPTY_ACTIVITY_SUBJECT_POST_ID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ActivitySubjectCommentId {
    private final ActivitySubjectPostId activitySubjectPostId;
    private final ActivitySubjectCommentPath activitySubjectCommentPath;

    public static ActivitySubjectCommentId create(ActivitySubjectPostId activitySubjectPostId, ActivitySubjectCommentPath activitySubjectCommentPath) {
        if (activitySubjectPostId == null) {
            throw new EmptyValueException(EMPTY_ACTIVITY_SUBJECT_POST_ID, "activitySubjectPostId");
        } else if (activitySubjectCommentPath == null) {
            throw new EmptyValueException(EMPTY_ACTIVITY_SUBJECT_COMMENT_PATH, "activitySubjectCommentPath");
        }
        return new ActivitySubjectCommentId(activitySubjectPostId, activitySubjectCommentPath);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof ActivitySubjectCommentId memberId)) return false;

        return new EqualsBuilder().append(getActivitySubjectCommentPath(), memberId.getActivitySubjectCommentPath()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getActivitySubjectCommentPath()).toHashCode();
    }
}
