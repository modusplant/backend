package kr.modusplant.global.persistence.service;

import kr.modusplant.global.domain.model.SiteMember;
import kr.modusplant.global.domain.model.SiteMemberRole;
import kr.modusplant.global.domain.service.crud.SiteMemberService;
import kr.modusplant.global.domain.service.crud.SiteMemberRoleService;
import kr.modusplant.global.mapper.SiteMemberEntityMapper;
import kr.modusplant.global.mapper.SiteMemberEntityMapperImpl;
import kr.modusplant.global.mapper.SiteMemberRoleEntityMapper;
import kr.modusplant.global.mapper.SiteMemberRoleEntityMapperImpl;
import kr.modusplant.global.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.persistence.entity.SiteMemberRoleEntity;
import kr.modusplant.global.persistence.repository.SiteMemberJpaRepository;
import kr.modusplant.global.persistence.repository.SiteMemberRoleJpaRepository;
import kr.modusplant.support.context.ServiceOnlyContext;
import kr.modusplant.support.util.domain.SiteMemberRoleTestUtils;
import kr.modusplant.support.util.domain.SiteMemberTestUtils;
import kr.modusplant.support.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.support.util.entity.SiteMemberRoleEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ServiceOnlyContext
class SiteMemberRoleServiceImplTest implements SiteMemberRoleTestUtils, SiteMemberRoleEntityTestUtils, SiteMemberTestUtils, SiteMemberEntityTestUtils {

    private final SiteMemberRoleService memberRoleService;
    private final SiteMemberService memberService;
    private final SiteMemberRoleJpaRepository memberRoleRepository;
    private final SiteMemberJpaRepository memberRepository;
    private final SiteMemberRoleEntityMapper memberRoleMapper = new SiteMemberRoleEntityMapperImpl();
    private final SiteMemberEntityMapper memberMapper = new SiteMemberEntityMapperImpl();

    @Autowired
    SiteMemberRoleServiceImplTest(SiteMemberRoleService memberRoleService, SiteMemberService memberService, SiteMemberRoleJpaRepository memberRoleRepository, SiteMemberJpaRepository memberRepository) {
        this.memberRoleService = memberRoleService;
        this.memberService = memberService;
        this.memberRoleRepository = memberRoleRepository;
        this.memberRepository = memberRepository;
    }

    @DisplayName("uuid로 회원 역할 찾기")
    @Test
    void getByUuidTest() {
        // given
        SiteMemberRoleEntity memberRoleEntity = createMemberRoleUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberRoleEntity.getMember();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberRole memberRole = memberRoleMapper.toSiteMemberRole(memberRoleEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberRoleRepository.findByUuid(memberRole.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberRoleEntity));
        given(memberRoleRepository.save(memberRoleEntity)).willReturn(memberRoleEntity);

        // when
        memberService.insert(member);
        memberRole = memberRoleService.insert(memberRole);

        // then
        assertThat(memberRoleService.getByUuid(memberRole.getUuid()).orElseThrow()).isEqualTo(memberRole);
    }

    @DisplayName("role로 회원 역할 찾기")
    @Test
    void getByRoleTest() {
        // given
        SiteMemberRoleEntity memberRoleEntity = createMemberRoleUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberRoleEntity.getMember();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberRole memberRole = memberRoleMapper.toSiteMemberRole(memberRoleEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberRoleRepository.findByUuid(memberRole.getUuid())).willReturn(Optional.empty());
        given(memberRoleRepository.save(memberRoleEntity)).willReturn(memberRoleEntity);
        given(memberRoleRepository.findByRole(memberRoleEntity.getRole())).willReturn(List.of(memberRoleEntity));

        // when
        memberService.insert(member);
        memberRole = memberRoleService.insert(memberRole);

        // then
        assertThat(memberRoleService.getByRole(memberRoleEntity.getRole()).getFirst()).isEqualTo(memberRole);
    }

    @DisplayName("uuid로 회원 역할 삭제")
    @Test
    void deleteByUuidTest() {
        // given
        SiteMemberRoleEntity memberRoleEntity = createMemberRoleUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberRoleEntity.getMember();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberRole memberRole = memberRoleMapper.toSiteMemberRole(memberRoleEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberRoleRepository.findByUuid(memberRole.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberRoleEntity));
        given(memberRoleRepository.save(memberRoleEntity)).willReturn(memberRoleEntity);
        given(memberRoleRepository.findAll()).willReturn(emptyList());
        willDoNothing().given(memberRoleRepository).deleteByUuid(memberRole.getUuid());

        // when
        memberService.insert(member);
        memberRole = memberRoleService.insert(memberRole);
        memberRoleService.removeByUuid(memberRole.getUuid());

        // then
        assertThat(memberRoleService.getAll()).isEmpty();
    }
}