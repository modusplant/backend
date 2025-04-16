package kr.modusplant.domains.member.domain.service;

import kr.modusplant.domains.common.context.DomainServiceOnlyContext;
import kr.modusplant.domains.member.common.util.domain.SiteMemberAuthTestUtils;
import kr.modusplant.domains.member.common.util.domain.SiteMemberTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberAuthEntityTestUtils;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.domain.model.SiteMember;
import kr.modusplant.domains.member.domain.model.SiteMemberAuth;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberAuthCrudService;
import kr.modusplant.domains.member.domain.service.supers.SiteMemberCrudService;
import kr.modusplant.domains.member.enums.AuthProvider;
import kr.modusplant.domains.member.mapper.SiteMemberAuthEntityMapper;
import kr.modusplant.domains.member.mapper.SiteMemberAuthEntityMapperImpl;
import kr.modusplant.domains.member.mapper.SiteMemberEntityMapper;
import kr.modusplant.domains.member.mapper.SiteMemberEntityMapperImpl;
import kr.modusplant.domains.member.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberAuthCrudJpaRepository;
import kr.modusplant.domains.member.persistence.repository.SiteMemberCrudJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@DomainServiceOnlyContext
class SiteMemberAuthCrudServiceImplTest implements SiteMemberAuthTestUtils, SiteMemberAuthEntityTestUtils, SiteMemberTestUtils, SiteMemberEntityTestUtils {

    private final SiteMemberAuthCrudService memberAuthService;
    private final SiteMemberCrudService memberService;
    private final SiteMemberAuthCrudJpaRepository memberAuthRepository;
    private final SiteMemberCrudJpaRepository memberRepository;
    private final SiteMemberAuthEntityMapper memberAuthMapper = new SiteMemberAuthEntityMapperImpl();
    private final SiteMemberEntityMapper memberMapper = new SiteMemberEntityMapperImpl();

    @Autowired
    SiteMemberAuthCrudServiceImplTest(SiteMemberAuthCrudService memberAuthService, SiteMemberCrudService memberService, SiteMemberAuthCrudJpaRepository memberAuthRepository, SiteMemberCrudJpaRepository memberRepository) {
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

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.findByUuid(memberAuthEntity.getUuid())).willReturn(Optional.of(memberAuthEntity));
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

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.findByUuid(memberAuthEntity.getUuid())).willReturn(Optional.of(memberAuthEntity));
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

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.findByUuid(memberAuthEntity.getUuid())).willReturn(Optional.of(memberAuthEntity));
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);
        given(memberAuthRepository.findByOriginalMember(memberEntity)).willReturn(Optional.of(memberAuthEntity));

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

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.findByUuid(memberAuthEntity.getUuid())).willReturn(Optional.of(memberAuthEntity));
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

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.findByUuid(memberAuthEntity.getUuid())).willReturn(Optional.of(memberAuthEntity));
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

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.findByUuid(memberAuthEntity.getUuid())).willReturn(Optional.of(memberAuthEntity));
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

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.findByUuid(memberAuthEntity.getUuid())).willReturn(Optional.of(memberAuthEntity));
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

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.findByUuid(memberAuthEntity.getUuid())).willReturn(Optional.of(memberAuthEntity));
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

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.findByUuid(memberAuthEntity.getUuid())).willReturn(Optional.of(memberAuthEntity));
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

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.findByUuid(memberAuthEntity.getUuid())).willReturn(Optional.of(memberAuthEntity));
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);
        given(memberAuthRepository.findByEmail(updatedEmail)).willReturn(List.of(updatedMemberAuthEntity));

        // when
        memberService.insert(member);
        memberAuthService.insert(memberAuth);
        memberAuthService.update(updatedMemberAuth);

        // then
        assertThat(memberAuthService.getByEmail(updatedEmail).getFirst()).isEqualTo(updatedMemberAuth);
    }

    @DisplayName("uuid로 회원 인증 제거")
    @Test
    void removeByUuidTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberAuthEntity memberAuthEntity = createMemberAuthBasicUserEntityWithUuidBuilder().activeMember(memberEntity).originalMember(memberEntity).build();
        SiteMemberAuth memberAuth = memberAuthMapper.toSiteMemberAuth(memberAuthEntity);
        UUID uuid = memberAuth.getUuid();

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.of(memberEntity)).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);
        given(memberAuthRepository.findByUuid(memberAuthEntity.getUuid())).willReturn(Optional.empty());
        willDoNothing().given(memberAuthRepository).deleteByUuid(memberAuthEntity.getUuid());

        // when
        memberService.insert(member);
        memberAuthService.insert(memberAuth);
        memberAuthService.removeByUuid(uuid);

        // then
        assertThat(memberAuthService.getByUuid(uuid)).isEmpty();
    }
}