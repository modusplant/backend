package kr.modusplant.domains.communication.tip.mapper;

import kr.modusplant.domains.communication.tip.app.http.response.TipPostResponse;
import kr.modusplant.domains.communication.tip.common.util.entity.TipCategoryEntityTestUtils;
import kr.modusplant.domains.communication.tip.common.util.entity.TipPostEntityTestUtils;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCategoryEntity;
import kr.modusplant.domains.communication.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.communication.tip.persistence.repository.TipCategoryRepository;
import kr.modusplant.domains.communication.tip.persistence.repository.TipPostRepository;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RepositoryOnlyContext
class TipPostAppInfraMapperTest implements TipPostEntityTestUtils, TipCategoryEntityTestUtils, SiteMemberEntityTestUtils {
    private final TipPostAppInfraMapper tipPostAppInfraMapper = new TipPostAppInfraMapperImpl();
    private final SiteMemberRepository siteMemberRepository;
    private final TipCategoryRepository tipCategoryRepository;
    private final TipPostRepository tipPostRepository;

    @Autowired
    TipPostAppInfraMapperTest(SiteMemberRepository siteMemberRepository, TipCategoryRepository tipCategoryRepository, TipPostRepository tipPostRepository){
        this.siteMemberRepository = siteMemberRepository;
        this.tipCategoryRepository = tipCategoryRepository;
        this.tipPostRepository = tipPostRepository;
    }

    @Test
    @DisplayName("엔티티를 응답으로 전환")
    void toTipPostResponseTest() {
        // given
        TipCategoryEntity tipCategoryEntity = tipCategoryRepository.save(createTestTipCategoryEntity());
        SiteMemberEntity siteMemberEntity = siteMemberRepository.save(createMemberBasicUserEntity());
        TipPostEntity tipPostEntity = tipPostRepository.save(
                createTipPostEntityBuilder()
                        .category(tipCategoryEntity)
                        .authMember(siteMemberEntity)
                        .createMember(siteMemberEntity)
                        .build()
        );

        // when
        TipPostResponse tipPostResponse = tipPostAppInfraMapper.toTipPostResponse(tipPostEntity);

        // then
        assertThat(tipPostResponse.getCategoryUuid()).isEqualTo(tipPostEntity.getCategory().getUuid());
        assertThat(tipPostResponse.getCategory()).isEqualTo(tipPostEntity.getCategory().getCategory());
        assertThat(tipPostResponse.getNickname()).isEqualTo(tipPostEntity.getAuthMember().getNickname());
    }

}