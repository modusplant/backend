package kr.modusplant.domains.member.domain.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import kr.modusplant.domains.common.context.CrudServiceOnlyContext;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTermTestUtils;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberTermEntityTestUtils;
import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.domain.model.SiteMemberTerm;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberCrudService;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberTermCrudService;
import kr.modusplant.domains.member.mapper.SiteMemberEntityMapper;
import kr.modusplant.domains.member.mapper.SiteMemberEntityMapperImpl;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;
import static kr.modusplant.global.util.VersionUtils.createVersion;
import static kr.modusplant.global.vo.ExceptionMessage.EXISTED_ENTITY;
import static kr.modusplant.global.vo.ExceptionMessage.NOT_FOUND_ENTITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@CrudServiceOnlyContext
class SiteMemberTermCrudServiceImplTest implements SiteMemberTermTestUtils, SiteMemberTermEntityTestUtils, SiteMemberTestUtils, SiteMemberEntityTestUtils {

    private final SiteMemberTermCrudService memberTermService;
    private final SiteMemberCrudService memberService;
    private final SiteMemberTermCrudJpaRepository memberTermRepository;
    private final SiteMemberCrudJpaRepository memberRepository;
    private final SiteMemberTermEntityMapper memberTermMapper = new SiteMemberTermEntityMapperImpl();
    private final SiteMemberEntityMapper memberMapper = new SiteMemberEntityMapperImpl();

    @Autowired
    SiteMemberTermCrudServiceImplTest(SiteMemberTermCrudService memberTermService, SiteMemberCrudService memberService, SiteMemberTermCrudJpaRepository memberTermRepository, SiteMemberCrudJpaRepository memberRepository) {
        this.memberTermService = memberTermService;
        this.memberService = memberService;
        this.memberTermRepository = memberTermRepository;
        this.memberRepository = memberRepository;
    }

    @DisplayName("uuid로 회원 약관 얻기")
    @Test
    void getByUuidTest() {
        // given
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberTermEntity.getMember();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberTerm memberTerm = memberTermMapper.toSiteMemberTerm(memberTermEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberTermRepository.findByUuid(memberTerm.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberTermEntity));
        given(memberTermRepository.save(memberTermEntity)).willReturn(memberTermEntity);

        // when
        memberService.insert(member);
        memberTerm = memberTermService.insert(memberTerm);

        // then
        assertThat(memberTermService.getByUuid(memberTerm.getUuid()).orElseThrow()).isEqualTo(memberTerm);
    }

    @DisplayName("member로 회원 약관 얻기")
    @Test
    void getByMemberTest() {
        // given
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberTermEntity.getMember();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberTerm memberTerm = memberTermMapper.toSiteMemberTerm(memberTermEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberTermRepository.findByUuid(memberTerm.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberTermEntity));
        given(memberTermRepository.save(memberTermEntity)).willReturn(memberTermEntity);

        // when
        memberService.insert(member);
        memberTerm = memberTermService.insert(memberTerm);

        // then
        assertThat(memberTermService.getByMember(member).orElseThrow()).isEqualTo(memberTerm);
    }

    @DisplayName("agreedTermsOfUseVersion으로 회원 약관 얻기")
    @Test
    void getByAgreedTermsOfUseVersionTest() {
        // given
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberTermEntity.getMember();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberTerm memberTerm = memberTermMapper.toSiteMemberTerm(memberTermEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberTermRepository.findByUuid(memberTerm.getUuid())).willReturn(Optional.empty());
        given(memberTermRepository.save(memberTermEntity)).willReturn(memberTermEntity);
        given(memberTermRepository.findByAgreedTermsOfUseVersion(memberTermEntity.getAgreedTermsOfUseVersion())).willReturn(List.of(memberTermEntity));

        // when
        memberService.insert(member);
        memberTerm = memberTermService.insert(memberTerm);

        // then
        assertThat(memberTermService.getByAgreedTermsOfUseVersion(memberTermEntity.getAgreedTermsOfUseVersion()).getFirst()).isEqualTo(memberTerm);
    }

    @DisplayName("agreedPrivacyPolicyVersion으로 회원 약관 얻기")
    @Test
    void getByOriginalMemberUuidTest() {
        // given
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberTermEntity.getMember();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberTerm memberTerm = memberTermMapper.toSiteMemberTerm(memberTermEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberTermRepository.findByUuid(memberTerm.getUuid())).willReturn(Optional.empty());
        given(memberTermRepository.save(memberTermEntity)).willReturn(memberTermEntity);
        given(memberTermRepository.findByAgreedPrivacyPolicyVersion(memberTermEntity.getAgreedPrivacyPolicyVersion())).willReturn(List.of(memberTermEntity));

        // when
        memberService.insert(member);
        memberTerm = memberTermService.insert(memberTerm);

        // then
        assertThat(memberTermService.getByAgreedPrivacyPolicyVersion(memberTermEntity.getAgreedPrivacyPolicyVersion()).getFirst()).isEqualTo(memberTerm);
    }

    @DisplayName("agreedAdInfoReceivingVersion으로 회원 약관 얻기")
    @Test
    void getByEmailTest() {
        // given
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberTermEntity.getMember();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberTerm memberTerm = memberTermMapper.toSiteMemberTerm(memberTermEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberTermRepository.findByUuid(memberTerm.getUuid())).willReturn(Optional.empty());
        given(memberTermRepository.save(memberTermEntity)).willReturn(memberTermEntity);
        given(memberTermRepository.findByAgreedAdInfoReceivingVersion(memberTermEntity.getAgreedAdInfoReceivingVersion())).willReturn(List.of(memberTermEntity));

        // when
        memberService.insert(member);
        memberTerm = memberTermService.insert(memberTerm);

        // then
        assertThat(memberTermService.getByAgreedAdInfoReceivingVersion(memberTerm.getAgreedAdInfoReceivingVersion()).getFirst()).isEqualTo(memberTerm);
    }

    @DisplayName("빈 회원 약관 얻기")
    @Test
    void getOptionalEmptyTest() {
        // given
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();
        UUID uuid = memberTermEntity.getUuid();
        SiteMemberEntity memberEntity = memberTermEntity.getMember();
        SiteMember member = memberMapper.toSiteMember(memberEntity);

        // getByUuid
        // given & when
        given(memberTermRepository.findByUuid(uuid)).willReturn(Optional.empty());

        // then
        assertThat(memberTermService.getByUuid(uuid)).isEmpty();

        // getByMember
        // given & when
        given(memberTermRepository.findByMember(memberEntity)).willReturn(Optional.empty());

        // then
        assertThat(memberTermService.getByMember(member)).isEmpty();
    }

    @DisplayName("회원 약관 삽입 간 검증")
    @Test
    void validateDuringInsertTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        UUID memberEntityUuid = memberEntity.getUuid();
        SiteMemberTermEntity memberTermEntity = SiteMemberTermEntity.builder().member(memberEntity).build();
        SiteMemberTerm memberTerm = memberTermMapper.toSiteMemberTerm(memberTermEntity);

        // Not Found member 검증
        // given & when
        given(memberRepository.findByUuid(memberEntityUuid)).willReturn(Optional.empty());

        // then
        EntityNotFoundException notFoundException = assertThrows(EntityNotFoundWithUuidException.class,
                () -> memberTermService.insert(memberTerm));
        assertThat(notFoundException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY, "uuid", memberEntityUuid, SiteMemberEntity.class));

        // Existed memberTerm 검증
        // given & when
        given(memberRepository.findByUuid(memberEntityUuid)).willReturn(Optional.of(memberEntity));
        given(memberTermRepository.findByUuid(memberEntityUuid)).willReturn(Optional.of(memberTermEntity));

        // then
        EntityExistsException existsException = assertThrows(EntityExistsWithUuidException.class,
                () -> memberTermService.insert(memberTerm));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                EXISTED_ENTITY, "uuid", memberEntityUuid, SiteMemberTermEntity.class));
    }

    @DisplayName("회원 약관 갱신")
    @Test
    void updateTest() {
        // given
        String updatedAgreedTermsOfUseVersion = createVersion(1, 0, 1);
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();
        SiteMemberTermEntity updatedMemberTermEntity = SiteMemberTermEntity.builder().memberTermEntity(memberTermEntity).agreedTermsOfUseVersion(updatedAgreedTermsOfUseVersion).build();
        SiteMemberTerm memberTerm = memberTermMapper.toSiteMemberTerm(memberTermEntity);
        SiteMemberTerm updatedMemberTerm = memberTermMapper.toSiteMemberTerm(updatedMemberTermEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberTermRepository.findByUuid(memberTerm.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(updatedMemberTermEntity));
        given(memberTermRepository.save(memberTermEntity)).willReturn(memberTermEntity);
        given(memberTermRepository.save(updatedMemberTermEntity)).willReturn(updatedMemberTermEntity);
        given(memberTermRepository.findByAgreedTermsOfUseVersion(updatedAgreedTermsOfUseVersion)).willReturn(List.of(updatedMemberTermEntity));

        // when
        memberService.insert(member);
        memberTermService.insert(memberTerm);
        memberTermService.update(updatedMemberTerm);

        // then
        assertThat(memberTermService.getByAgreedTermsOfUseVersion(updatedAgreedTermsOfUseVersion).getFirst()).isEqualTo(updatedMemberTerm);
    }

    @DisplayName("회원 약관 갱신 간 검증")
    @Test
    void validateDuringUpdateTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        UUID memberEntityUuid = memberEntity.getUuid();
        SiteMemberTermEntity memberTermEntity = SiteMemberTermEntity.builder().member(memberEntity).build();
        SiteMemberTerm memberTerm = memberTermMapper.toSiteMemberTerm(memberTermEntity);

        // Not Found member 검증
        // given & when
        given(memberRepository.findByUuid(memberEntityUuid)).willReturn(Optional.empty());

        // then
        EntityNotFoundException notFoundException = assertThrows(EntityNotFoundWithUuidException.class,
                () -> memberTermService.update(memberTerm));
        assertThat(notFoundException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY, "uuid", memberEntityUuid, SiteMemberEntity.class));

        // Not Found memberTerm 검증
        // given & when
        given(memberRepository.findByUuid(memberEntityUuid)).willReturn(Optional.of(memberEntity));
        given(memberTermRepository.findByUuid(memberEntityUuid)).willReturn(Optional.empty());

        // then
        notFoundException = assertThrows(EntityNotFoundWithUuidException.class,
                () -> memberTermService.update(memberTerm));
        assertThat(notFoundException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY, "uuid", memberEntityUuid, SiteMemberTermEntity.class));
    }

    @DisplayName("uuid로 회원 약관 제거")
    @Test
    void removeByUuidTest() {
        // given
        SiteMemberTermEntity memberTermEntity = createMemberTermUserEntityWithUuid();
        SiteMemberEntity memberEntity = memberTermEntity.getMember();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberTerm memberTerm = memberTermMapper.toSiteMemberTerm(memberTermEntity);
        UUID uuid = memberTerm.getUuid();

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberTermRepository.findByUuid(uuid)).willReturn(Optional.empty()).willReturn(Optional.of(memberTermEntity)).willReturn(Optional.empty());
        given(memberTermRepository.save(memberTermEntity)).willReturn(memberTermEntity);
        willDoNothing().given(memberTermRepository).deleteByUuid(uuid);

        // when
        memberService.insert(member);
        memberTermService.insert(memberTerm);
        memberTermService.removeByUuid(uuid);

        // then
        assertThat(memberTermService.getByUuid(uuid)).isEmpty();
    }

    @DisplayName("uuid로 회원 역할 제거 간 검증")
    @Test
    void validateDuringRemoveByUuidTest() {
        // given & when
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        UUID memberEntityUuid = memberEntity.getUuid();

        given(memberTermRepository.findByUuid(memberEntityUuid)).willReturn(Optional.empty());

        // then
        EntityNotFoundException notFoundException = assertThrows(EntityNotFoundWithUuidException.class,
                () -> memberTermService.removeByUuid(memberEntityUuid));
        assertThat(notFoundException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY, "uuid", memberEntityUuid, SiteMemberTermEntity.class));
    }
}