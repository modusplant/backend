package kr.modusplant.global.persistence.service;

import kr.modusplant.global.domain.model.SiteMember;
import kr.modusplant.global.domain.model.SiteMemberAuth;
import kr.modusplant.global.domain.service.crud.SiteMemberAuthService;
import kr.modusplant.global.domain.service.crud.SiteMemberService;
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

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

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

    @DisplayName("uuid로 회원 인증 찾기")
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
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);

        // when
        memberService.insert(member);
        memberAuth = memberAuthService.insert(memberAuth);

        // then
        assertThat(memberAuthService.getByUuid(memberAuth.getUuid()).orElseThrow()).isEqualTo(memberAuth);
    }

    @DisplayName("activeMemberUuid로 회원 인증 찾기")
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
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);
        given(memberAuthRepository.findByActiveMember(memberEntity)).willReturn(List.of(memberAuthEntity));

        // when
        memberService.insert(member);
        memberAuth = memberAuthService.insert(memberAuth);

        // then
        assertThat(memberAuthService.getByActiveMember(member).getFirst()).isEqualTo(memberAuth);
    }

    @DisplayName("originalMemberUuid로 회원 인증 찾기")
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
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);
        given(memberAuthRepository.findByOriginalMember(memberEntity)).willReturn(Optional.of(memberAuthEntity));

        // when
        memberService.insert(member);
        memberAuth = memberAuthService.insert(memberAuth);

        // then
        assertThat(memberAuthService.getByOriginalMember(member).orElseThrow()).isEqualTo(memberAuth);
    }

    @DisplayName("email로 회원 인증 찾기")
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
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);
        given(memberAuthRepository.findByEmail(memberAuthEntity.getEmail())).willReturn(List.of(memberAuthEntity));

        // when
        memberService.insert(member);
        memberAuth = memberAuthService.insert(memberAuth);

        // then
        assertThat(memberAuthService.getByEmail(memberAuth.getEmail()).getFirst()).isEqualTo(memberAuth);
    }

    @DisplayName("provider로 회원 인증 찾기")
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
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);
        given(memberAuthRepository.findByProvider(memberAuthEntity.getProvider())).willReturn(List.of(memberAuthEntity));

        // when
        memberService.insert(member);
        memberAuth = memberAuthService.insert(memberAuth);

        // then
        assertThat(memberAuthService.getByProvider(memberAuth.getProvider()).getFirst()).isEqualTo(memberAuth);
    }

    @DisplayName("providerId로 회원 인증 찾기")
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
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);
        given(memberAuthRepository.findByProviderId(memberAuthEntity.getProviderId())).willReturn(List.of(memberAuthEntity));

        // when
        memberService.insert(member);
        memberAuth = memberAuthService.insert(memberAuth);

        // then
        assertThat(memberAuthService.getByProviderId(memberAuth.getProviderId()).getFirst()).isEqualTo(memberAuth);
    }

    @DisplayName("provider와 providerId로 회원 인증 찾기")
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
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);
        given(memberAuthRepository.findByProviderAndProviderId(memberAuthEntity.getProvider(), memberAuthEntity.getProviderId())).willReturn(List.of(memberAuthEntity));

        // when
        memberService.insert(member);
        memberAuth = memberAuthService.insert(memberAuth);

        // then
        assertThat(memberAuthService.getByProviderAndProviderId(memberAuth.getProvider(), memberAuth.getProviderId()).getFirst()).isEqualTo(memberAuth);
    }

    @DisplayName("failedAttempt로 회원 인증 찾기")
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
        given(memberAuthRepository.save(memberAuthEntity)).willReturn(memberAuthEntity);
        given(memberAuthRepository.findByFailedAttempt(memberAuthEntity.getFailedAttempt())).willReturn(List.of(memberAuthEntity));

        // when
        memberService.insert(member);
        memberAuth = memberAuthService.insert(memberAuth);

        // then
        assertThat(memberAuthService.getByFailedAttempt(memberAuth.getFailedAttempt()).getFirst()).isEqualTo(memberAuth);
    }

    @DisplayName("uuid로 회원 인증 삭제")
    @Test
    void deleteByUuidTest() {
        // given
        SiteMemberEntity memberEntity = createMemberBasicUserEntityWithUuid();
        SiteMember member = memberMapper.toSiteMember(memberEntity);
        SiteMemberAuthEntity memberAuthEntity = createMemberAuthBasicUserEntityWithUuidBuilder().activeMember(memberEntity).originalMember(memberEntity).build();
        SiteMemberAuth memberAuth = memberAuthMapper.toSiteMemberAuth(memberAuthEntity);

        given(memberRepository.findByUuid(memberEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberEntity));
        given(memberRepository.save(memberEntity)).willReturn(memberEntity);
        given(memberAuthRepository.findByUuid(memberAuthEntity.getUuid())).willReturn(Optional.empty()).willReturn(Optional.of(memberAuthEntity));
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
}