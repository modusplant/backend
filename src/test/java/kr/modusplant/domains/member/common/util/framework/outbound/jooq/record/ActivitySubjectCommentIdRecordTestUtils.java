package kr.modusplant.domains.member.common.util.framework.outbound.jooq.record;

import kr.modusplant.domains.member.framework.outbound.jooq.record.ActivitySubjectCommentIdRecord;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface ActivitySubjectCommentIdRecordTestUtils {
    ActivitySubjectCommentIdRecord testActivitySubjectCommentIdRecord = new ActivitySubjectCommentIdRecord(TEST_POST_ULID, TEST_COMM_COMMENT_PATH);
}
