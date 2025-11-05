package kr.modusplant.domains.member.common.util.usecase.record;

import kr.modusplant.domains.member.usecase.record.MemberCommentLikeRecord;

import static kr.modusplant.domains.member.common.constant.MemberStringConstant.TEST_TARGET_COMMENT_PATH_STRING;
import static kr.modusplant.domains.member.common.constant.MemberStringConstant.TEST_TARGET_POST_ID_STRING;
import static kr.modusplant.domains.member.common.constant.MemberUuidConstant.TEST_MEMBER_ID_UUID;

public interface MemberCommentLikeRecordTestUtils {
    MemberCommentLikeRecord TEST_MEMBER_COMMENT_LIKE_RECORD = new MemberCommentLikeRecord(TEST_MEMBER_ID_UUID, TEST_TARGET_POST_ID_STRING, TEST_TARGET_COMMENT_PATH_STRING);
}
