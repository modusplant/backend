package kr.modusplant.global.persistence.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import kr.modusplant.global.domain.model.SiteMember;
import kr.modusplant.global.domain.model.SiteMemberRole;
import kr.modusplant.global.domain.service.crud.SiteMemberRoleService;
import kr.modusplant.global.domain.service.crud.SiteMemberService;
import kr.modusplant.global.enums.Role;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
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
import java.util.UUID;

import static java.util.Collections.emptyList;
import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;
import static kr.modusplant.global.vo.ExceptionMessage.EXISTED_ENTITY;
import static kr.modusplant.global.vo.ExceptionMessage.NOT_FOUND_ENTITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @DisplayName("uuid로 회원 역할 얻기")
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

    @DisplayName("role로 회원 역할 얻기")
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

    @DisplayName("회원 역할 삽입 간 검증")
    @Test
    void validateDuringInsertTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        UUID memberEntityUuid = memberEntity.getUuid();
        SiteMemberRoleEntity memberRoleEntity = SiteMemberRoleEntity.builder().member(memberEntity).build();
        SiteMemberRole memberRole = memberRoleMapper.toSiteMemberRole(memberRoleEntity);

        // Not Found member 검증
        // given & when
        given(memberRepository.findByUuid(memberEntityUuid)).willReturn(Optional.empty());

        // then
        EntityNotFoundException notFoundException = assertThrows(EntityNotFoundWithUuidException.class,
                () -> memberRoleService.insert(memberRole));
        assertThat(notFoundException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY, "uuid", memberEntityUuid, SiteMemberEntity.class));

        // Existed memberRole 검증
        // given & when
        given(memberRepository.findByUuid(memberEntityUuid)).willReturn(Optional.of(memberEntity));
        given(memberRoleRepository.findByUuid(memberEntityUuid)).willReturn(Optional.of(memberRoleEntity));

        // then
        EntityExistsException existsException = assertThrows(EntityExistsWithUuidException.class,
                () -> memberRoleService.insert(memberRole));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                EXISTED_ENTITY, "uuid", memberEntityUuid, SiteMemberRoleEntity.class));
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

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberRoleRepository.findByUuid(memberRole.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(updatedMemberRoleEntity));
        given(memberRoleRepository.save(memberRoleEntity)).willReturn(memberRoleEntity);
        given(memberRoleRepository.save(updatedMemberRoleEntity)).willReturn(updatedMemberRoleEntity);
        given(memberRoleRepository.findByRole(updatedRole)).willReturn(List.of(updatedMemberRoleEntity));

        // when
        memberService.insert(member);
        memberRoleService.insert(memberRole);
        memberRoleService.update(updatedMemberRole);

        // then
        assertThat(memberRoleService.getByRole(updatedRole).getFirst()).isEqualTo(updatedMemberRole);
    }

    @DisplayName("회원 역할 갱신 간 검증")
    @Test
    void validateDuringUpdateTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        UUID memberEntityUuid = memberEntity.getUuid();
        SiteMemberRoleEntity memberRoleEntity = SiteMemberRoleEntity.builder().member(memberEntity).build();
        SiteMemberRole memberRole = memberRoleMapper.toSiteMemberRole(memberRoleEntity);

        // Not Found member 검증
        // given & when
        given(memberRepository.findByUuid(memberEntityUuid)).willReturn(Optional.empty());

        // then
        EntityNotFoundException notFoundException = assertThrows(EntityNotFoundWithUuidException.class,
                () -> memberRoleService.update(memberRole));
        assertThat(notFoundException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY, "uuid", memberEntityUuid, SiteMemberEntity.class));

        // Not Found memberRole 검증
        // given & when
        given(memberRepository.findByUuid(memberEntityUuid)).willReturn(Optional.of(memberEntity));
        given(memberRoleRepository.findByUuid(memberEntityUuid)).willReturn(Optional.empty());

        // then
        notFoundException = assertThrows(EntityNotFoundWithUuidException.class,
                () -> memberRoleService.update(memberRole));
        assertThat(notFoundException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY, "uuid", memberEntityUuid, SiteMemberRoleEntity.class));
    }

    @DisplayName("uuid로 회원 역할 제거")
    @Test
    void removeByUuidTest() {
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

    @DisplayName("uuid로 회원 역할 제거 간 검증")
    @Test
    void validateDuringRemoveByUuidTest() {
        // given & when
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        UUID memberEntityUuid = memberEntity.getUuid();

        given(memberRoleRepository.findByUuid(memberEntityUuid)).willReturn(Optional.empty());

        // then
        EntityNotFoundException notFoundException = assertThrows(EntityNotFoundWithUuidException.class,
                () -> memberRoleService.removeByUuid(memberEntityUuid));
        assertThat(notFoundException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY, "uuid", memberEntityUuid, SiteMemberRoleEntity.class));
    }
}