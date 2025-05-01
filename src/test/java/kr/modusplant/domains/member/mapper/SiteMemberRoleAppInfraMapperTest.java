package kr.modusplant.domains.member.mapper;

import kr.modusplant.domains.member.app.http.request.SiteMemberRoleInsertRequest;
import kr.modusplant.domains.member.app.http.response.SiteMemberRoleResponse;
import kr.modusplant.domains.member.common.util.app.http.request.SiteMemberRoleRequestTestUtils;
import kr.modusplant.domains.member.common.util.app.http.response.SiteMemberRoleResponseTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberRoleEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberRoleEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class SiteMemberRoleAppInfraMapperTest implements SiteMemberRoleRequestTestUtils, SiteMemberRoleResponseTestUtils, SiteMemberRoleEntityTestUtils {

    private final SiteMemberRepository memberRepository;
    private final SiteMemberRoleAppInfraMapper memberRoleMapper = new SiteMemberRoleAppInfraMapperImpl();

    @Autowired
    SiteMemberRoleAppInfraMapperTest(SiteMemberRepository memberRepository) {
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
        SiteMemberRoleEntity memberRoleEntity = memberRoleMapper.toMemberRoleEntity(new SiteMemberRoleInsertRequest(memberEntity.getUuid(), memberRoleUser.getRole()), memberRepository);

        // then
        assertThat(memberRoleEntity.getRole()).isEqualTo(memberRoleUser.getRole());
    }
}