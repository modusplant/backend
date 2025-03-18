package kr.modusplant.global.persistence.repository;

import kr.modusplant.global.persistence.entity.SiteMemberAuthEntity;
import kr.modusplant.global.persistence.entity.SiteMemberEntity;
import kr.modusplant.support.context.RepositoryOnlyContext;
import kr.modusplant.support.util.entity.SiteMemberAuthEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

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
        SiteMemberAuthEntity memberAuth = createMemberAuthBasicUserEntity();
        SiteMemberEntity member = memberRepository.save(memberAuth.getActiveMember());

        // when
        memberAuth = memberAuthRepository.save(SiteMemberAuthEntity.builder()
                .memberAuthEntity(memberAuth)
                .activeMember(member)
                .originalMember(member)
                .build());

        // then
        assertThat(memberAuthRepository.findByUuid(memberAuth.getUuid()).orElseThrow()).isEqualTo(memberAuth);
    }

    @DisplayName("activeMember로 회원 인증 찾기")
    @Test
    void findByActiveMemberTest() {
        // given
        SiteMemberAuthEntity memberAuth = createMemberAuthBasicUserEntity();
        SiteMemberEntity member = memberRepository.save(memberAuth.getActiveMember());

        // when
        memberAuth = memberAuthRepository.save(SiteMemberAuthEntity.builder()
                .memberAuthEntity(memberAuth)
                .activeMember(member)
                .originalMember(member)
                .build());

        // then
        assertThat(memberAuthRepository.findByActiveMember(memberAuth.getActiveMember()).getFirst()).isEqualTo(memberAuth);
    }

    @DisplayName("originalMember로 회원 인증 찾기")
    @Test
    void findByOriginalMemberTest() {
        // given
        SiteMemberAuthEntity memberAuth = createMemberAuthBasicUserEntity();
        SiteMemberEntity member = memberRepository.save(memberAuth.getActiveMember());

        // when
        memberAuth = memberAuthRepository.save(SiteMemberAuthEntity.builder()
                .memberAuthEntity(memberAuth)
                .activeMember(member)
                .originalMember(member)
                .build());

        // then
        assertThat(memberAuthRepository.findByUuid(memberAuth.getUuid()).orElseThrow()).isEqualTo(memberAuth);
    }

    @DisplayName("email로 회원 인증 찾기")
    @Test
    void findByEmailTest() {
        // given
        SiteMemberAuthEntity memberAuth = createMemberAuthBasicUserEntity();
        SiteMemberEntity member = memberRepository.save(memberAuth.getActiveMember());

        // when
        memberAuth = memberAuthRepository.save(SiteMemberAuthEntity.builder()
                .memberAuthEntity(memberAuth)
                .activeMember(member)
                .originalMember(member)
                .build());

        // then
        assertThat(memberAuthRepository.findByEmail(memberAuth.getEmail()).getFirst()).isEqualTo(memberAuth);
    }

    @DisplayName("provider로 회원 인증 찾기")
    @Test
    void findByProviderTest() {
        // given
        SiteMemberAuthEntity memberAuth = createMemberAuthBasicUserEntity();
        SiteMemberEntity member = memberRepository.save(memberAuth.getActiveMember());

        // when
        memberAuth = memberAuthRepository.save(SiteMemberAuthEntity.builder()
                .memberAuthEntity(memberAuth)
                .activeMember(member)
                .originalMember(member)
                .build());

        // then
        assertThat(memberAuthRepository.findByProvider(memberAuth.getProvider()).getFirst()).isEqualTo(memberAuth);
    }

    @DisplayName("providerId로 회원 인증 찾기")
    @Test
    void findByProviderIdTest() {
        // given
        SiteMemberAuthEntity memberAuth = createMemberAuthBasicUserEntity();
        SiteMemberEntity member = memberRepository.save(memberAuth.getActiveMember());

        // when
        memberAuth = memberAuthRepository.save(SiteMemberAuthEntity.builder()
                .memberAuthEntity(memberAuth)
                .activeMember(member)
                .originalMember(member)
                .build());

        // then
        assertThat(memberAuthRepository.findByProviderId(memberAuth.getProviderId()).getFirst()).isEqualTo(memberAuth);
    }

    @DisplayName("failedAttempt로 회원 인증 찾기")
    @Test
    void findByFailedAttemptTest() {
        // given
        SiteMemberAuthEntity memberAuth = createMemberAuthBasicUserEntity();
        SiteMemberEntity member = memberRepository.save(memberAuth.getActiveMember());

        // when
        memberAuth = memberAuthRepository.save(SiteMemberAuthEntity.builder()
                .memberAuthEntity(memberAuth)
                .activeMember(member)
                .originalMember(member)
                .build());

        // then
        assertThat(memberAuthRepository.findByFailedAttempt(memberAuth.getFailedAttempt()).getFirst()).isEqualTo(memberAuth);
    }

    @DisplayName("lastModifiedAt으로 회원 인증 찾기")
    @Test
    void findByLastModifiedAtTest() {
        // given
        SiteMemberAuthEntity memberAuth = createMemberAuthBasicUserEntity();
        SiteMemberEntity member = memberRepository.save(memberAuth.getActiveMember());

        // when
        memberAuth = memberAuthRepository.save(SiteMemberAuthEntity.builder()
                .memberAuthEntity(memberAuth)
                .activeMember(member)
                .originalMember(member)
                .build());

        // then
        assertThat(memberAuthRepository.findByLastModifiedAt(memberAuth.getLastModifiedAt()).getFirst()).isEqualTo(memberAuth);
    }

    @DisplayName("uuid로 회원 인증 삭제")
    @Test
    void deleteByUuidTest() {
        // given
        SiteMemberAuthEntity memberAuth = createMemberAuthBasicUserEntity();
        SiteMemberEntity member = memberRepository.save(memberAuth.getActiveMember());
        memberAuth = memberAuthRepository.save(SiteMemberAuthEntity.builder()
                .memberAuthEntity(memberAuth)
                .activeMember(member)
                .originalMember(member)
                .build());

        // when
        memberAuthRepository.deleteByUuid(memberAuth.getUuid());

        // then
        assertThat(memberAuthRepository.findAll()).isEmpty();
    }

    @DisplayName("uuid로 회원 인증 확인")
    @Test
    void existsByUuidTest() {
        // given
        SiteMemberAuthEntity memberAuth = createMemberAuthBasicUserEntity();
        SiteMemberEntity member = memberRepository.save(memberAuth.getActiveMember());
        memberAuth = memberAuthRepository.save(SiteMemberAuthEntity.builder()
                .memberAuthEntity(memberAuth)
                .activeMember(member)
                .originalMember(member)
                .build());

        // when
        memberAuthRepository.save(memberAuth);

        // then
        assertThat(memberAuthRepository.existsByUuid(memberAuth.getUuid())).isEqualTo(true);
    }
}