package kr.modusplant.legacy.domains.member.domain.service;

import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.entity.constant.SiteMemberEntityConstant;
import kr.modusplant.framework.out.jpa.entity.util.SiteMemberEntityTestUtils;
import kr.modusplant.framework.out.jpa.repository.SiteMemberRepository;
import kr.modusplant.infrastructure.persistence.constant.EntityName;
import kr.modusplant.legacy.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.shared.exception.EntityExistsException;
import kr.modusplant.shared.exception.EntityNotFoundException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@DomainsServiceOnlyContext
class SiteMemberValidationServiceTest implements SiteMemberEntityConstant, SiteMemberEntityTestUtils {
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
        EntityExistsException existsException = assertThrows(EntityExistsException.class,
                () -> memberValidationService.validateExistedUuid(memberEntity.getUuid()));
        assertThat(existsException.getMessage()).isEqualTo(new EntityExistsException(ErrorCode.MEMBER_EXISTS, EntityName.SITE_MEMBER).getMessage());
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
        EntityNotFoundException notFoundException = assertThrows(EntityNotFoundException.class,
                () -> memberValidationService.validateNotFoundUuid(memberEntityUuid));
        assertThat(notFoundException.getMessage()).isEqualTo(new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND, EntityName.SITE_MEMBER).getMessage());
    }
}