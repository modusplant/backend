package kr.modusplant.legacy.domains.member.mapper;

import kr.modusplant.framework.out.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.repository.SiteMemberRepository;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberAuthInsertRequest;
import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberAuthResponse;
import kr.modusplant.legacy.domains.member.common.util.app.http.request.SiteMemberAuthRequestTestUtils;
import kr.modusplant.legacy.domains.member.common.util.app.http.response.SiteMemberAuthResponseTestUtils;
import kr.modusplant.legacy.domains.member.common.util.entity.SiteMemberAuthEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class SiteMemberAuthAppInfraMapperTest implements SiteMemberAuthRequestTestUtils, SiteMemberAuthResponseTestUtils, SiteMemberAuthEntityTestUtils {

    private final SiteMemberRepository memberRepository;
    private final SiteMemberAuthAppInfraMapper memberAuthMapper = new SiteMemberAuthAppInfraMapperImpl();

    @Autowired
    SiteMemberAuthAppInfraMapperTest(SiteMemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @DisplayName("엔터티를 응답으로 전환")
    @Test
    void toSiteMemberAuthResponseTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMemberAuthEntity memberAuthEntity = this.createMemberAuthBasicUserEntityBuilder().originalMember(memberEntity).activeMember(memberEntity).build();

        // when
        SiteMemberAuthResponse memberAuthResponse = memberAuthMapper.toMemberAuthResponse(memberAuthEntity);

        // then
        assertThat(memberAuthResponse.originalMemberUuid()).isEqualTo(memberAuthBasicUserResponse.originalMemberUuid());
    }

    @DisplayName("요청을 엔터티로 전환")
    @Test
    void toSiteMemberAuthEntityTest() {
        // given
        SiteMemberEntity memberEntity = memberRepository.save(createMemberBasicUserEntity());

        // when
        SiteMemberAuthEntity memberAuthEntity = memberAuthMapper.toMemberAuthEntity(new SiteMemberAuthInsertRequest(memberEntity.getUuid(), memberAuthBasicUser.getEmail(), memberAuthBasicUser.getPw(), memberAuthBasicUser.getProvider(), memberAuthBasicUser.getProviderId()), memberRepository);

        // then
        assertThat(memberAuthEntity.getEmail()).isEqualTo(memberAuthBasicUser.getEmail());
    }
}