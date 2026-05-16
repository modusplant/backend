package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.MemberCommentUnlikeRecord;

import static kr.modusplant.shared.persistence.common.util.constant.CommentConstant.TEST_COMM_COMMENT_PATH;
import static kr.modusplant.shared.persistence.common.util.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.shared.persistence.common.util.constant.PostConstant.TEST_COMM_POST_ULID;

public interface MemberCommentUnlikeRecordTestUtils {
    MemberCommentUnlikeRecord testMemberCommentUnlikeRecord = new MemberCommentUnlikeRecord(MEMBER_BASIC_USER_UUID, TEST_COMM_POST_ULID, TEST_COMM_COMMENT_PATH);
}
