package kr.modusplant.legacy.domains.member.mapper;

import kr.modusplant.framework.out.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.entity.util.SiteMemberAuthEntityTestUtils;
import kr.modusplant.framework.out.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberAuthInsertRequest;
import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberAuthResponse;
import kr.modusplant.legacy.domains.member.common.util.app.http.request.SiteMemberAuthRequestTestUtils;
import kr.modusplant.legacy.domains.member.common.util.app.http.response.SiteMemberAuthResponseTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static kr.modusplant.framework.out.jpa.entity.constant.SiteMemberAuthEntityConstant.*;
import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class SiteMemberAuthAppInfraMapperTest implements SiteMemberAuthRequestTestUtils, SiteMemberAuthResponseTestUtils, SiteMemberAuthEntityTestUtils {

    private final SiteMemberJpaRepository memberRepository;
    private final SiteMemberAuthAppInfraMapper memberAuthMapper = new SiteMemberAuthAppInfraMapperImpl();

    @Autowired
    SiteMemberAuthAppInfraMapperTest(SiteMemberJpaRepository memberRepository) {
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
        SiteMemberAuthEntity memberAuthEntity = memberAuthMapper.toMemberAuthEntity(new SiteMemberAuthInsertRequest(memberEntity.getUuid(), MEMBER_AUTH_BASIC_USER_EMAIL, MEMBER_AUTH_BASIC_USER_PW, MEMBER_AUTH_BASIC_USER_PROVIDER, MEMBER_AUTH_BASIC_USER_PROVIDER_ID), memberRepository);

        // then
        assertThat(memberAuthEntity.getEmail()).isEqualTo(MEMBER_AUTH_BASIC_USER_EMAIL);
    }
}