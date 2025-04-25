package kr.modusplant.domains.member.domain.service;

import jakarta.persistence.EntityNotFoundException;
import kr.modusplant.domains.common.context.DomainsServiceOnlyContext;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;
import static kr.modusplant.global.vo.ExceptionMessage.EXISTED_ENTITY;
import static kr.modusplant.global.vo.ExceptionMessage.NOT_FOUND_ENTITY;
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
        given(memberRepository.findByUuid(memberEntityUuid)).willReturn(Optional.of(memberEntity));

        // then
        EntityExistsWithUuidException existsException = assertThrows(EntityExistsWithUuidException.class,
                () -> memberValidationService.validateExistedUuid(memberEntity.getUuid()));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                EXISTED_ENTITY, "uuid", memberEntityUuid, SiteMemberEntity.class));
    }

    @DisplayName("존재하지 않는 회원 UUID 검증")
    @Test
    void validateNotFoundUuidTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        UUID memberEntityUuid = memberEntity.getUuid();

        // when
        given(memberRepository.findByUuid(memberEntityUuid)).willReturn(Optional.empty());

        // then
        EntityNotFoundException notFoundException = assertThrows(EntityNotFoundWithUuidException.class,
                () -> memberValidationService.validateNotFoundUuid(memberEntityUuid));
        assertThat(notFoundException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY, "uuid", memberEntityUuid, SiteMemberEntity.class));
    }
}