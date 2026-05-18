package kr.modusplant.domains.account.identity.framework.out.jpa.repository;

import kr.modusplant.domains.account.identity.framework.out.jpa.entity.MemberAuthEntity;
import kr.modusplant.domains.account.identity.framework.out.jpa.entity.common.util.MemberAuthEntityTestUtils;
import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.out.jpa.repository.MemberJpaRepository;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RepositoryOnlyContext
class MemberAuthJpaRepositoryTest implements MemberAuthEntityTestUtils {

    private final MemberAuthJpaRepository memberAuthRepository;
    private final MemberJpaRepository memberRepository;

    @Autowired
    MemberAuthJpaRepositoryTest(MemberAuthJpaRepository memberAuthRepository, MemberJpaRepository memberRepository) {
        this.memberAuthRepository = memberAuthRepository;
        this.memberRepository = memberRepository;
    }

    @DisplayName("uuid로 회원 인증 찾기")
    @Test
    void findByUuidTest() {
        // given
        MemberEntity member = memberRepository.save(createMemberBasicUserEntity());

        // when
        MemberAuthEntity memberAuth = memberAuthRepository.save(createMemberAuthBasicUserEntityBuilder().member(member).build());

        // then
        assertThat(memberAuthRepository.findByUuid(memberAuth.getUuid()).orElseThrow()).isEqualTo(memberAuth);
    }

    @DisplayName("member로 회원 인증 찾기")
    @Test
    void findByMemberTest() {
        // given
        MemberEntity member = memberRepository.save(createMemberBasicUserEntity());

        // when
        MemberAuthEntity memberAuth = memberAuthRepository.save(createMemberAuthBasicUserEntityBuilder().member(member).build());

        // then
        assertThat(memberAuthRepository.findByUuid(memberAuth.getUuid()).orElseThrow()).isEqualTo(memberAuth);
    }

    @DisplayName("email로 회원 인증 찾기")
    @Test
    void findByEmailTest() {
        // given
        MemberEntity member = memberRepository.save(createMemberBasicUserEntity());

        // when
        MemberAuthEntity memberAuth = memberAuthRepository.save(createMemberAuthBasicUserEntityBuilder().member(member).build());

        // then
        assertThat(memberAuthRepository.findByEmail(memberAuth.getEmail()).get()).isEqualTo(memberAuth);
    }

    @DisplayName("provider로 회원 인증 찾기")
    @Test
    void findByProviderTest() {
        // given
        MemberEntity member = memberRepository.save(createMemberBasicUserEntity());

        // when
        MemberAuthEntity memberAuth = memberAuthRepository.save(createMemberAuthBasicUserEntityBuilder().member(member).build());

        // then
        assertTrue(memberAuthRepository.findByProvider(memberAuth.getProvider()).stream().anyMatch(element -> element.getMember().getUuid().equals(member.getUuid())));
    }

    @DisplayName("providerId로 회원 인증 찾기")
    @Test
    void findByProviderIdTest() {
        // given
        MemberEntity member = memberRepository.save(createMemberGoogleUserEntity());

        // when
        MemberAuthEntity memberAuth = memberAuthRepository.save(createMemberAuthGoogleUserEntityBuilder().member(member).build());

        // then
        assertThat(memberAuthRepository.findByProviderId(memberAuth.getProviderId()).get()).isEqualTo(memberAuth);
    }

    @DisplayName("email과 provider로 회원 인증 찾기")
    @Test
    void findByEmailAndProviderTest() {
        // given
        MemberEntity member = memberRepository.save(createMemberBasicUserEntity());

        // when
        MemberAuthEntity memberAuth = memberAuthRepository.save(createMemberAuthBasicUserEntityBuilder().member(member).build());

        // then
        assertThat(memberAuthRepository.findByEmailAndProvider(memberAuth.getEmail(), memberAuth.getProvider()).orElseThrow()).isEqualTo(memberAuth);
    }

    @DisplayName("provider와 providerId로 회원 인증 찾기")
    @Test
    void findByProviderAndProviderIdTest() {
        // given
        MemberEntity member = memberRepository.save(createMemberGoogleUserEntity());

        // when
        MemberAuthEntity memberAuth = memberAuthRepository.save(createMemberAuthGoogleUserEntityBuilder().member(member).build());

        // then
        assertThat(memberAuthRepository.findByProviderAndProviderId(memberAuth.getProvider(), memberAuth.getProviderId()).orElseThrow()).isEqualTo(memberAuth);
    }

    @DisplayName("lastModifiedAt으로 회원 인증 찾기")
    @Test
    void findByLastModifiedAtTest() {
        // given
        MemberEntity member = memberRepository.save(createMemberBasicUserEntity());

        // when
        MemberAuthEntity memberAuth = memberAuthRepository.save(createMemberAuthBasicUserEntityBuilder().member(member).build());

        // then
        assertThat(memberAuthRepository.findByLastModifiedAt(memberAuth.getLastModifiedAt()).getFirst()).isEqualTo(memberAuth);
    }

    @DisplayName("uuid로 회원 인증 삭제")
    @Test
    void deleteByUuidTest() {
        // given
        MemberEntity member = memberRepository.save(createMemberBasicUserEntity());
        MemberAuthEntity memberAuth = memberAuthRepository.save(createMemberAuthBasicUserEntityBuilder().member(member).build());
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
        MemberEntity member = memberRepository.save(createMemberBasicUserEntity());

        // when
        MemberAuthEntity memberAuth = memberAuthRepository.save(createMemberAuthBasicUserEntityBuilder().member(member).build());

        // then
        assertThat(memberAuthRepository.existsByUuid(memberAuth.getUuid())).isEqualTo(true);
    }

    @DisplayName("회원 인증 엔터티 toString 호출 시 순환 오류 발생 여부 확인")
    @Test
    void testToString_givenSiteMemberAuthEntity_willReturnRepresentative() {
        // given
        MemberEntity member = memberRepository.save(createMemberBasicUserEntity());

        // when
        MemberAuthEntity memberAuth = memberAuthRepository.save(createMemberAuthBasicUserEntityBuilder().member(member).build());

        // then
        assertDoesNotThrow(memberAuth::toString);
    }
}