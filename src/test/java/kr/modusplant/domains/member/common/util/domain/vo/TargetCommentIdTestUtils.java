package kr.modusplant.domains.member.common.util.domain.vo;

import kr.modusplant.domains.member.domain.vo.TargetCommentId;

import static kr.modusplant.domains.member.common.util.domain.vo.TargetCommentPathTestUtils.testTargetCommentPath;
import static kr.modusplant.domains.member.common.util.domain.vo.TargetPostIdTestUtils.testTargetPostId;

public interface TargetCommentIdTestUtils {
    TargetCommentId testTargetCommentId = TargetCommentId.create(testTargetPostId, testTargetCommentPath);
}
