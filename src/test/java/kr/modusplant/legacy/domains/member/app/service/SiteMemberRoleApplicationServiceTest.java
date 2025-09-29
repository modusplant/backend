package kr.modusplant.legacy.domains.member.app.service;

import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberRoleEntity;
import kr.modusplant.framework.out.jpa.repository.SiteMemberRepository;
import kr.modusplant.framework.out.jpa.repository.SiteMemberRoleRepository;
import kr.modusplant.infrastructure.security.enums.Role;
import kr.modusplant.legacy.domains.common.context.DomainsServiceWithoutValidationServiceContext;
import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberRoleUpdateRequest;
import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberRoleResponse;
import kr.modusplant.legacy.domains.member.common.util.app.http.request.SiteMemberRequestTestUtils;
import kr.modusplant.legacy.domains.member.common.util.app.http.request.SiteMemberRoleRequestTestUtils;
import kr.modusplant.legacy.domains.member.common.util.app.http.response.SiteMemberResponseTestUtils;
import kr.modusplant.legacy.domains.member.common.util.app.http.response.SiteMemberRoleResponseTestUtils;
import kr.modusplant.legacy.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.legacy.domains.member.common.util.entity.SiteMemberRoleEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@DomainsServiceWithoutValidationServiceContext
class SiteMemberRoleApplicationServiceTest implements SiteMemberRoleRequestTestUtils, SiteMemberRoleResponseTestUtils, SiteMemberRoleEntityTestUtils, SiteMemberRequestTestUtils, SiteMemberResponseTestUtils, SiteMemberEntityTestUtils {

    private final SiteMemberRoleApplicationService memberRoleService;
    private final SiteMemberApplicationService memberService;
    private final SiteMemberRoleRepository memberRoleRepository;
    private final SiteMemberRepository memberRepository;

    @Autowired
    SiteMemberRoleApplicationServiceTest(SiteMemberRoleApplicationService memberRoleService, SiteMemberApplicationService memberService, SiteMemberRoleRepository memberRoleRepository, SiteMemberRepository memberRepository) {
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
        UUID uuid = memberEntity.getUuid();

        given(memberRepository.existsByUuid(uuid)).willReturn(true);
        given(memberRepository.findByUuid(uuid)).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(createMemberBasicUserEntity())).willReturn(memberEntity);
        given(memberRoleRepository.existsByUuid(uuid)).willReturn(false);
        given(memberRoleRepository.findByUuid(uuid)).willReturn(Optional.of(memberRoleEntity));
        given(memberRoleRepository.save(memberRoleEntity)).willReturn(memberRoleEntity);

        // when
        memberService.insert(memberBasicUserInsertRequest);
        SiteMemberRoleResponse memberRoleResponse = memberRoleService.insert(memberRoleUserInsertRequest);

        // then
        assertThat(memberRoleService.getByUuid(memberRoleResponse.uuid()).orElseThrow()).isEqualTo(memberRoleResponse);
    }

    @DisplayName("member로 회원 역할 얻기")
    @Test
    void getByMemberTest() {
        // given
        SiteMemberRoleEntity memberRoleEntity = createMemberRoleUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberRoleEntity.getMember();
        UUID uuid = memberEntity.getUuid();

        given(memberRepository.existsByUuid(uuid)).willReturn(true);
        given(memberRepository.findByUuid(uuid)).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(createMemberBasicUserEntity())).willReturn(memberEntity);
        given(memberRoleRepository.existsByUuid(uuid)).willReturn(false);
        given(memberRoleRepository.findByUuid(uuid)).willReturn(Optional.of(memberRoleEntity));
        given(memberRoleRepository.save(memberRoleEntity)).willReturn(memberRoleEntity);

        // when
        memberService.insert(memberBasicUserInsertRequest);
        SiteMemberRoleResponse memberRoleResponse = memberRoleService.insert(memberRoleUserInsertRequest);

        // then
        assertThat(memberRoleService.getByMember(memberEntity).orElseThrow()).isEqualTo(memberRoleResponse);
    }

    @DisplayName("role로 회원 역할 얻기")
    @Test
    void getByRoleTest() {
        // given
        SiteMemberRoleEntity memberRoleEntity = createMemberRoleUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberRoleEntity.getMember();
        UUID uuid = memberEntity.getUuid();

        given(memberRepository.save(createMemberBasicUserEntity())).willReturn(memberEntity);
        given(memberRepository.existsByUuid(uuid)).willReturn(true);
        given(memberRepository.findByUuid(uuid)).willReturn(Optional.of(memberEntity));
        given(memberRoleRepository.existsByUuid(uuid)).willReturn(false);
        given(memberRoleRepository.save(memberRoleEntity)).willReturn(memberRoleEntity);
        given(memberRoleRepository.findByRole(memberRoleEntity.getRole())).willReturn(List.of(memberRoleEntity));

        // when
        memberService.insert(memberBasicUserInsertRequest);
        SiteMemberRoleResponse memberRoleResponse = memberRoleService.insert(memberRoleUserInsertRequest);

        // then
        assertThat(memberRoleService.getByRole(memberRoleResponse.role()).getFirst()).isEqualTo(memberRoleResponse);
    }

    @DisplayName("빈 회원 역할 얻기")
    @Test
    void getOptionalEmptyTest() {
        // given
        SiteMemberRoleEntity memberRoleEntity = createMemberRoleUserEntityWithUuid();
        UUID uuid = memberRoleEntity.getUuid();
        SiteMemberEntity memberEntity = memberRoleEntity.getMember();

        // getByUuid
        // given & when
        given(memberRoleRepository.findByUuid(uuid)).willReturn(Optional.empty());

        // then
        assertThat(memberRoleService.getByUuid(uuid)).isEmpty();

        // getByMember
        // given & when
        given(memberRoleRepository.findByMember(memberEntity)).willReturn(Optional.empty());

        // then
        assertThat(memberRoleService.getByMember(memberEntity)).isEmpty();
    }

    @DisplayName("회원 역할 갱신")
    @Test
    void updateTest() {
        // given
        Role updatedRole = Role.ADMIN;
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMemberRoleEntity memberRoleEntity = createMemberRoleUserEntityWithUuid();
        SiteMemberRoleEntity updatedMemberRoleEntity = SiteMemberRoleEntity.builder().memberRoleEntity(memberRoleEntity).role(updatedRole).build();
        UUID uuid = memberEntity.getUuid();

        given(memberRepository.save(createMemberBasicUserEntity())).willReturn(memberEntity);
        given(memberRepository.existsByUuid(uuid)).willReturn(true);
        given(memberRepository.findByUuid(uuid)).willReturn(Optional.of(memberEntity));
        given(memberRoleRepository.existsByUuid(uuid)).willReturn(false).willReturn(true);
        given(memberRoleRepository.save(memberRoleEntity)).willReturn(memberRoleEntity).willReturn(updatedMemberRoleEntity);
        given(memberRoleRepository.findByUuid(uuid)).willReturn(Optional.of(updatedMemberRoleEntity));
        given(memberRoleRepository.findByRole(updatedRole)).willReturn(List.of(updatedMemberRoleEntity));

        // when
        memberService.insert(memberBasicUserInsertRequest);
        memberRoleService.insert(memberRoleUserInsertRequest);
        SiteMemberRoleResponse updatedMemberRoleResponse = memberRoleService.update(new SiteMemberRoleUpdateRequest(memberEntity.getUuid(), updatedRole));

        // then
        assertThat(memberRoleService.getByRole(updatedRole).getFirst()).isEqualTo(updatedMemberRoleResponse);
    }

    @DisplayName("uuid로 회원 역할 제거")
    @Test
    void removeByUuidTest() {
        // given
        SiteMemberRoleEntity memberRoleEntity = createMemberRoleUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberRoleEntity.getMember();
        UUID uuid = memberEntity.getUuid();

        given(memberRepository.save(createMemberBasicUserEntity())).willReturn(memberEntity);
        given(memberRepository.existsByUuid(uuid)).willReturn(true);
        given(memberRepository.findByUuid(uuid)).willReturn(Optional.of(memberEntity));
        given(memberRoleRepository.existsByUuid(uuid)).willReturn(false).willReturn(true);
        given(memberRoleRepository.save(memberRoleEntity)).willReturn(memberRoleEntity);
        given(memberRoleRepository.findByUuid(uuid)).willReturn(Optional.empty());
        willDoNothing().given(memberRoleRepository).deleteByUuid(uuid);

        // when
        memberService.insert(memberBasicUserInsertRequest);
        memberRoleService.insert(memberRoleUserInsertRequest);
        memberRoleService.removeByUuid(uuid);

        // then
        assertThat(memberRoleService.getByUuid(uuid)).isEmpty();
    }
}