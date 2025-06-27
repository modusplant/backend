package kr.modusplant.domains.member.domain.service;

import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.error.SiteMemberExistsException;
import kr.modusplant.domains.member.error.SiteMemberNotFoundException;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@DomainsServiceOnlyContext
class SiteMemberValidationServiceTest implements SiteMemberTestUtils, SiteMemberEntityTestUtils {
    private final SiteMemberValidationService memberValidationService;
    private final SiteMemberRepository memberRepository;

    @Autowired
    SiteMemberValidationServiceTest(SiteMemberValidationService memberValidationService, SiteMemberRepository memberRepository) {
        this.memberValidationService = memberValidationService;
        this.memberRepository = memberRepository;
    }

    @DisplayName("존재하는 회원 UUID 검증")
    @Test
    void validateExistedUuidTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        UUID memberEntityUuid = memberEntity.getUuid();

        // when
        given(memberRepository.existsByUuid(memberEntityUuid)).willReturn(true);

        // then
        SiteMemberExistsException existsException = assertThrows(SiteMemberExistsException.class,
                () -> memberValidationService.validateExistedUuid(memberEntity.getUuid()));
        assertThat(existsException.getMessage()).isEqualTo(new SiteMemberExistsException().getMessage());
    }

    @DisplayName("존재하지 않는 회원 UUID 검증")
    @Test
    void validateNotFoundUuidTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        UUID memberEntityUuid = memberEntity.getUuid();

        // when
        given(memberRepository.existsByUuid(memberEntityUuid)).willReturn(false);

        // then
        SiteMemberNotFoundException notFoundException = assertThrows(SiteMemberNotFoundException.class,
                () -> memberValidationService.validateNotFoundUuid(memberEntityUuid));
        assertThat(notFoundException.getMessage()).isEqualTo(new SiteMemberNotFoundException().getMessage());
    }
}