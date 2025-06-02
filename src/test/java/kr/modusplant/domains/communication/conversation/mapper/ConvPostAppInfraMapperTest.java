package kr.modusplant.domains.communication.conversation.mapper;

import kr.modusplant.domains.communication.conversation.app.http.response.ConvPostResponse;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvCategoryEntityTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvPostEntityTestUtils;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCategoryEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvPostEntity;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvCategoryRepository;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvPostRepository;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RepositoryOnlyContext
class ConvPostAppInfraMapperTest implements ConvPostEntityTestUtils, ConvCategoryEntityTestUtils, SiteMemberEntityTestUtils {
    private final ConvPostAppInfraMapper convPostAppInfraMapper = new ConvPostAppInfraMapperImpl();
    private final SiteMemberRepository siteMemberRepository;
    private final ConvCategoryRepository convCategoryRepository;
    private final ConvPostRepository convPostRepository;

    @Autowired
    ConvPostAppInfraMapperTest(SiteMemberRepository siteMemberRepository, ConvCategoryRepository convCategoryRepository, ConvPostRepository convPostRepository){
        this.siteMemberRepository = siteMemberRepository;
        this.convCategoryRepository = convCategoryRepository;
        this.convPostRepository = convPostRepository;
    }

    @Test
    @DisplayName("엔티티를 응답으로 전환")
    void toConvPostResponseTest() {
        // given
        ConvCategoryEntity convCategoryEntity = convCategoryRepository.save(createTestConvCategoryEntity());
        SiteMemberEntity siteMemberEntity = siteMemberRepository.save(createMemberBasicUserEntity());
        ConvPostEntity convPostEntity = convPostRepository.save(
                createConvPostEntityBuilder()
                        .category(convCategoryEntity)
                        .authMember(siteMemberEntity)
                        .createMember(siteMemberEntity)
                        .build()
        );

        // when
        ConvPostResponse convPostResponse = convPostAppInfraMapper.toConvPostResponse(convPostEntity);

        // then
        assertThat(convPostResponse.getCategoryUuid()).isEqualTo(convPostEntity.getCategory().getUuid());
        assertThat(convPostResponse.getCategory()).isEqualTo(convPostEntity.getCategory().getCategory());
        assertThat(convPostResponse.getNickname()).isEqualTo(convPostEntity.getAuthMember().getNickname());
    }

}