package kr.modusplant.legacy.domains.member.domain.service;

import kr.modusplant.framework.out.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.repository.SiteMemberAuthRepository;
import kr.modusplant.framework.out.jpa.repository.SiteMemberRepository;
import kr.modusplant.infrastructure.persistence.constant.EntityName;
import kr.modusplant.legacy.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.legacy.domains.member.common.util.domain.SiteMemberAuthConstant;
import kr.modusplant.legacy.domains.member.common.util.domain.SiteMemberConstant;
import kr.modusplant.legacy.domains.member.common.util.entity.SiteMemberAuthEntityConstant;
import kr.modusplant.legacy.domains.member.common.util.entity.SiteMemberEntityConstant;
import kr.modusplant.shared.exception.EntityExistsException;
import kr.modusplant.shared.exception.EntityNotFoundException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@DomainsServiceOnlyContext
class SiteMemberAuthValidationServiceTest implements SiteMemberAuthConstant, SiteMemberAuthEntityConstant, SiteMemberConstant, SiteMemberEntityConstant {

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
        assertThat(existsException.getMessage()).isEqualTo(new EntityExistsException(ErrorCode.MEMBER_AUTH_EXISTS, EntityName.SITE_MEMBER_AUTH).getMessage());
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
        EntityNotFoundException notFoundException = assertThrows(EntityNotFoundException.class,
                () -> memberAuthValidationService.validateNotFoundOriginalMemberUuid(memberAuthEntityUuid));
        assertThat(notFoundException.getMessage()).isEqualTo(new EntityNotFoundException(ErrorCode.MEMBER_AUTH_NOT_FOUND, EntityName.SITE_MEMBER_AUTH).getMessage());
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
        assertThat(notFoundException.getMessage()).isEqualTo(new EntityNotFoundException(ErrorCode.MEMBER_AUTH_NOT_FOUND, EntityName.SITE_MEMBER_AUTH).getMessage());
    }
}