package kr.modusplant.legacy.domains.member.mapper;

import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberRoleEntity;
import kr.modusplant.framework.out.jpa.entity.util.SiteMemberRoleEntityTestUtils;
import kr.modusplant.framework.out.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberRoleInsertRequest;
import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberRoleResponse;
import kr.modusplant.legacy.domains.member.common.util.app.http.request.SiteMemberRoleRequestTestUtils;
import kr.modusplant.legacy.domains.member.common.util.app.http.response.SiteMemberRoleResponseTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static kr.modusplant.framework.out.jpa.entity.constant.SiteMemberRoleEntityConstant.MEMBER_ROLE_USER_ROLE;
import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class SiteMemberRoleAppInfraMapperTest implements SiteMemberRoleRequestTestUtils, SiteMemberRoleResponseTestUtils, SiteMemberRoleEntityTestUtils {

    private final SiteMemberJpaRepository memberRepository;
    private final SiteMemberRoleAppInfraMapper memberRoleMapper = new SiteMemberRoleAppInfraMapperImpl();

    @Autowired
    SiteMemberRoleAppInfraMapperTest(SiteMemberJpaRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @DisplayName("엔터티를 응답으로 전환")
    @Test
    void toSiteMemberRoleResponseTest() {
        // given
        SiteMemberRoleEntity memberRoleEntity = createMemberRoleUserEntityWithUuid();

        // when
        SiteMemberRoleResponse memberRoleResponse = memberRoleMapper.toMemberRoleResponse(memberRoleEntity);

        // then
        assertThat(memberRoleResponse).isEqualTo(memberRoleUserResponse);
    }

    @DisplayName("요청을 엔터티로 전환")
    @Test
    void toSiteMemberRoleEntityTest() {
        // given
        SiteMemberEntity memberEntity = memberRepository.save(createMemberBasicUserEntity());

        // when
        SiteMemberRoleEntity memberRoleEntity = memberRoleMapper.toMemberRoleEntity(new SiteMemberRoleInsertRequest(memberEntity.getUuid(), MEMBER_ROLE_USER_ROLE), memberRepository);

        // then
        assertThat(memberRoleEntity.getRole()).isEqualTo(MEMBER_ROLE_USER_ROLE);
    }
}