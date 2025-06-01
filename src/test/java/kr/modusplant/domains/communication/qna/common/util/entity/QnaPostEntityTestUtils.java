package kr.modusplant.domains.communication.qna.common.util.entity;


import kr.modusplant.domains.communication.qna.common.util.domain.QnaPostTestUtils;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity.QnaPostEntityBuilder;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;


public interface QnaPostEntityTestUtils extends SiteMemberEntityTestUtils, QnaCategoryEntityTestUtils, QnaPostTestUtils {
    default QnaPostEntityBuilder createQnaPostEntityBuilder() {
        return QnaPostEntity.builder()
                .title(qnaPost.getTitle())
                .content(qnaPost.getContent());
    }
}
