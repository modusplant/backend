package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.SiteMemberAuthEntity;
import kr.modusplant.framework.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.jpa.entity.common.util.SiteMemberAuthEntityTestUtils;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RepositoryOnlyContext
class SiteMemberAuthJpaRepositoryTest implements SiteMemberAuthEntityTestUtils {

    private final SiteMemberAuthJpaRepository memberAuthRepository;
    private final SiteMemberJpaRepository memberRepository;

    @Autowired
    SiteMemberAuthJpaRepositoryTest(SiteMemberAuthJpaRepository memberAuthRepository, SiteMemberJpaRepository memberRepository) {
        this.memberAuthRepository = memberAuthRepository;
        this.memberRepository = memberRepository;
    }

    @DisplayName("uuid로 회원 인증 찾기")
    @Test
    void findByUuidTest() {
        // given
        SiteMemberEntity member = memberRepository.save(createMemberBasicUserEntity());

        // when
        SiteMemberAuthEntity memberAuth = memberAuthRepository.save(createMemberAuthBasicUserEntityBuilder().originalMember(member).activeMember(member).build());

        // then
        assertThat(memberAuthRepository.findByUuid(memberAuth.getUuid()).orElseThrow()).isEqualTo(memberAuth);
    }

    @DisplayName("activeMember로 회원 인증 찾기")
    @Test
    void findByActiveMemberTest() {
        // given
        SiteMemberEntity member = memberRepository.save(createMemberBasicUserEntity());

        // when
        SiteMemberAuthEntity memberAuth = memberAuthRepository.save(createMemberAuthBasicUserEntityBuilder().originalMember(member).activeMember(member).build());

        // then
        assertThat(memberAuthRepository.findByActiveMember(memberAuth.getActiveMember()).getFirst()).isEqualTo(memberAuth);
    }

    @DisplayName("originalMember로 회원 인증 찾기")
    @Test
    void findByOriginalMemberTest() {
        // given
        SiteMemberEntity member = memberRepository.save(createMemberBasicUserEntity());

        // when
        SiteMemberAuthEntity memberAuth = memberAuthRepository.save(createMemberAuthBasicUserEntityBuilder().originalMember(member).activeMember(member).build());

        // then
        assertThat(memberAuthRepository.findByUuid(memberAuth.getUuid()).orElseThrow()).isEqualTo(memberAuth);
    }

    @DisplayName("email로 회원 인증 찾기")
    @Test
    void findByEmailTest() {
        // given
        SiteMemberEntity member = memberRepository.save(createMemberBasicUserEntity());

        // when
        SiteMemberAuthEntity memberAuth = memberAuthRepository.save(createMemberAuthBasicUserEntityBuilder().originalMember(member).activeMember(member).build());

        // then
        assertThat(memberAuthRepository.findByEmail(memberAuth.getEmail()).getFirst()).isEqualTo(memberAuth);
    }

    @DisplayName("provider로 회원 인증 찾기")
    @Test
    void findByProviderTest() {
        // given
        SiteMemberEntity member = memberRepository.save(createMemberBasicUserEntity());

        // when
        SiteMemberAuthEntity memberAuth = memberAuthRepository.save(createMemberAuthBasicUserEntityBuilder().originalMember(member).activeMember(member).build());

        // then
        assertTrue(memberAuthRepository.findByProvider(memberAuth.getProvider()).stream().anyMatch(element -> element.getOriginalMember().getUuid().equals(member.getUuid())));
    }

    @DisplayName("providerId로 회원 인증 찾기")
    @Test
    void findByProviderIdTest() {
        // given
        SiteMemberEntity member = memberRepository.save(createMemberBasicUserEntity());

        // when
        SiteMemberAuthEntity memberAuth = memberAuthRepository.save(createMemberAuthBasicUserEntityBuilder().originalMember(member).activeMember(member).build());

        // then
        assertThat(memberAuthRepository.findByProviderId(memberAuth.getProviderId()).stream().map(element -> element.getUuid().equals(member.getUuid())).findFirst()).isPresent();
    }

    @DisplayName("email과 provider로 회원 인증 찾기")
    @Test
    void findByEmailAndProviderTest() {
        // given
        SiteMemberEntity member = memberRepository.save(createMemberBasicUserEntity());

        // when
        SiteMemberAuthEntity memberAuth = memberAuthRepository.save(createMemberAuthBasicUserEntityBuilder().originalMember(member).activeMember(member).build());

        // then
        assertThat(memberAuthRepository.findByEmailAndProvider(memberAuth.getEmail(), memberAuth.getProvider()).orElseThrow()).isEqualTo(memberAuth);
    }

    @DisplayName("provider와 providerId로 회원 인증 찾기")
    @Test
    void findByProviderAndProviderIdTest() {
        // given
        SiteMemberEntity member = memberRepository.save(createMemberGoogleUserEntity());

        // when
        SiteMemberAuthEntity memberAuth = memberAuthRepository.save(createMemberAuthGoogleUserEntityBuilder().originalMember(member).activeMember(member).build());

        // then
        assertThat(memberAuthRepository.findByProviderAndProviderId(memberAuth.getProvider(), memberAuth.getProviderId()).orElseThrow()).isEqualTo(memberAuth);
    }

    @DisplayName("lastModifiedAt으로 회원 인증 찾기")
    @Test
    void findByLastModifiedAtTest() {
        // given
        SiteMemberEntity member = memberRepository.save(createMemberBasicUserEntity());

        // when
        SiteMemberAuthEntity memberAuth = memberAuthRepository.save(createMemberAuthBasicUserEntityBuilder().originalMember(member).activeMember(member).build());

        // then
        assertThat(memberAuthRepository.findByLastModifiedAt(memberAuth.getLastModifiedAt()).getFirst()).isEqualTo(memberAuth);
    }

    @DisplayName("uuid로 회원 인증 삭제")
    @Test
    void deleteByUuidTest() {
        // given
        SiteMemberEntity member = memberRepository.save(createMemberBasicUserEntity());
        SiteMemberAuthEntity memberAuth = memberAuthRepository.save(createMemberAuthBasicUserEntityBuilder().originalMember(member).activeMember(member).build());
        UUID uuid = memberAuth.getUuid();

        // when
        memberAuthRepository.deleteByUuid(uuid);

        // then
        assertThat(memberAuthRepository.findByUuid(uuid)).isEmpty();
    }

    @DisplayName("uuid로 회원 인증 확인")
    @Test
    void existsByUuidTest() {
        // given
        SiteMemberEntity member = memberRepository.save(createMemberBasicUserEntity());

        // when
        SiteMemberAuthEntity memberAuth = memberAuthRepository.save(createMemberAuthBasicUserEntityBuilder().originalMember(member).activeMember(member).build());

        // then
        assertThat(memberAuthRepository.existsByUuid(memberAuth.getUuid())).isEqualTo(true);
    }
}