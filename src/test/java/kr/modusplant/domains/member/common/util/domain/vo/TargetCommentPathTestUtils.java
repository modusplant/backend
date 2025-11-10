package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.TargetCommentPath;

import static kr.modusplant.shared.persistence.common.util.constant.CommCommentConstant.TEST_COMM_COMMENT_PATH;

public interface TargetCommentPathTestUtils {
    TargetCommentPath testTargetCommentPath = TargetCommentPath.create(TEST_COMM_COMMENT_PATH);
}
