package kr.modusplant.domains.communication.qna.common.util.domain;

import kr.modusplant.domains.communication.qna.domain.model.QnaLike;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils;

public interface QnaLikeTestUtils extends QnaPostTestUtils, SiteMemberTestUtils {
    QnaLike testQnaLike = QnaLike.builder()
            .postId(testQnaPostWithUlid.getUlid())
            .memberId(memberBasicUserWithUuid.getUuid())
            .build();
}
