package kr.modusplant.domains.member.mapper;

import kr.modusplant.domains.member.common.util.domain.SiteMemberRoleTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberRoleEntityTestUtils;
import kr.modusplant.domains.member.domain.model.SiteMemberRole;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberRoleEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRoleRepository;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class SiteMemberRoleEntityMapperTest implements SiteMemberRoleTestUtils, SiteMemberRoleEntityTestUtils {

    private final SiteMemberRoleRepository memberRoleRepository;
    private final SiteMemberRepository memberRepository;
    private final SiteMemberRoleEntityMapper memberRoleMapper = new SiteMemberRoleEntityMapperImpl();

    @Autowired
    SiteMemberRoleEntityMapperTest(SiteMemberRoleRepository memberRoleRepository, SiteMemberRepository memberRepository) {
        this.memberRoleRepository = memberRoleRepository;
        this.memberRepository = memberRepository;
    }

    @DisplayName("매퍼 적용 후 일관된 회원 역할 엔터티 확인")
    @Test
    void checkConsistentEntity() {
        // given
        SiteMemberRoleEntity memberRoleEntity = createMemberRoleUserEntity();

        // when
        memberRoleEntity = memberRoleRepository.save(memberRoleEntity);

        // then
        assertThat(memberRoleEntity).isEqualTo(memberRoleMapper.updateSiteMemberRoleEntity(memberRoleMapper.toSiteMemberRole(memberRoleEntity), memberRepository));
    }

    @DisplayName("매퍼 적용 후 일관된 회원 역할 도메인 확인")
    @Test
    void checkConsistentDomain() {
        // given
        SiteMemberEntity memberEntity = memberRepository.save(createMemberBasicUserEntity());
        SiteMemberRoleEntity memberRoleEntity = SiteMemberRoleEntity.builder().memberRoleEntity(createMemberRoleUserEntity()).member(memberEntity).build();

        // when
        SiteMemberRole memberRole = memberRoleMapper.toSiteMemberRole(memberRoleEntity);

        // then
        assertThat(memberRole).isEqualTo(memberRoleMapper.toSiteMemberRole(memberRoleMapper.createSiteMemberRoleEntity(memberRole, memberRepository)));
    }
}