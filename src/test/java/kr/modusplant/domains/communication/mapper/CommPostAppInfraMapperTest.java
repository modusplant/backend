package kr.modusplant.domains.communication.mapper;

import kr.modusplant.domains.communication.app.http.response.CommPostResponse;
import kr.modusplant.domains.communication.common.util.entity.CommPostEntityTestUtils;
import kr.modusplant.domains.communication.common.util.entity.CommSecondaryCategoryEntityTestUtils;
import kr.modusplant.domains.communication.persistence.entity.CommPostEntity;
import kr.modusplant.domains.communication.persistence.entity.CommSecondaryCategoryEntity;
import kr.modusplant.domains.communication.persistence.repository.CommPostRepository;
import kr.modusplant.domains.communication.persistence.repository.CommSecondaryCategoryRepository;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RepositoryOnlyContext
class CommPostAppInfraMapperTest implements CommPostEntityTestUtils, CommSecondaryCategoryEntityTestUtils, SiteMemberEntityTestUtils {
    private final CommPostAppInfraMapper commPostAppInfraMapper = new CommPostAppInfraMapperImpl();
    private final SiteMemberRepository siteMemberRepository;
    private final CommSecondaryCategoryRepository commCategoryRepository;
    private final CommPostRepository commPostRepository;

    @Autowired
    CommPostAppInfraMapperTest(SiteMemberRepository siteMemberRepository, CommSecondaryCategoryRepository commCategoryRepository, CommPostRepository commPostRepository){
        this.siteMemberRepository = siteMemberRepository;
        this.commCategoryRepository = commCategoryRepository;
        this.commPostRepository = commPostRepository;
    }

    @Test
    @DisplayName("엔티티를 응답으로 전환")
    void toCommPostResponseTest() {
        // given
        CommSecondaryCategoryEntity commSecondaryCategoryEntity = commCategoryRepository.save(createTestCommSecondaryCategoryEntity());
        SiteMemberEntity siteMemberEntity = siteMemberRepository.save(createMemberBasicUserEntity());
        CommPostEntity commPostEntity = commPostRepository.save(
                createCommPostEntityBuilder()
                        .category(commSecondaryCategoryEntity)
                        .authMember(siteMemberEntity)
                        .createMember(siteMemberEntity)
                        .build()
        );

        // when
        CommPostResponse commPostResponse = commPostAppInfraMapper.toCommPostResponse(commPostEntity);

        // then
        assertThat(commPostResponse.categoryUuid()).isEqualTo(commPostEntity.getCategory().getUuid());
        assertThat(commPostResponse.category()).isEqualTo(commPostEntity.getCategory().getCategory());
        assertThat(commPostResponse.nickname()).isEqualTo(commPostEntity.getAuthMember().getNickname());
    }
}