package kr.modusplant.domains.communication.qna.mapper;

import kr.modusplant.domains.communication.qna.app.http.response.QnaPostResponse;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaCategoryEntityTestUtils;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaPostEntityTestUtils;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCategoryEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaCategoryRepository;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostRepository;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RepositoryOnlyContext
class QnaPostAppInfraMapperTest implements QnaPostEntityTestUtils, QnaCategoryEntityTestUtils, SiteMemberEntityTestUtils {
    private final QnaPostAppInfraMapper qnaPostAppInfraMapper = new QnaPostAppInfraMapperImpl();
    private final SiteMemberRepository siteMemberRepository;
    private final QnaCategoryRepository qnaCategoryRepository;
    private final QnaPostRepository qnaPostRepository;

    @Autowired
    QnaPostAppInfraMapperTest(SiteMemberRepository siteMemberRepository, QnaCategoryRepository qnaCategoryRepository, QnaPostRepository qnaPostRepository){
        this.siteMemberRepository = siteMemberRepository;
        this.qnaCategoryRepository = qnaCategoryRepository;
        this.qnaPostRepository = qnaPostRepository;
    }

    @Test
    @DisplayName("엔티티를 응답으로 전환")
    void toQnaPostResponseTest() {
        // given
        QnaCategoryEntity qnaCategoryEntity = qnaCategoryRepository.save(testQnaCategoryEntity);
        SiteMemberEntity siteMemberEntity = siteMemberRepository.save(createMemberBasicUserEntity());
        QnaPostEntity qnaPostEntity = qnaPostRepository.save(
                createQnaPostEntityBuilder()
                        .group(qnaCategoryEntity)
                        .authMember(siteMemberEntity)
                        .createMember(siteMemberEntity)
                        .build()
        );

        // when
        QnaPostResponse qnaPostResponse = qnaPostAppInfraMapper.toQnaPostResponse(qnaPostEntity);

        // then
        assertThat(qnaPostResponse.getGroupOrder()).isEqualTo(qnaPostEntity.getGroup().getOrder());
        assertThat(qnaPostResponse.getCategory()).isEqualTo(qnaPostEntity.getGroup().getCategory());
        assertThat(qnaPostResponse.getNickname()).isEqualTo(qnaPostEntity.getAuthMember().getNickname());
    }

}