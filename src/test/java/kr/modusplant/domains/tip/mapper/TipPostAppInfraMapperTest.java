package kr.modusplant.domains.tip.mapper;

import kr.modusplant.domains.group.common.util.entity.PlantGroupEntityTestUtils;
import kr.modusplant.domains.group.persistence.entity.PlantGroupEntity;
import kr.modusplant.domains.group.persistence.repository.PlantGroupRepository;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.domains.tip.app.http.response.TipPostResponse;
import kr.modusplant.domains.tip.common.util.entity.TipPostEntityTestUtils;
import kr.modusplant.domains.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.tip.persistence.repository.TipPostRepository;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@RepositoryOnlyContext
class TipPostAppInfraMapperTest implements TipPostEntityTestUtils, PlantGroupEntityTestUtils, SiteMemberEntityTestUtils {
    private final TipPostAppInfraMapper tipPostAppInfraMapper = new TipPostAppInfraMapperImpl();
    private final SiteMemberRepository siteMemberRepository;
    private final PlantGroupRepository plantGroupRepository;
    private final TipPostRepository tipPostRepository;

    @Autowired
    TipPostAppInfraMapperTest(SiteMemberRepository siteMemberRepository,PlantGroupRepository plantGroupRepository,TipPostRepository tipPostRepository){
        this.siteMemberRepository = siteMemberRepository;
        this.plantGroupRepository = plantGroupRepository;
        this.tipPostRepository = tipPostRepository;
    }

    @Test
    @DisplayName("엔티티를 응답으로 전환")
    void toTipPostResponseTest() {
        // given
        PlantGroupEntity plantGroupEntity = plantGroupRepository.save(createPlantGroupEntity());
        SiteMemberEntity siteMemberEntity = siteMemberRepository.save(createMemberBasicUserEntity());
        TipPostEntity tipPostEntity = tipPostRepository.save(
                createTipPostEntityBuilder()
                        .group(plantGroupEntity)
                        .authMember(siteMemberEntity)
                        .createMember(siteMemberEntity)
                        .build()
        );

        // when
        TipPostResponse tipPostResponse = tipPostAppInfraMapper.toTipPostResponse(tipPostEntity);

        // then
        assertThat(tipPostResponse.getGroupOrder()).isEqualTo(tipPostEntity.getGroup().getOrder());
        assertThat(tipPostResponse.getCategory()).isEqualTo(tipPostEntity.getGroup().getCategory());
        assertThat(tipPostResponse.getNickname()).isEqualTo(tipPostEntity.getAuthMember().getNickname());
    }

}