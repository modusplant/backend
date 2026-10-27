package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.MemberCommentChooseRecord;

import static kr.modusplant.domains.comment.common.constant.CommentConstant.TEST_COMMENT_PATH;
import static kr.modusplant.domains.member.common.constant.MemberConstant.MEMBER_BASIC_USER_UUID;
import static kr.modusplant.domains.post.common.constant.PostConstant.TEST_POST_ULID;

public interface MemberCommentChooseRecordTestUtils {
    MemberCommentChooseRecord testMemberCommentChooseRecord = new MemberCommentChooseRecord(MEMBER_BASIC_USER_UUID, TEST_POST_ULID, TEST_COMMENT_PATH);
}
