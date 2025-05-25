package kr.modusplant.domains.qna.common.util.entity;


import kr.modusplant.domains.group.common.util.entity.PlantGroupEntityTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.qna.common.util.domain.QnaPostTestUtils;
import kr.modusplant.domains.qna.persistence.entity.QnaPostEntity;
import kr.modusplant.domains.qna.persistence.entity.QnaPostEntity.QnaPostEntityBuilder;


public interface QnaPostEntityTestUtils extends SiteMemberEntityTestUtils, PlantGroupEntityTestUtils, QnaPostTestUtils {
    default QnaPostEntityBuilder createQnaPostEntityBuilder() {
        return QnaPostEntity.builder()
                .title(qnaPost.getTitle())
                .content(qnaPost.getContent());
    }
}
