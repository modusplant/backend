package kr.modusplant.domains.member.framework.out.jooq.record.common.util;

import kr.modusplant.domains.member.framework.out.jooq.record.TargetCommentIdRecord;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface TargetCommentIdRecordTestUtils {
    TargetCommentIdRecord testTargetCommentIdRecord = new TargetCommentIdRecord(TEST_POST_ULID, TEST_COMM_COMMENT_PATH);
}
