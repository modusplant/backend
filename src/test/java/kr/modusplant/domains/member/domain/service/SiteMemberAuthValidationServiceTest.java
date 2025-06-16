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
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static kr.modusplant.domains.member.vo.MemberUuid.ORIGINAL_MEMBER_UUID;
import static kr.modusplant.global.enums.ExceptionMessage.EXISTED_ENTITY;
import static kr.modusplant.global.enums.ExceptionMessage.NOT_FOUND_ENTITY;
import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;
import static kr.modusplant.global.vo.FieldName.EMAIL;
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

    @DisplayName("존재하는 최초 회원 UUID 검증")
    @Test
    void validateExistedOriginalMemberUuidTest() {
        // given
        SiteMemberEntity activeMemberEntity = createMemberBasicUserEntityWithUuid();
        SiteMemberEntity originalMemberEntity = SiteMemberEntity.builder().memberEntity(activeMemberEntity).uuid(UUID.randomUUID()).build();
        UUID originalMemberEntityUuid = originalMemberEntity.getUuid();

        // when
        given(memberRepository.findByUuid(originalMemberEntityUuid)).willReturn(Optional.of(originalMemberEntity));
        given(memberAuthRepository.existsByOriginalMember(originalMemberEntity)).willReturn(true);

        // then
        EntityExistsException existsException = assertThrows(EntityExistsException.class,
                () -> memberAuthValidationService.validateExistedOriginalMemberUuid(originalMemberEntityUuid));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                EXISTED_ENTITY.getValue(), ORIGINAL_MEMBER_UUID, originalMemberEntityUuid, SiteMemberAuthEntity.class));
    }

    @DisplayName("존재하지 않는 최초 회원 UUID 검증")
    @Test
    void validateNotFoundOriginalMemberUuidTest() {
        // given
        SiteMemberEntity activeMemberEntity = createMemberBasicUserEntityWithUuid();
        SiteMemberEntity originalMemberEntity = SiteMemberEntity.builder().memberEntity(activeMemberEntity).uuid(UUID.randomUUID()).build();
        UUID originalMemberEntityUuid = originalMemberEntity.getUuid();
        SiteMemberAuthEntity memberAuthEntity = this.createMemberAuthBasicUserEntityBuilder().originalMember(originalMemberEntity).activeMember(activeMemberEntity).build();
        UUID memberAuthEntityUuid = memberAuthEntity.getUuid();

        // when
        given(memberRepository.findByUuid(originalMemberEntityUuid)).willReturn(Optional.of(originalMemberEntity));
        given(memberAuthRepository.existsByOriginalMember(originalMemberEntity)).willReturn(false);

        // then
        EntityNotFoundException notFoundException = assertThrows(EntityNotFoundWithUuidException.class,
                () -> memberAuthValidationService.validateNotFoundOriginalMemberUuid(memberAuthEntityUuid));
        assertThat(notFoundException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY.getValue(), "uuid", memberAuthEntityUuid, SiteMemberAuthEntity.class));
    }

    @DisplayName("존재하지 않는 이메일 검증")
    @Test
    void validateNotFoundEmailTest() {
        // given
        SiteMemberEntity activeMemberEntity = createMemberBasicUserEntityWithUuid();
        SiteMemberEntity originalMemberEntity = SiteMemberEntity.builder().memberEntity(activeMemberEntity).uuid(UUID.randomUUID()).build();
        UUID originalMemberEntityUuid = originalMemberEntity.getUuid();
        SiteMemberAuthEntity memberAuthEntity = this.createMemberAuthBasicUserEntityBuilder().originalMember(originalMemberEntity).activeMember(activeMemberEntity).build();
        String email = memberAuthEntity.getEmail();

        // when
        given(memberRepository.findByUuid(originalMemberEntityUuid)).willReturn(Optional.of(originalMemberEntity));
        given(memberAuthRepository.findByEmail(email)).willReturn(emptyList());

        // then
        EntityNotFoundException notFoundException = assertThrows(EntityNotFoundException.class,
                () -> memberAuthValidationService.validateNotFoundEmail(email));
        assertThat(notFoundException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY.getValue(), EMAIL, email, SiteMemberAuthEntity.class));
    }
}