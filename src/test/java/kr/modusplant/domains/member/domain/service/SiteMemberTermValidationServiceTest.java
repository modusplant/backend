package kr.modusplant.domains.member.domain.service;

import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTermTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberTermEntityTestUtils;
import kr.modusplant.domains.member.error.SiteMemberTermExistsException;
import kr.modusplant.domains.member.error.SiteMemberTermNotFoundException;
import kr.modusplant.domains.member.persistence.repository.SiteMemberTermRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

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
        SiteMemberTermExistsException existsException = assertThrows(SiteMemberTermExistsException.class,
                () -> memberTermValidationService.validateExistedUuid(uuid));
        assertThat(existsException.getMessage()).isEqualTo(new SiteMemberTermExistsException().getMessage());
    }

    @DisplayName("존재하지 않는 회원 약관 UUID 검증")
    @Test
    void validateNotFoundUuidTest() {
        // given
        UUID uuid = memberTermUserWithUuid.getUuid();

        // when
        given(memberTermRepository.existsByUuid(uuid)).willReturn(false);

        // then
        SiteMemberTermNotFoundException notFoundException = assertThrows(SiteMemberTermNotFoundException.class,
                () -> memberTermValidationService.validateNotFoundUuid(uuid));
        assertThat(notFoundException.getMessage()).isEqualTo(new SiteMemberTermNotFoundException().getMessage());
    }
}