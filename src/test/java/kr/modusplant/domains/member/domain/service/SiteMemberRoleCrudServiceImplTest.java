package kr.modusplant.domains.member.domain.service;

import kr.modusplant.domains.common.context.DomainServiceOnlyContext;
import kr.modusplant.domains.member.common.util.domain.SiteMemberRoleTestUtils;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberRoleEntityTestUtils;
import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.domain.model.SiteMemberRole;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberCrudService;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberRoleCrudService;
import kr.modusplant.domains.member.mapper.SiteMemberEntityMapper;
import kr.modusplant.domains.member.mapper.SiteMemberEntityMapperImpl;
import kr.modusplant.domains.member.mapper.SiteMemberRoleEntityMapper;
import kr.modusplant.domains.member.mapper.SiteMemberRoleEntityMapperImpl;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberRoleEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberCrudJpaRepository;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRoleCrudJpaRepository;
import kr.modusplant.global.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@DomainServiceOnlyContext
class SiteMemberRoleCrudServiceImplTest implements SiteMemberRoleTestUtils, SiteMemberRoleEntityTestUtils, SiteMemberTestUtils, SiteMemberEntityTestUtils {

    private final SiteMemberRoleCrudService memberRoleService;
    private final SiteMemberCrudService memberService;
    private final SiteMemberRoleCrudJpaRepository memberRoleRepository;
    private final SiteMemberCrudJpaRepository memberRepository;
    private final SiteMemberRoleEntityMapper memberRoleMapper = new SiteMemberRoleEntityMapperImpl();
    private final SiteMemberEntityMapper memberMapper = new SiteMemberEntityMapperImpl();

    @Autowired
    SiteMemberRoleCrudServiceImplTest(SiteMemberRoleCrudService memberRoleService, SiteMemberCrudService memberService, SiteMemberRoleCrudJpaRepository memberRoleRepository, SiteMemberCrudJpaRepository memberRepository) {
        this.memberRoleService = memberRoleService;
        this.memberService = memberService;
        this.memberRoleRepository = memberRoleRepository;
        this.memberRepository = memberRepository;
    }

    @DisplayName("uuid로 회원 역할 얻기")
    @Test
    void getByUuidTest() {
        // given
        SiteMemberRoleEntity memberRoleEntity = createMemberRoleUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberRoleEntity.getMember();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberRole memberRole = memberRoleMapper.toSiteMemberRole(memberRoleEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberRoleRepository.findByUuid(memberRole.getUuid())).willReturn(Optional.of(memberRoleEntity));
        given(memberRoleRepository.save(memberRoleEntity)).willReturn(memberRoleEntity);

        // when
        memberService.insert(member);
        memberRole = memberRoleService.insert(memberRole);

        // then
        assertThat(memberRoleService.getByUuid(memberRole.getUuid()).orElseThrow()).isEqualTo(memberRole);
    }

    @DisplayName("member로 회원 역할 얻기")
    @Test
    void getByMemberTest() {
        // given
        SiteMemberRoleEntity memberRoleEntity = createMemberRoleUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberRoleEntity.getMember();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberRole memberRole = memberRoleMapper.toSiteMemberRole(memberRoleEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberRoleRepository.findByUuid(memberRole.getUuid())).willReturn(Optional.of(memberRoleEntity));
        given(memberRoleRepository.save(memberRoleEntity)).willReturn(memberRoleEntity);

        // when
        memberService.insert(member);
        memberRole = memberRoleService.insert(memberRole);

        // then
        assertThat(memberRoleService.getByMember(member).orElseThrow()).isEqualTo(memberRole);
    }

    @DisplayName("role로 회원 역할 얻기")
    @Test
    void getByRoleTest() {
        // given
        SiteMemberRoleEntity memberRoleEntity = createMemberRoleUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberRoleEntity.getMember();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberRole memberRole = memberRoleMapper.toSiteMemberRole(memberRoleEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberRoleRepository.findByUuid(memberRole.getUuid())).willReturn(Optional.of(memberRoleEntity));
        given(memberRoleRepository.save(memberRoleEntity)).willReturn(memberRoleEntity);
        given(memberRoleRepository.findByRole(memberRoleEntity.getRole())).willReturn(List.of(memberRoleEntity));

        // when
        memberService.insert(member);
        memberRole = memberRoleService.insert(memberRole);

        // then
        assertThat(memberRoleService.getByRole(memberRoleEntity.getRole()).getFirst()).isEqualTo(memberRole);
    }

    @DisplayName("빈 회원 역할 얻기")
    @Test
    void getOptionalEmptyTest() {
        // given
        SiteMemberRoleEntity memberRoleEntity = createMemberRoleUserEntityWithUuid();
        UUID uuid = memberRoleEntity.getUuid();
        SiteMemberEntity memberEntity = memberRoleEntity.getMember();
        SiteMember member = memberMapper.toSiteMember(memberEntity);

        // getByUuid
        // given & when
        given(memberRoleRepository.findByUuid(uuid)).willReturn(Optional.empty());

        // then
        assertThat(memberRoleService.getByUuid(uuid)).isEmpty();

        // getByMember
        // given & when
        given(memberRoleRepository.findByMember(memberEntity)).willReturn(Optional.empty());

        // then
        assertThat(memberRoleService.getByMember(member)).isEmpty();
    }

    @DisplayName("회원 역할 갱신")
    @Test
    void updateTest() {
        // given
        Role updatedRole = Role.ROLE_ADMIN;
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberRoleEntity memberRoleEntity = createMemberRoleUserEntityWithUuid();
        SiteMemberRoleEntity updatedMemberRoleEntity = SiteMemberRoleEntity.builder().memberRoleEntity(memberRoleEntity).role(updatedRole).build();
        SiteMemberRole memberRole = memberRoleMapper.toSiteMemberRole(memberRoleEntity);
        SiteMemberRole updatedMemberRole = memberRoleMapper.toSiteMemberRole(updatedMemberRoleEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberRoleRepository.findByUuid(memberRole.getUuid())).willReturn(Optional.of(memberRoleEntity));
        given(memberRoleRepository.save(memberRoleEntity)).willReturn(memberRoleEntity);
        given(memberRoleRepository.findByRole(updatedRole)).willReturn(List.of(updatedMemberRoleEntity));

        // when
        memberService.insert(member);
        memberRoleService.insert(memberRole);
        memberRoleService.update(updatedMemberRole);

        // then
        assertThat(memberRoleService.getByRole(updatedRole).getFirst()).isEqualTo(updatedMemberRole);
    }

    @DisplayName("uuid로 회원 역할 제거")
    @Test
    void removeByUuidTest() {
        // given
        SiteMemberRoleEntity memberRoleEntity = createMemberRoleUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberRoleEntity.getMember();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberRole memberRole = memberRoleMapper.toSiteMemberRole(memberRoleEntity);
        UUID uuid = memberRole.getUuid();

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberRoleRepository.findByUuid(memberRole.getUuid())).willReturn(Optional.empty());
        given(memberRoleRepository.save(memberRoleEntity)).willReturn(memberRoleEntity);
        willDoNothing().given(memberRoleRepository).deleteByUuid(uuid);

        // when
        memberService.insert(member);
        memberRoleService.insert(memberRole);
        memberRoleService.removeByUuid(uuid);

        // then
        assertThat(memberRoleService.getByUuid(uuid)).isEmpty();
    }
}