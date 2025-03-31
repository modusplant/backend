package kr.modusplant.global.persistence.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import kr.modusplant.global.domain.model.SiteMember;
import kr.modusplant.global.domain.model.SiteMemberAuth;
import kr.modusplant.global.domain.service.crud.SiteMemberAuthService;
import kr.modusplant.global.domain.service.crud.SiteMemberService;
import kr.modusplant.global.enums.AuthProvider;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import kr.modusplant.global.mapper.SiteMemberAuthEntityMapper;
import kr.modusplant.global.mapper.SiteMemberAuthEntityMapperImpl;
import kr.modusplant.global.mapper.SiteMemberEntityMapper;
import kr.modusplant.global.mapper.SiteMemberEntityMapperImpl;
import kr.modusplant.global.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.global.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.persistence.repository.SiteMemberAuthJpaRepository;
import kr.modusplant.global.persistence.repository.SiteMemberJpaRepository;
import kr.modusplant.support.context.ServiceOnlyContext;
import kr.modusplant.support.util.domain.SiteMemberAuthTestUtils;
import kr.modusplant.support.util.domain.SiteMemberTestUtils;
import kr.modusplant.support.util.entity.SiteMemberAuthEntityTestUtils;
import kr.modusplant.support.util.entity.SiteMemberEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;
import static kr.modusplant.global.vo.CamelCaseWord.ACTIVE_MEMBER_UUID;
import static kr.modusplant.global.vo.CamelCaseWord.ORIGINAL_MEMBER_UUID;
import static kr.modusplant.global.vo.ExceptionMessage.EXISTED_ENTITY;
import static kr.modusplant.global.vo.ExceptionMessage.NOT_FOUND_ENTITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@ServiceOnlyContext
class SiteMemberAuthServiceImplTest implements SiteMemberAuthTestUtils, SiteMemberAuthEntityTestUtils, SiteMemberTestUtils, SiteMemberEntityTestUtils {

    private final SiteMemberAuthService memberAuthService;
    private final SiteMemberService memberService;
    private final SiteMemberAuthJpaRepository memberAuthRepository;
    private final SiteMemberJpaRepository memberRepository;
    private final SiteMemberAuthEntityMapper memberAuthMapper = new SiteMemberAuthEntityMapperImpl();
    private final SiteMemberEntityMapper memberMapper = new SiteMemberEntityMapperImpl();

    @Autowired
    SiteMemberAuthServiceImplTest(SiteMemberAuthService memberAuthService, SiteMemberService memberService, SiteMemberAuthJpaRepository memberAuthRepository, SiteMemberJpaRepository memberRepository) {
        this.memberAuthService = memberAuthService;
        this.memberService = memberService;
        this.memberAuthRepository = memberAuthRepository;
        this.memberRepository = memberRepository;
    }

    @DisplayName("uuid로 회원 인증 얻기")
    @Test
    void getByUuidTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberAuthEntity memberAuthEntity = createMemberAuthBasicUserEntityWithUuidBuilder().activeMember(memberEntity).originalMember(memberEntity).build();
        SiteMemberAuth memberAuth = memberAuthMapper.toSiteMemberAuth(memberAuthEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.findByUuid(memberAuthEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberAuthEntity));
        given(memberAuthRepository.findByOriginalMember(memberEntity)).willReturn(Optional.empty()).willReturn(Optional.of(memberAuthEntity));
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);

        // when
        memberService.insert(member);
        memberAuth = memberAuthService.insert(memberAuth);

        // then
        assertThat(memberAuthService.getByUuid(memberAuth.getUuid()).orElseThrow()).isEqualTo(memberAuth);
    }

    @DisplayName("activeMemberUuid로 회원 인증 얻기")
    @Test
    void getByActiveMemberUuidTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberAuthEntity memberAuthEntity = createMemberAuthBasicUserEntityWithUuidBuilder().activeMember(memberEntity).originalMember(memberEntity).build();
        SiteMemberAuth memberAuth = memberAuthMapper.toSiteMemberAuth(memberAuthEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.findByUuid(memberAuthEntity.getUuid())).willReturn(Optional.empty());
        given(memberAuthRepository.findByOriginalMember(memberEntity)).willReturn(Optional.empty()).willReturn(Optional.of(memberAuthEntity));
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);
        given(memberAuthRepository.findByActiveMember(memberEntity)).willReturn(List.of(memberAuthEntity));

        // when
        memberService.insert(member);
        memberAuth = memberAuthService.insert(memberAuth);

        // then
        assertThat(memberAuthService.getByActiveMember(member).getFirst()).isEqualTo(memberAuth);
    }

    @DisplayName("originalMemberUuid로 회원 인증 얻기")
    @Test
    void getByOriginalMemberUuidTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberAuthEntity memberAuthEntity = createMemberAuthBasicUserEntityWithUuidBuilder().activeMember(memberEntity).originalMember(memberEntity).build();
        SiteMemberAuth memberAuth = memberAuthMapper.toSiteMemberAuth(memberAuthEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.findByUuid(memberAuthEntity.getUuid())).willReturn(Optional.empty());
        given(memberAuthRepository.findByOriginalMember(memberEntity)).willReturn(Optional.empty()).willReturn(Optional.of(memberAuthEntity));
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);

        // when
        memberService.insert(member);
        memberAuth = memberAuthService.insert(memberAuth);

        // then
        assertThat(memberAuthService.getByOriginalMember(member).orElseThrow()).isEqualTo(memberAuth);
    }

    @DisplayName("email로 회원 인증 얻기")
    @Test
    void getByEmailTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberAuthEntity memberAuthEntity = createMemberAuthBasicUserEntityWithUuidBuilder().activeMember(memberEntity).originalMember(memberEntity).build();
        SiteMemberAuth memberAuth = memberAuthMapper.toSiteMemberAuth(memberAuthEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.findByUuid(memberAuthEntity.getUuid())).willReturn(Optional.empty());
        given(memberAuthRepository.findByOriginalMember(memberEntity)).willReturn(Optional.empty()).willReturn(Optional.of(memberAuthEntity));
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);
        given(memberAuthRepository.findByEmail(memberAuthEntity.getEmail())).willReturn(List.of(memberAuthEntity));

        // when
        memberService.insert(member);
        memberAuth = memberAuthService.insert(memberAuth);

        // then
        assertThat(memberAuthService.getByEmail(memberAuth.getEmail()).getFirst()).isEqualTo(memberAuth);
    }

    @DisplayName("provider로 회원 인증 얻기")
    @Test
    void getByProviderTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberAuthEntity memberAuthEntity = createMemberAuthBasicUserEntityWithUuidBuilder().activeMember(memberEntity).originalMember(memberEntity).build();
        SiteMemberAuth memberAuth = memberAuthMapper.toSiteMemberAuth(memberAuthEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.findByUuid(memberAuthEntity.getUuid())).willReturn(Optional.empty());
        given(memberAuthRepository.findByOriginalMember(memberEntity)).willReturn(Optional.empty()).willReturn(Optional.of(memberAuthEntity));
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);
        given(memberAuthRepository.findByProvider(memberAuthEntity.getProvider())).willReturn(List.of(memberAuthEntity));

        // when
        memberService.insert(member);
        memberAuth = memberAuthService.insert(memberAuth);

        // then
        assertThat(memberAuthService.getByProvider(memberAuth.getProvider()).getFirst()).isEqualTo(memberAuth);
    }

    @DisplayName("providerId로 회원 인증 얻기")
    @Test
    void getByProviderIdTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberAuthEntity memberAuthEntity = createMemberAuthBasicUserEntityWithUuidBuilder().activeMember(memberEntity).originalMember(memberEntity).build();
        SiteMemberAuth memberAuth = memberAuthMapper.toSiteMemberAuth(memberAuthEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.findByUuid(memberAuthEntity.getUuid())).willReturn(Optional.empty());
        given(memberAuthRepository.findByOriginalMember(memberEntity)).willReturn(Optional.empty()).willReturn(Optional.of(memberAuthEntity));
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);
        given(memberAuthRepository.findByProviderId(memberAuthEntity.getProviderId())).willReturn(List.of(memberAuthEntity));

        // when
        memberService.insert(member);
        memberAuth = memberAuthService.insert(memberAuth);

        // then
        assertThat(memberAuthService.getByProviderId(memberAuth.getProviderId()).getFirst()).isEqualTo(memberAuth);
    }

    @DisplayName("email과 provider로 회원 인증 얻기")
    @Test
    void getByEmailAndProviderTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberAuthEntity memberAuthEntity = createMemberAuthBasicUserEntityWithUuidBuilder().activeMember(memberEntity).originalMember(memberEntity).build();
        SiteMemberAuth memberAuth = memberAuthMapper.toSiteMemberAuth(memberAuthEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.findByUuid(memberAuthEntity.getUuid())).willReturn(Optional.empty());
        given(memberAuthRepository.findByOriginalMember(memberEntity)).willReturn(Optional.empty()).willReturn(Optional.of(memberAuthEntity));
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);
        given(memberAuthRepository.findByEmailAndProvider(memberAuthEntity.getEmail(), memberAuthEntity.getProvider())).willReturn(Optional.of(memberAuthEntity));

        // when
        memberService.insert(member);
        memberAuth = memberAuthService.insert(memberAuth);

        // then
        assertThat(memberAuthService.getByEmailAndProvider(memberAuth.getEmail(), memberAuth.getProvider()).orElseThrow()).isEqualTo(memberAuth);
    }

    @DisplayName("provider와 providerId로 회원 인증 얻기")
    @Test
    void getByProviderAndProviderIdTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberAuthEntity memberAuthEntity = createMemberAuthBasicUserEntityWithUuidBuilder().activeMember(memberEntity).originalMember(memberEntity).build();
        SiteMemberAuth memberAuth = memberAuthMapper.toSiteMemberAuth(memberAuthEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.findByUuid(memberAuthEntity.getUuid())).willReturn(Optional.empty());
        given(memberAuthRepository.findByOriginalMember(memberEntity)).willReturn(Optional.empty()).willReturn(Optional.of(memberAuthEntity));
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);
        given(memberAuthRepository.findByProviderAndProviderId(memberAuthEntity.getProvider(), memberAuthEntity.getProviderId())).willReturn(Optional.of(memberAuthEntity));

        // when
        memberService.insert(member);
        memberAuth = memberAuthService.insert(memberAuth);

        // then
        assertThat(memberAuthService.getByProviderAndProviderId(memberAuth.getProvider(), memberAuth.getProviderId()).orElseThrow()).isEqualTo(memberAuth);
    }

    @DisplayName("failedAttempt로 회원 인증 얻기")
    @Test
    void getByFailedAttemptTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberAuthEntity memberAuthEntity = createMemberAuthBasicUserEntityWithUuidBuilder().activeMember(memberEntity).originalMember(memberEntity).build();
        SiteMemberAuth memberAuth = memberAuthMapper.toSiteMemberAuth(memberAuthEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.findByUuid(memberAuthEntity.getUuid())).willReturn(Optional.empty());
        given(memberAuthRepository.findByOriginalMember(memberEntity)).willReturn(Optional.empty()).willReturn(Optional.of(memberAuthEntity));
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);
        given(memberAuthRepository.findByFailedAttempt(memberAuthEntity.getFailedAttempt())).willReturn(List.of(memberAuthEntity));

        // when
        memberService.insert(member);
        memberAuth = memberAuthService.insert(memberAuth);

        // then
        assertThat(memberAuthService.getByFailedAttempt(memberAuth.getFailedAttempt()).getFirst()).isEqualTo(memberAuth);
    }

    @DisplayName("빈 회원 인증 얻기")
    @Test
    void getOptionalEmptyTest() {
        // given
        SiteMemberAuth memberAuth = memberAuthBasicUserWithUuid;
        UUID uuid = memberAuth.getUuid();
        UUID originalMemberUuid = memberAuth.getOriginalMemberUuid();
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        String email = memberAuth.getEmail();
        AuthProvider provider = memberAuth.getProvider();
        String providerId = memberAuth.getProviderId();

        // getByUuid
        // given & when
        given(memberAuthRepository.findByUuid(uuid)).willReturn(Optional.empty());

        // then
        assertThat(memberAuthService.getByUuid(uuid)).isEmpty();

        // getByOriginalMember
        // given & when
        given(memberRepository.findByUuid(originalMemberUuid)).willReturn(Optional.of(memberEntity));
        given(memberAuthRepository.findByOriginalMember(memberEntity)).willReturn(Optional.empty());

        // then
        assertThat(memberAuthService.getByOriginalMember(memberBasicUserWithUuid)).isEmpty();

        // getByEmailAndProvider
        // given & when
        given(memberAuthRepository.findByEmailAndProvider(email, provider)).willReturn(Optional.empty());

        // then
        assertThat(memberAuthService.getByEmailAndProvider(email, provider)).isEmpty();

        // getByProviderAndProviderId
        // given & when
        given(memberAuthRepository.findByProviderAndProviderId(provider, providerId)).willReturn(Optional.empty());

        // then
        assertThat(memberAuthService.getByProviderAndProviderId(provider, providerId)).isEmpty();
    }

    @DisplayName("회원 인증 삽입 간 검증")
    @Test
    void validateDuringInsertTest() {
        // given
        SiteMemberEntity activeMemberEntity = createMemberBasicUserEntityWithUuid();
        UUID activeMemberEntityUuid = activeMemberEntity.getUuid();
        SiteMemberEntity originalMemberEntity = SiteMemberEntity.builder().memberEntity(activeMemberEntity).uuid(UUID.randomUUID()).build();
        UUID originalMemberEntityUuid = originalMemberEntity.getUuid();
        SiteMemberAuthEntity memberAuthEntity = createMemberAuthBasicUserEntityWithUuidBuilder().activeMember(activeMemberEntity).originalMember(originalMemberEntity).build();
        UUID memberAuthEntityUuid = memberAuthEntity.getUuid();
        SiteMemberAuth memberAuth = memberAuthMapper.toSiteMemberAuth(memberAuthEntity);

        // Not Found activeMember 검증
        // given & when
        given(memberRepository.findByUuid(activeMemberEntityUuid)).willReturn(Optional.empty());

        // then
        EntityNotFoundException notFoundException = assertThrows(EntityNotFoundException.class,
                () -> memberAuthService.insert(memberAuth));
        assertThat(notFoundException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY, ACTIVE_MEMBER_UUID, activeMemberEntityUuid, SiteMemberEntity.class));

        // Not Found originalMember 검증
        // given & when
        given(memberRepository.findByUuid(activeMemberEntityUuid)).willReturn(Optional.of(activeMemberEntity));
        given(memberRepository.findByUuid(originalMemberEntityUuid)).willReturn(Optional.empty());

        // then
        notFoundException = assertThrows(EntityNotFoundException.class,
                () -> memberAuthService.insert(memberAuth));
        assertThat(notFoundException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY, ORIGINAL_MEMBER_UUID, originalMemberEntityUuid, SiteMemberEntity.class));

        // Existed memberAuth 검증
        // given & when
        given(memberRepository.findByUuid(originalMemberEntityUuid)).willReturn(Optional.of(originalMemberEntity));
        given(memberAuthRepository.findByUuid(memberAuthEntityUuid)).willReturn(Optional.of(memberAuthEntity));

        // then
        EntityExistsException existsException = assertThrows(EntityExistsWithUuidException.class,
                () -> memberAuthService.insert(memberAuth));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                EXISTED_ENTITY, "uuid", memberAuthEntityUuid, SiteMemberAuthEntity.class));

        // Existed originalMember 검증
        // given & when
        given(memberAuthRepository.findByUuid(memberAuthEntityUuid)).willReturn(Optional.empty());
        given(memberAuthRepository.findByOriginalMember(originalMemberEntity)).willReturn(Optional.of(memberAuthEntity));

        // then
        existsException = assertThrows(EntityExistsException.class,
                () -> memberAuthService.insert(memberAuth));
        assertThat(existsException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                EXISTED_ENTITY, ORIGINAL_MEMBER_UUID, originalMemberEntityUuid, SiteMemberAuthEntity.class));
    }

    @DisplayName("회원 인증 갱신")
    @Test
    void updateTest() {
        // given
        String updatedEmail = "updatedEmail1@naver.com";
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberAuthEntity memberAuthEntity = createMemberAuthBasicUserEntityWithUuidBuilder().activeMember(memberEntity).originalMember(memberEntity).build();
        SiteMemberAuthEntity updatedMemberAuthEntity = SiteMemberAuthEntity.builder().memberAuthEntity(memberAuthEntity).email(updatedEmail).build();
        SiteMemberAuth memberAuth = memberAuthMapper.toSiteMemberAuth(memberAuthEntity);
        SiteMemberAuth updatedMemberAuth = memberAuthMapper.toSiteMemberAuth(updatedMemberAuthEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.findByUuid(memberAuthEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberAuthEntity));
        given(memberAuthRepository.findByOriginalMember(memberEntity)).willReturn(Optional.empty());
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity).willReturn(updatedMemberAuthEntity);
        given(memberAuthRepository.findByEmail(updatedEmail)).willReturn(List.of(updatedMemberAuthEntity));

        // when
        memberService.insert(member);
        memberAuthService.insert(memberAuth);
        memberAuthService.update(updatedMemberAuth);

        // then
        assertThat(memberAuthService.getByEmail(updatedEmail).getFirst()).isEqualTo(updatedMemberAuth);
    }

    @DisplayName("회원 인증 갱신 간 검증")
    @Test
    void validateDuringUpdateTest() {
        // given
        SiteMemberEntity activeMemberEntity = createMemberBasicUserEntityWithUuid();
        UUID activeMemberEntityUuid = activeMemberEntity.getUuid();
        SiteMemberEntity originalMemberEntity = SiteMemberEntity.builder().memberEntity(activeMemberEntity).uuid(UUID.randomUUID()).build();
        UUID originalMemberEntityUuid = originalMemberEntity.getUuid();
        SiteMemberAuthEntity memberAuthEntity = createMemberAuthBasicUserEntityWithUuidBuilder().activeMember(activeMemberEntity).originalMember(originalMemberEntity).build();
        UUID memberAuthEntityUuid = memberAuthEntity.getUuid();
        SiteMemberAuth memberAuth = memberAuthMapper.toSiteMemberAuth(memberAuthEntity);

        // Not Found activeMember 검증
        // given & when
        given(memberRepository.findByUuid(activeMemberEntityUuid)).willReturn(Optional.empty());

        // then
        EntityNotFoundException notFoundException = assertThrows(EntityNotFoundException.class,
                () -> memberAuthService.update(memberAuth));
        assertThat(notFoundException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY, ACTIVE_MEMBER_UUID, activeMemberEntityUuid, SiteMemberEntity.class));

        // Not Found originalMember 검증
        // given & when
        given(memberRepository.findByUuid(activeMemberEntityUuid)).willReturn(Optional.of(activeMemberEntity));
        given(memberRepository.findByUuid(originalMemberEntityUuid)).willReturn(Optional.empty());

        // then
        notFoundException = assertThrows(EntityNotFoundException.class,
                () -> memberAuthService.update(memberAuth));
        assertThat(notFoundException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY, ORIGINAL_MEMBER_UUID, originalMemberEntityUuid, SiteMemberEntity.class));

        // Not Found memberAuth 검증
        // given & when
        given(memberRepository.findByUuid(originalMemberEntityUuid)).willReturn(Optional.of(originalMemberEntity));
        given(memberAuthRepository.findByUuid(memberAuthEntityUuid)).willReturn(Optional.empty());

        // then
        notFoundException = assertThrows(EntityNotFoundWithUuidException.class,
                () -> memberAuthService.update(memberAuth));
        assertThat(notFoundException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY, "uuid", memberAuthEntityUuid, SiteMemberAuthEntity.class));
    }

    @DisplayName("uuid로 회원 인증 제거")
    @Test
    void removeByUuidTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberAuthEntity memberAuthEntity = createMemberAuthBasicUserEntityWithUuidBuilder().activeMember(memberEntity).originalMember(memberEntity).build();
        SiteMemberAuth memberAuth = memberAuthMapper.toSiteMemberAuth(memberAuthEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.findByUuid(memberAuthEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberAuthEntity));
        given(memberAuthRepository.findByOriginalMember(memberEntity)).willReturn(Optional.empty()).willReturn(Optional.of(memberAuthEntity));
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);
        given(memberAuthRepository.findAll()).willReturn(emptyList());
        willDoNothing().given(memberAuthRepository).deleteByUuid(memberAuthEntity.getUuid());

        // when
        memberService.insert(member);
        memberAuth = memberAuthService.insert(memberAuth);
        memberAuthService.removeByUuid(memberAuth.getUuid());

        // then
        assertThat(memberAuthService.getAll()).isEmpty();
    }

    @DisplayName("uuid로 회원 인증 제거 간 검증")
    @Test
    void validateDuringRemoveByUuidTest() {
        // given & when
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMemberAuthEntity memberAuthEntity = createMemberAuthBasicUserEntityWithUuidBuilder().activeMember(memberEntity).originalMember(memberEntity).build();
        UUID memberAuthEntityUuid = memberAuthEntity.getUuid();

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(memberAuthRepository.findByUuid(memberAuthEntityUuid)).willReturn(Optional.empty());

        // then
        EntityNotFoundException notFoundException = assertThrows(EntityNotFoundWithUuidException.class,
                () -> memberAuthService.removeByUuid(memberAuthEntityUuid));
        assertThat(notFoundException.getMessage()).isEqualTo(getFormattedExceptionMessage(
                NOT_FOUND_ENTITY, "uuid", memberAuthEntityUuid, SiteMemberAuthEntity.class));
    }
}