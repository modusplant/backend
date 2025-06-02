package kr.modusplant.domains.communication.qna.common.util.entity;

import kr.modusplant.domains.communication.qna.persistence.entity.QnaLikeEntity;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;

public interface QnaLikeEntityTestUtils extends QnaPostEntityTestUtils, SiteMemberEntityTestUtils {
    default QnaLikeEntity createQnaLikeEntity() {
        return QnaLikeEntity.of(qnaPost.getUlid(), memberBasicUserWithUuid.getUuid());
    }
}
