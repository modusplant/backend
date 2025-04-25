package kr.modusplant.domains.member.domain.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.member.common.util.domain.SiteMemberAuthTestUtils;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberAuthEntityTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberAuthRepository;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;
import static kr.modusplant.global.vo.CamelCaseWord.ORIGINAL_MEMBER_UUID;
import static kr.modusplant.global.vo.ExceptionMessage.EXISTED_ENTITY;
import static kr.modusplant.global.vo.ExceptionMessage.NOT_FOUND_ENTITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@DomainsServiceOnlyContext
class SiteMemberAuthValidationServiceTest implements SiteMemberAuthTestUtils, SiteMemberAuthEntityTestUtils, SiteMemberTestUtils, SiteMemberEntityTestUtils {

    private final SiteMemberAuthValidationService memberAuthValidationService;
    private final SiteMemberAuthRepository memberAuthRepository;
    private final SiteMemberRepository memberRepository;

    @Autowired
    SiteMemberAuthValidationServiceTest(SiteMemberAuthValidationService memberAuthValidationService, SiteMemberAuthRepository memberAuthRepository, SiteMemberRepository memberRepository) {
        this.memberAuthValidationService = memberAuthValidationService;
        this.memberAuthRepository = memberAuthRepository;
        this.memberRepository = memberRepository;
    }

    @DisplayName("존재하는 회원 인증 UUID 검증")
    @Test
    void validateExistedUuidTest() {
        // given
        SiteMemberEntity activeMemberEntity = createMemberBasicUserEntityWithUuid();
        SiteMemberEntity originalMemberEntity = SiteMemberEntity.builder().memberEntity(activeMemberEntity).uuid(UUID.randomUUID()).build();
        UUID originalMemberEntityUuid = originalMemberEntity.getUuid();
        SiteMemberAuthEntity memberAuthEntity = createMemberAuthBasicUserEntityWithUuidBuilder().activeMember(activeMemberEntity).originalMember(originalMemberEntity).build();
        UUID memberAuthEntityUuid = memberAuthEntity.getUuid();

        // when
        given(memberRepository.findByUuid(originalMemberEntityUuid)).willReturn(Optional.of(originalMemberEntity));
        given(memberAuthRepository.findByUuid(memberAuthEntityUuid)).willReturn(Optional.of(memberAuthEntity));

        // then
        EntityExistsException existsException = assertThrows(EntityExistsWithUuidException.class,
                () -> memberAuthValidationService.validateExistedUuid(memberAuthEntityUuid));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                EXISTED_ENTITY, "uuid", memberAuthEntityUuid, SiteMemberAuthEntity.class));
    }

    @DisplayName("존재하는 회원 인증 최초 회원 UUID 검증")
    @Test
    void validateExistedOriginalMemberUuidTest() {
        // given
        SiteMemberEntity activeMemberEntity = createMemberBasicUserEntityWithUuid();
        SiteMemberEntity originalMemberEntity = SiteMemberEntity.builder().memberEntity(activeMemberEntity).uuid(UUID.randomUUID()).build();
        UUID originalMemberEntityUuid = originalMemberEntity.getUuid();
        SiteMemberAuthEntity memberAuthEntity = createMemberAuthBasicUserEntityWithUuidBuilder().activeMember(activeMemberEntity).originalMember(originalMemberEntity).build();

        // when
        given(memberRepository.findByUuid(originalMemberEntityUuid)).willReturn(Optional.of(originalMemberEntity));
        given(memberAuthRepository.findByOriginalMember(originalMemberEntity)).willReturn(Optional.of(memberAuthEntity));

        // then
        EntityExistsException existsException = assertThrows(EntityExistsException.class,
                () -> memberAuthValidationService.validateExistedOriginalMemberUuid(originalMemberEntityUuid));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                EXISTED_ENTITY, ORIGINAL_MEMBER_UUID, originalMemberEntityUuid, SiteMemberAuthEntity.class));
    }

    @DisplayName("존재하지 않는 회원 인증 UUID 검증")
    @Test
    void validateNotFoundOriginalMemberUuidTest() {
        // given
        SiteMemberEntity activeMemberEntity = createMemberBasicUserEntityWithUuid();
        SiteMemberEntity originalMemberEntity = SiteMemberEntity.builder().memberEntity(activeMemberEntity).uuid(UUID.randomUUID()).build();
        UUID originalMemberEntityUuid = originalMemberEntity.getUuid();
        SiteMemberAuthEntity memberAuthEntity = createMemberAuthBasicUserEntityWithUuidBuilder().activeMember(activeMemberEntity).originalMember(originalMemberEntity).build();
        UUID memberAuthEntityUuid = memberAuthEntity.getUuid();

        // when
        given(memberRepository.findByUuid(originalMemberEntityUuid)).willReturn(Optional.of(originalMemberEntity));
        given(memberAuthRepository.findByUuid(memberAuthEntityUuid)).willReturn(Optional.empty());

        // then
        EntityNotFoundException notFoundException = assertThrows(EntityNotFoundWithUuidException.class,
                () -> memberAuthValidationService.validateNotFoundOriginalMemberUuid(memberAuthEntityUuid));
        assertThat(notFoundException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY, "uuid", memberAuthEntityUuid, SiteMemberAuthEntity.class));
    }
}