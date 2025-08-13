package kr.modusplant.legacy.domains.member.domain.service;

import kr.modusplant.global.vo.EntityName;
import kr.modusplant.legacy.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.legacy.domains.member.common.util.domain.SiteMemberRoleTestUtils;
import kr.modusplant.legacy.domains.member.common.util.domain.SiteMemberTestUtils;
import kr.modusplant.legacy.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.legacy.domains.member.common.util.entity.SiteMemberRoleEntityTestUtils;
import kr.modusplant.legacy.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.legacy.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.legacy.domains.member.persistence.repository.SiteMemberRoleRepository;
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
        EntityExistsException existsException = assertThrows(EntityExistsException.class,
                () -> memberRoleValidationService.validateExistedUuid(memberEntityUuid));
        assertThat(existsException.getMessage()).isEqualTo(new EntityExistsException(ErrorCode.SITEMEMBER_ROLE_EXISTS, EntityName.SITE_MEMBER_ROLE).getMessage());
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
        assertThat(notFoundException.getMessage()).isEqualTo(new EntityNotFoundException(ErrorCode.SITEMEMBER_ROLE_NOT_FOUND, EntityName.SITE_MEMBER_ROLE).getMessage());
    }
}