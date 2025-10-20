package kr.modusplant.legacy.domains.member.domain.service;

import kr.modusplant.framework.out.jpa.entity.common.util.SiteMemberTermEntityTestUtils;
import kr.modusplant.framework.out.jpa.repository.SiteMemberTermJpaRepository;
import kr.modusplant.infrastructure.persistence.constant.EntityName;
import kr.modusplant.legacy.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.shared.exception.EntityExistsException;
import kr.modusplant.shared.exception.EntityNotFoundException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static kr.modusplant.shared.persistence.common.constant.SiteMemberTermConstant.MEMBER_TERM_USER_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@DomainsServiceOnlyContext
class SiteMemberTermValidationServiceTest implements SiteMemberTermEntityTestUtils {
    private final SiteMemberTermValidationService memberTermValidationService;
    private final SiteMemberTermJpaRepository memberTermRepository;

    @Autowired
    SiteMemberTermValidationServiceTest(SiteMemberTermValidationService memberTermValidationService, SiteMemberTermJpaRepository memberTermRepository) {
        this.memberTermValidationService = memberTermValidationService;
        this.memberTermRepository = memberTermRepository;
    }

    @DisplayName("존재하는 회원 약관 UUID 검증")
    @Test
    void validateExistedUuidTest() {
        // given
        UUID uuid = MEMBER_TERM_USER_UUID;

        // when
        given(memberTermRepository.existsByUuid(uuid)).willReturn(true);

        // then
        EntityExistsException existsException = assertThrows(EntityExistsException.class,
                () -> memberTermValidationService.validateExistedUuid(uuid));
        assertThat(existsException.getMessage()).isEqualTo(new EntityExistsException(ErrorCode.MEMBER_TERM_EXISTS, EntityName.SITE_MEMBER_TERM).getMessage());
    }

    @DisplayName("존재하지 않는 회원 약관 UUID 검증")
    @Test
    void validateNotFoundUuidTest() {
        // given
        UUID uuid = MEMBER_TERM_USER_UUID;

        // when
        given(memberTermRepository.existsByUuid(uuid)).willReturn(false);

        // then
        EntityNotFoundException notFoundException = assertThrows(EntityNotFoundException.class,
                () -> memberTermValidationService.validateNotFoundUuid(uuid));
        assertThat(notFoundException.getMessage()).isEqualTo(new EntityNotFoundException(ErrorCode.MEMBER_TERM_NOT_FOUND, EntityName.SITE_MEMBER_TERM).getMessage());
    }
}