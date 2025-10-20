package kr.modusplant.legacy.domains.member.domain.service;

import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.entity.common.util.SiteMemberEntityTestUtils;
import kr.modusplant.framework.out.jpa.entity.common.util.SiteMemberRoleEntityTestUtils;
import kr.modusplant.framework.out.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.framework.out.jpa.repository.SiteMemberRoleJpaRepository;
import kr.modusplant.infrastructure.persistence.constant.EntityName;
import kr.modusplant.legacy.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.shared.exception.EntityExistsException;
import kr.modusplant.shared.exception.EntityNotFoundException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@DomainsServiceOnlyContext
class SiteMemberRoleValidationServiceTest implements SiteMemberRoleEntityTestUtils, SiteMemberEntityTestUtils {

    private final SiteMemberRoleValidationService memberRoleValidationService;
    private final SiteMemberRoleJpaRepository memberRoleRepository;
    private final SiteMemberJpaRepository memberRepository;

    @Autowired
    SiteMemberRoleValidationServiceTest(SiteMemberRoleValidationService memberRoleValidationService, SiteMemberRoleJpaRepository memberRoleRepository, SiteMemberJpaRepository memberRepository) {
        this.memberRoleValidationService = memberRoleValidationService;
        this.memberRoleRepository = memberRoleRepository;
        this.memberRepository = memberRepository;
    }

    @DisplayName("존재하는 회원 역할 UUID 검증")
    @Test
    void validateExistedUuidTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        UUID memberEntityUuid = memberEntity.getUuid();

        // when
        given(memberRepository.findByUuid(memberEntityUuid)).willReturn(Optional.of(memberEntity));
        given(memberRoleRepository.existsByUuid(memberEntityUuid)).willReturn(true);

        // then
        EntityExistsException existsException = assertThrows(EntityExistsException.class,
                () -> memberRoleValidationService.validateExistedUuid(memberEntityUuid));
        assertThat(existsException.getMessage()).isEqualTo(new EntityExistsException(ErrorCode.MEMBER_ROLE_EXISTS, EntityName.SITE_MEMBER_ROLE).getMessage());
    }

    @DisplayName("존재하지 않는 회원 역할 UUID 검증")
    @Test
    void validateNotFoundUuidTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        UUID memberEntityUuid = memberEntity.getUuid();

        // when
        given(memberRepository.findByUuid(memberEntityUuid)).willReturn(Optional.of(memberEntity));
        given(memberRoleRepository.existsByUuid(memberEntityUuid)).willReturn(false);

        // then
        EntityNotFoundException notFoundException = assertThrows(EntityNotFoundException.class,
                () -> memberRoleValidationService.validateNotFoundUuid(memberEntityUuid));
        assertThat(notFoundException.getMessage()).isEqualTo(new EntityNotFoundException(ErrorCode.MEMBER_ROLE_NOT_FOUND, EntityName.SITE_MEMBER_ROLE).getMessage());
    }
}