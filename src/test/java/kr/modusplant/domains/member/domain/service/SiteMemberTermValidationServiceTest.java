package kr.modusplant.domains.member.domain.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import kr.modusplant.domains.common.context.DomainServiceOnlyContext;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTermTestUtils;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberTermEntityTestUtils;
import kr.modusplant.domains.member.domain.model.SiteMemberTerm;
import kr.modusplant.domains.member.mapper.SiteMemberTermEntityMapper;
import kr.modusplant.domains.member.mapper.SiteMemberTermEntityMapperImpl;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberTermEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberCrudJpaRepository;
import kr.modusplant.domains.member.persistence.repository.SiteMemberTermCrudJpaRepository;
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

@DomainServiceOnlyContext
class SiteMemberTermValidationServiceTest implements SiteMemberTermTestUtils, SiteMemberTermEntityTestUtils, SiteMemberTestUtils, SiteMemberEntityTestUtils {
    private final SiteMemberTermValidationService memberTermValidationService;
    private final SiteMemberTermCrudJpaRepository memberTermRepository;
    private final SiteMemberCrudJpaRepository memberRepository;
    private final SiteMemberTermEntityMapper memberTermMapper = new SiteMemberTermEntityMapperImpl();

    @Autowired
    SiteMemberTermValidationServiceTest(SiteMemberTermValidationService memberTermValidationService, SiteMemberTermCrudJpaRepository memberTermRepository, SiteMemberCrudJpaRepository memberRepository) {
        this.memberTermValidationService = memberTermValidationService;
        this.memberTermRepository = memberTermRepository;
        this.memberRepository = memberRepository;
    }

    @DisplayName("존재하는 회원 약관 UUID 검증")
    @Test
    void validateExistedMemberTermUuidTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        UUID memberEntityUuid = memberEntity.getUuid();
        SiteMemberTermEntity memberTermEntity = SiteMemberTermEntity.builder().member(memberEntity).build();
        SiteMemberTerm memberTerm = memberTermMapper.toSiteMemberTerm(memberTermEntity);

        // when
        given(memberRepository.findByUuid(memberEntityUuid)).willReturn(Optional.of(memberEntity));
        given(memberTermRepository.findByUuid(memberEntityUuid)).willReturn(Optional.of(memberTermEntity));

        // then
        EntityExistsException existsException = assertThrows(EntityExistsWithUuidException.class,
                () -> memberTermValidationService.validateExistedMemberTermUuid(memberTerm.getUuid()));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                EXISTED_ENTITY, "uuid", memberEntityUuid, SiteMemberTermEntity.class));
    }

    @DisplayName("존재하지 않는 회원 약관 UUID 검증")
    @Test
    void validateNotFoundMemberTermUuidTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        UUID memberEntityUuid = memberEntity.getUuid();
        SiteMemberTermEntity memberTermEntity = SiteMemberTermEntity.builder().member(memberEntity).build();
        SiteMemberTerm memberTerm = memberTermMapper.toSiteMemberTerm(memberTermEntity);

        // when
        given(memberTermRepository.findByUuid(memberEntityUuid)).willReturn(Optional.empty());

        // then
        EntityNotFoundException notFoundException = assertThrows(EntityNotFoundWithUuidException.class,
                () -> memberTermValidationService.validateNotFoundMemberTermUuid(memberTerm.getUuid()));
        assertThat(notFoundException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY, "uuid", memberEntityUuid, SiteMemberTermEntity.class));
    }
}