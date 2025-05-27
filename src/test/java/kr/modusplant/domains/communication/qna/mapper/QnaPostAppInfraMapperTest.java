package kr.modusplant.domains.communication.qna.mapper;

import kr.modusplant.domains.communication.qna.common.util.entity.QnaPostEntityTestUtils;
import kr.modusplant.domains.group.common.util.entity.PlantGroupEntityTestUtils;
import kr.modusplant.domains.group.persistence.entity.PlantGroupEntity;
import kr.modusplant.domains.group.persistence.repository.PlantGroupRepository;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.domains.communication.qna.app.http.response.QnaPostResponse;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostRepository;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RepositoryOnlyContext
class QnaPostAppInfraMapperTest implements QnaPostEntityTestUtils, PlantGroupEntityTestUtils, SiteMemberEntityTestUtils {
    private final QnaPostAppInfraMapper qnaPostAppInfraMapper = new QnaPostAppInfraMapperImpl();
    private final SiteMemberRepository siteMemberRepository;
    private final PlantGroupRepository plantGroupRepository;
    private final QnaPostRepository qnaPostRepository;

    @Autowired
    QnaPostAppInfraMapperTest(SiteMemberRepository siteMemberRepository, PlantGroupRepository plantGroupRepository, QnaPostRepository qnaPostRepository){
        this.siteMemberRepository = siteMemberRepository;
        this.plantGroupRepository = plantGroupRepository;
        this.qnaPostRepository = qnaPostRepository;
    }

    @Test
    @DisplayName("엔티티를 응답으로 전환")
    void toQnaPostResponseTest() {
        // given
        PlantGroupEntity plantGroupEntity = plantGroupRepository.save(createPlantGroupEntity());
        SiteMemberEntity siteMemberEntity = siteMemberRepository.save(createMemberBasicUserEntity());
        QnaPostEntity qnaPostEntity = qnaPostRepository.save(
                createQnaPostEntityBuilder()
                        .group(plantGroupEntity)
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