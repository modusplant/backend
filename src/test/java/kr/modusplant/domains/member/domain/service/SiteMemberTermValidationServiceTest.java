package kr.modusplant.domains.member.domain.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTermTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberTermEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberTermEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberTermRepository;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static kr.modusplant.global.enums.ExceptionMessage.EXISTED_ENTITY;
import static kr.modusplant.global.enums.ExceptionMessage.NOT_FOUND_ENTITY;
import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@DomainsServiceOnlyContext
class SiteMemberTermValidationServiceTest implements SiteMemberTermTestUtils, SiteMemberTermEntityTestUtils {
    private final SiteMemberTermValidationService memberTermValidationService;
    private final SiteMemberTermRepository memberTermRepository;

    @Autowired
    SiteMemberTermValidationServiceTest(SiteMemberTermValidationService memberTermValidationService, SiteMemberTermRepository memberTermRepository) {
        this.memberTermValidationService = memberTermValidationService;
        this.memberTermRepository = memberTermRepository;
    }

    @DisplayName("존재하는 회원 약관 UUID 검증")
    @Test
    void validateExistedUuidTest() {
        // given
        UUID uuid = memberTermUserWithUuid.getUuid();

        // when
        given(memberTermRepository.existsByUuid(uuid)).willReturn(true);

        // then
        EntityExistsException existsException = assertThrows(EntityExistsWithUuidException.class,
                () -> memberTermValidationService.validateExistedUuid(uuid));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                EXISTED_ENTITY.getValue(), "uuid", uuid, SiteMemberTermEntity.class));
    }

    @DisplayName("존재하지 않는 회원 약관 UUID 검증")
    @Test
    void validateNotFoundUuidTest() {
        // given
        UUID uuid = memberTermUserWithUuid.getUuid();

        // when
        given(memberTermRepository.existsByUuid(uuid)).willReturn(false);

        // then
        EntityNotFoundException notFoundException = assertThrows(EntityNotFoundWithUuidException.class,
                () -> memberTermValidationService.validateNotFoundUuid(uuid));
        assertThat(notFoundException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY.getValue(), "uuid", uuid, SiteMemberTermEntity.class));
    }
}