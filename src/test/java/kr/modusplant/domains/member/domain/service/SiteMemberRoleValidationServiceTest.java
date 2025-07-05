package kr.modusplant.domains.member.domain.service;

import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.member.common.util.domain.SiteMemberRoleTestUtils;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberRoleEntityTestUtils;
import kr.modusplant.domains.member.error.MemberExistsException;
import kr.modusplant.domains.member.error.MemberNotFoundException;
import kr.modusplant.domains.member.error.SiteMemberRoleExistsException;
import kr.modusplant.domains.member.error.SiteMemberRoleNotFoundException;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@DomainsServiceOnlyContext
class SiteMemberRoleValidationServiceTest implements SiteMemberRoleTestUtils, SiteMemberRoleEntityTestUtils, SiteMemberTestUtils, SiteMemberEntityTestUtils {

    private final SiteMemberRoleValidationService memberRoleValidationService;
    private final SiteMemberRoleRepository memberRoleRepository;
    private final SiteMemberRepository memberRepository;

    @Autowired
    SiteMemberRoleValidationServiceTest(SiteMemberRoleValidationService memberRoleValidationService, SiteMemberRoleRepository memberRoleRepository, SiteMemberRepository memberRepository) {
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
        MemberExistsException existsException = assertThrows(MemberExistsException.class,
                () -> memberRoleValidationService.validateExistedUuid(memberEntityUuid));
        assertThat(existsException.getMessage()).isEqualTo(MemberExistsException.ofMemberRole().getMessage());
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
        MemberNotFoundException notFoundException = assertThrows(MemberNotFoundException.class,
                () -> memberRoleValidationService.validateNotFoundUuid(memberEntityUuid));
        assertThat(notFoundException.getMessage()).isEqualTo(MemberNotFoundException.ofMemberRole().getMessage());
    }
}