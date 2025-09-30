package kr.modusplant.legacy.domains.communication.mapper;

import kr.modusplant.framework.out.jpa.entity.CommPostEntity;
import kr.modusplant.framework.out.jpa.entity.CommPrimaryCategoryEntity;
import kr.modusplant.framework.out.jpa.entity.CommSecondaryCategoryEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.entity.util.SiteMemberEntityTestUtils;
import kr.modusplant.framework.out.jpa.repository.CommPostJpaRepository;
import kr.modusplant.framework.out.jpa.repository.CommPrimaryCategoryJpaRepository;
import kr.modusplant.framework.out.jpa.repository.CommSecondaryCategoryJpaRepository;
import kr.modusplant.framework.out.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import kr.modusplant.legacy.domains.communication.app.http.response.CommPostResponse;
import kr.modusplant.legacy.domains.communication.common.util.entity.CommPostEntityTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.entity.CommPrimaryCategoryEntityTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.entity.CommSecondaryCategoryEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RepositoryOnlyContext
class CommPostAppInfraMapperTest implements CommPostEntityTestUtils, CommPrimaryCategoryEntityTestUtils, CommSecondaryCategoryEntityTestUtils, SiteMemberEntityTestUtils {
    private final CommPostAppInfraMapper commPostAppInfraMapper = new CommPostAppInfraMapperImpl();
    private final SiteMemberJpaRepository siteMemberRepository;
    private final CommPrimaryCategoryJpaRepository commPrimaryCategoryRepository;
    private final CommSecondaryCategoryJpaRepository commSecondaryCategoryRepository;
    private final CommPostJpaRepository commPostRepository;

    @Autowired
    CommPostAppInfraMapperTest(SiteMemberJpaRepository siteMemberRepository, CommPrimaryCategoryJpaRepository commPrimaryCategoryRepository, CommSecondaryCategoryJpaRepository commSecondaryCategoryRepository, CommPostJpaRepository commPostRepository){
        this.siteMemberRepository = siteMemberRepository;
        this.commPrimaryCategoryRepository = commPrimaryCategoryRepository;
        this.commSecondaryCategoryRepository = commSecondaryCategoryRepository;
        this.commPostRepository = commPostRepository;
    }

    @Test
    @DisplayName("엔티티를 응답으로 전환")
    void toCommPostResponseTest() {
        // given
        CommPrimaryCategoryEntity commPrimaryCategoryEntity = commPrimaryCategoryRepository.save(createTestCommPrimaryCategoryEntity());
        CommSecondaryCategoryEntity commSecondaryCategoryEntity = commSecondaryCategoryRepository.save(createTestCommSecondaryCategoryEntity());
        SiteMemberEntity siteMemberEntity = siteMemberRepository.save(createMemberBasicUserEntity());
        CommPostEntity commPostEntity = commPostRepository.save(
                createCommPostEntityBuilder()
                        .primaryCategory(commPrimaryCategoryEntity)
                        .secondaryCategory(commSecondaryCategoryEntity)
                        .authMember(siteMemberEntity)
                        .createMember(siteMemberEntity)
                        .build()
        );

        // when
        CommPostResponse commPostResponse = commPostAppInfraMapper.toCommPostResponse(commPostEntity);

        // then
        assertThat(commPostResponse.primaryCategory()).isEqualTo(commPostEntity.getPrimaryCategory().getCategory());
        assertThat(commPostResponse.primaryCategoryUuid()).isEqualTo(commPostEntity.getPrimaryCategory().getUuid());
        assertThat(commPostResponse.primaryCategoryOrder()).isEqualTo(commPostEntity.getPrimaryCategory().getOrder());
        assertThat(commPostResponse.secondaryCategory()).isEqualTo(commPostEntity.getSecondaryCategory().getCategory());
        assertThat(commPostResponse.secondaryCategoryUuid()).isEqualTo(commPostEntity.getSecondaryCategory().getUuid());
        assertThat(commPostResponse.secondaryCategoryOrder()).isEqualTo(commPostEntity.getSecondaryCategory().getOrder());
        assertThat(commPostResponse.nickname()).isEqualTo(commPostEntity.getAuthMember().getNickname());
    }
}