package kr.modusplant.legacy.domains.member.app.service;

import kr.modusplant.framework.out.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.out.persistence.entity.SiteMemberEntity;
import kr.modusplant.framework.out.persistence.repository.SiteMemberAuthRepository;
import kr.modusplant.framework.out.persistence.repository.SiteMemberRepository;
import kr.modusplant.legacy.domains.common.context.DomainsServiceWithoutValidationServiceContext;
import kr.modusplant.legacy.domains.member.app.http.request.SiteMemberAuthUpdateRequest;
import kr.modusplant.legacy.domains.member.app.http.response.SiteMemberAuthResponse;
import kr.modusplant.legacy.domains.member.common.util.app.http.request.SiteMemberAuthRequestTestUtils;
import kr.modusplant.legacy.domains.member.common.util.app.http.request.SiteMemberRequestTestUtils;
import kr.modusplant.legacy.domains.member.common.util.app.http.response.SiteMemberAuthResponseTestUtils;
import kr.modusplant.legacy.domains.member.common.util.app.http.response.SiteMemberResponseTestUtils;
import kr.modusplant.legacy.domains.member.common.util.entity.SiteMemberAuthEntityTestUtils;
import kr.modusplant.legacy.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.legacy.domains.member.domain.model.SiteMemberAuth;
import kr.modusplant.legacy.domains.member.enums.AuthProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@DomainsServiceWithoutValidationServiceContext
class SiteMemberAuthApplicationServiceTest implements SiteMemberAuthRequestTestUtils, SiteMemberAuthResponseTestUtils, SiteMemberAuthEntityTestUtils, SiteMemberRequestTestUtils, SiteMemberResponseTestUtils, SiteMemberEntityTestUtils {

    private final SiteMemberAuthApplicationService memberAuthService;
    private final SiteMemberApplicationService memberService;
    private final SiteMemberAuthRepository memberAuthRepository;
    private final SiteMemberRepository memberRepository;

    @Autowired
    SiteMemberAuthApplicationServiceTest(SiteMemberAuthApplicationService memberAuthService, SiteMemberApplicationService memberService, SiteMemberAuthRepository memberAuthRepository, SiteMemberRepository memberRepository) {
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
        SiteMemberAuthEntity memberAuthEntity = this.createMemberAuthBasicUserEntityBuilder().originalMember(memberEntity).activeMember(memberEntity).build();
        UUID uuid = memberEntity.getUuid();

        given(memberRepository.findByUuid(uuid)).willReturn(Optional.of(memberEntity));
        given(memberRepository.existsByUuid(uuid)).willReturn(true);
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.existsByOriginalMember(memberEntity)).willReturn(false);
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);
        given(memberAuthRepository.findByUuid(uuid)).willReturn(Optional.of(memberAuthEntity));

        // when
        memberService.insert(memberBasicUserInsertRequest);
        SiteMemberAuthResponse memberAuthResponse = memberAuthService.insert(memberAuthBasicUserInsertRequest);

        // then
        assertThat(memberAuthService.getByUuid(memberAuthResponse.originalMemberUuid()).orElseThrow()).isEqualTo(memberAuthResponse);
    }

    @DisplayName("activeMember로 회원 인증 얻기")
    @Test
    void getByActiveMemberTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMemberAuthEntity memberAuthEntity = this.createMemberAuthBasicUserEntityBuilder().originalMember(memberEntity).activeMember(memberEntity).build();
        UUID uuid = memberEntity.getUuid();

        given(memberRepository.findByUuid(uuid)).willReturn(Optional.of(memberEntity));
        given(memberRepository.existsByUuid(uuid)).willReturn(true);
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.existsByOriginalMember(memberEntity)).willReturn(false);
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);
        given(memberAuthRepository.findByActiveMember(memberEntity)).willReturn(List.of(memberAuthEntity));

        // when
        memberService.insert(memberBasicUserInsertRequest);
        SiteMemberAuthResponse memberAuthResponse = memberAuthService.insert(memberAuthBasicUserInsertRequest);

        // then
        assertThat(memberAuthService.getByActiveMember(memberEntity).getFirst()).isEqualTo(memberAuthResponse);
    }

    @DisplayName("originalMemberUuid로 회원 인증 얻기")
    @Test
    void getByOriginalMemberUuidTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMemberAuthEntity memberAuthEntity = this.createMemberAuthBasicUserEntityBuilder().originalMember(memberEntity).activeMember(memberEntity).build();
        UUID uuid = memberEntity.getUuid();

        given(memberRepository.findByUuid(uuid)).willReturn(Optional.of(memberEntity));
        given(memberRepository.existsByUuid(uuid)).willReturn(true);
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.existsByOriginalMember(memberEntity)).willReturn(false);
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);
        given(memberAuthRepository.findByOriginalMember(memberEntity)).willReturn(Optional.of(memberAuthEntity));

        // when
        memberService.insert(memberBasicUserInsertRequest);
        SiteMemberAuthResponse memberAuthResponse = memberAuthService.insert(memberAuthBasicUserInsertRequest);

        // then
        assertThat(memberAuthService.getByOriginalMember(memberEntity).orElseThrow()).isEqualTo(memberAuthResponse);
    }

    @DisplayName("email로 회원 인증 얻기")
    @Test
    void getByEmailTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMemberAuthEntity memberAuthEntity = this.createMemberAuthBasicUserEntityBuilder().originalMember(memberEntity).activeMember(memberEntity).build();
        UUID uuid = memberEntity.getUuid();

        given(memberRepository.findByUuid(uuid)).willReturn(Optional.of(memberEntity));
        given(memberRepository.existsByUuid(uuid)).willReturn(true);
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.existsByOriginalMember(memberEntity)).willReturn(false);
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);
        given(memberAuthRepository.findByEmail(memberAuthEntity.getEmail())).willReturn(List.of(memberAuthEntity));

        // when
        memberService.insert(memberBasicUserInsertRequest);
        SiteMemberAuthResponse memberAuthResponse = memberAuthService.insert(memberAuthBasicUserInsertRequest);

        // then
        assertThat(memberAuthService.getByEmail(memberAuthEntity.getEmail()).getFirst()).isEqualTo(memberAuthResponse);
    }

    @DisplayName("provider로 회원 인증 얻기")
    @Test
    void getByProviderTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMemberAuthEntity memberAuthEntity = this.createMemberAuthBasicUserEntityBuilder().originalMember(memberEntity).activeMember(memberEntity).build();
        UUID uuid = memberEntity.getUuid();

        given(memberRepository.findByUuid(uuid)).willReturn(Optional.of(memberEntity));
        given(memberRepository.existsByUuid(uuid)).willReturn(true);
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.existsByOriginalMember(memberEntity)).willReturn(false);
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);
        given(memberAuthRepository.findByProvider(memberAuthEntity.getProvider())).willReturn(List.of(memberAuthEntity));

        // when
        memberService.insert(memberBasicUserInsertRequest);
        SiteMemberAuthResponse memberAuthResponse = memberAuthService.insert(memberAuthBasicUserInsertRequest);

        // then
        assertThat(memberAuthService.getByProvider(memberAuthEntity.getProvider()).getFirst()).isEqualTo(memberAuthResponse);
    }

    @DisplayName("providerId로 회원 인증 얻기")
    @Test
    void getByProviderIdTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMemberAuthEntity memberAuthEntity = this.createMemberAuthBasicUserEntityBuilder().originalMember(memberEntity).activeMember(memberEntity).build();
        UUID uuid = memberEntity.getUuid();

        given(memberRepository.findByUuid(uuid)).willReturn(Optional.of(memberEntity));
        given(memberRepository.existsByUuid(uuid)).willReturn(true);
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.existsByOriginalMember(memberEntity)).willReturn(false);
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);
        given(memberAuthRepository.findByProviderId(memberAuthEntity.getProviderId())).willReturn(List.of(memberAuthEntity));

        // when
        memberService.insert(memberBasicUserInsertRequest);
        SiteMemberAuthResponse memberAuthResponse = memberAuthService.insert(memberAuthBasicUserInsertRequest);

        // then
        assertThat(memberAuthService.getByProviderId(memberAuthEntity.getProviderId()).getFirst()).isEqualTo(memberAuthResponse);
    }

    @DisplayName("email과 provider로 회원 인증 얻기")
    @Test
    void getByEmailAndProviderTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMemberAuthEntity memberAuthEntity = this.createMemberAuthBasicUserEntityBuilder().originalMember(memberEntity).activeMember(memberEntity).build();
        UUID uuid = memberEntity.getUuid();

        given(memberRepository.findByUuid(uuid)).willReturn(Optional.of(memberEntity));
        given(memberRepository.existsByUuid(uuid)).willReturn(true);
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.existsByOriginalMember(memberEntity)).willReturn(false);
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);
        given(memberAuthRepository.findByEmailAndProvider(memberAuthEntity.getEmail(), memberAuthEntity.getProvider())).willReturn(Optional.of(memberAuthEntity));

        // when
        memberService.insert(memberBasicUserInsertRequest);
        SiteMemberAuthResponse memberAuthResponse = memberAuthService.insert(memberAuthBasicUserInsertRequest);

        // then
        assertThat(memberAuthService.getByEmailAndProvider(memberAuthEntity.getEmail(), memberAuthEntity.getProvider()).orElseThrow()).isEqualTo(memberAuthResponse);
    }

    @DisplayName("provider와 providerId로 회원 인증 얻기")
    @Test
    void getByProviderAndProviderIdTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMemberAuthEntity memberAuthEntity = this.createMemberAuthBasicUserEntityBuilder().originalMember(memberEntity).activeMember(memberEntity).build();
        UUID uuid = memberEntity.getUuid();

        given(memberRepository.findByUuid(uuid)).willReturn(Optional.of(memberEntity));
        given(memberRepository.existsByUuid(uuid)).willReturn(true);
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.existsByOriginalMember(memberEntity)).willReturn(false);
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);
        given(memberAuthRepository.findByProviderAndProviderId(memberAuthEntity.getProvider(), memberAuthEntity.getProviderId())).willReturn(Optional.of(memberAuthEntity));

        // when
        memberService.insert(memberBasicUserInsertRequest);
        SiteMemberAuthResponse memberAuthResponse = memberAuthService.insert(memberAuthBasicUserInsertRequest);

        // then
        assertThat(memberAuthService.getByProviderAndProviderId(memberAuthEntity.getProvider(), memberAuthEntity.getProviderId()).orElseThrow()).isEqualTo(memberAuthResponse);
    }

    @DisplayName("빈 회원 인증 얻기")
    @Test
    void getOptionalEmptyTest() {
        // given
        SiteMemberAuth memberAuth = memberAuthBasicUserWithUuid;
        UUID uuid = memberAuth.getOriginalMemberUuid();
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
        given(memberRepository.findByUuid(uuid)).willReturn(Optional.of(memberEntity));
        given(memberAuthRepository.findByOriginalMember(memberEntity)).willReturn(Optional.empty());

        // then
        assertThat(memberAuthService.getByOriginalMember(memberEntity)).isEmpty();

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
        SiteMemberAuthEntity memberAuthEntity = this.createMemberAuthBasicUserEntityBuilder().originalMember(memberEntity).activeMember(memberEntity).build();
        SiteMemberAuthEntity updatedMemberAuthEntity = SiteMemberAuthEntity.builder().memberAuthEntity(memberAuthEntity).email(updatedEmail).build();
        UUID uuid = memberEntity.getUuid();

        given(memberRepository.findByUuid(uuid)).willReturn(Optional.of(memberEntity));
        given(memberRepository.existsByUuid(uuid)).willReturn(true);
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.existsByOriginalMember(memberEntity)).willReturn(false);
        given(memberAuthRepository.findByOriginalMember(memberEntity)).willReturn(Optional.of(updatedMemberAuthEntity));
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity).willReturn(updatedMemberAuthEntity);
        given(memberAuthRepository.findByEmail(updatedEmail)).willReturn(List.of(updatedMemberAuthEntity));

        // when
        memberService.insert(memberBasicUserInsertRequest);
        memberAuthService.insert(memberAuthBasicUserInsertRequest);
        SiteMemberAuthResponse updatedMemberAuthResponse = memberAuthService.update(new SiteMemberAuthUpdateRequest(memberAuthEntity.getOriginalMember().getUuid(), updatedEmail, memberAuthEntity.getPw()));

        // then
        assertThat(memberAuthService.getByEmail(updatedEmail).getFirst()).isEqualTo(updatedMemberAuthResponse);
    }

    @DisplayName("uuid로 회원 인증 제거")
    @Test
    void removeByUuidTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMemberAuthEntity memberAuthEntity = this.createMemberAuthBasicUserEntityBuilder().originalMember(memberEntity).activeMember(memberEntity).build();
        UUID uuid = memberEntity.getUuid();

        given(memberRepository.findByUuid(uuid)).willReturn(Optional.of(memberEntity));
        given(memberRepository.existsByUuid(uuid)).willReturn(true);
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.existsByOriginalMember(memberEntity)).willReturn(false).willReturn(true);
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);
        given(memberAuthRepository.findByUuid(uuid)).willReturn(Optional.empty());
        willDoNothing().given(memberAuthRepository).deleteByUuid(memberAuthEntity.getUuid());

        // when
        memberService.insert(memberBasicUserInsertRequest);
        memberAuthService.insert(memberAuthBasicUserInsertRequest);
        memberAuthService.removeByUuid(uuid);

        // then
        assertThat(memberAuthService.getByUuid(uuid)).isEmpty();
    }
}