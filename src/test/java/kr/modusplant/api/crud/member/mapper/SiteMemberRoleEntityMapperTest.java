package kr.modusplant.api.crud.member.mapper;

import kr.modusplant.api.crud.member.common.util.domain.SiteMemberRoleTestUtils;
import kr.modusplant.api.crud.member.common.util.entity.SiteMemberRoleEntityTestUtils;
import kr.modusplant.api.crud.member.domain.model.SiteMemberRole;
import kr.modusplant.api.crud.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.api.crud.member.persistence.entity.SiteMemberRoleEntity;
import kr.modusplant.api.crud.member.persistence.repository.SiteMemberCrudJpaRepository;
import kr.modusplant.api.crud.member.persistence.repository.SiteMemberRoleCrudJpaRepository;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class SiteMemberRoleEntityMapperTest implements SiteMemberRoleTestUtils, SiteMemberRoleEntityTestUtils {

    private final SiteMemberRoleCrudJpaRepository memberRoleRepository;
    private final SiteMemberCrudJpaRepository memberRepository;
    private final SiteMemberRoleEntityMapper memberRoleMapper = new SiteMemberRoleEntityMapperImpl();

    @Autowired
    SiteMemberRoleEntityMapperTest(SiteMemberRoleCrudJpaRepository memberRoleRepository, SiteMemberCrudJpaRepository memberRepository) {
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