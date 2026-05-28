package kr.modusplant.domains.term.framework.outbound.jpa.repository;

import kr.modusplant.domains.term.framework.outbound.jpa.entity.MemberTermEntity;
import kr.modusplant.domains.term.framework.outbound.jpa.entity.common.util.MemberTermEntityTestUtils;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@RepositoryOnlyContext
class MemberTermJpaRepositoryTest implements MemberTermEntityTestUtils {

    private final MemberTermJpaRepository memberTermRepository;

    @Autowired
    MemberTermJpaRepositoryTest(MemberTermJpaRepository memberTermRepository) {
        this.memberTermRepository = memberTermRepository;
    }

    @DisplayName("uuid로 회원 약관 찾기")
    @Test
    void findByUuidTest() {
        // given
        MemberTermEntity memberTerm = createMemberTermUserEntity();

        // when
        memberTerm = memberTermRepository.save(memberTerm);

        // then
        assertThat(memberTermRepository.findByUuid(memberTerm.getUuid()).orElseThrow()).isEqualTo(memberTerm);
    }

    @DisplayName("member로 회원 역할 찾기")
    @Test
    void findByMemberTest() {
        // given
        MemberTermEntity memberTerm = createMemberTermUserEntity();

        // when
        memberTermRepository.save(memberTerm);

        // then
        assertThat(memberTermRepository.findByMember(memberTerm.getMember()).orElseThrow()).isEqualTo(memberTerm);
    }

    @DisplayName("agreedTermsOfUseVersion으로 회원 약관 찾기")
    @Test
    void findByAgreedTermsOfUseVersionTest() {
        // given
        MemberTermEntity memberTerm = createMemberTermUserEntity();

        // when
        memberTerm = memberTermRepository.save(memberTerm);

        // then
        assertThat(memberTermRepository.findByAgreedTermsOfUseVersion(memberTerm.getAgreedTermsOfUseVersion()).getFirst()).isEqualTo(memberTerm);
    }

    @DisplayName("agreedPrivacyPolicyVersion으로 회원 약관 찾기")
    @Test
    void findByAgreedPrivacyPolicyVersionTest() {
        // given
        MemberTermEntity memberTerm = createMemberTermUserEntity();

        // when
        memberTerm = memberTermRepository.save(memberTerm);

        // then
        assertThat(memberTermRepository.findByAgreedPrivacyPolicyVersion(memberTerm.getAgreedPrivacyPolicyVersion()).getFirst()).isEqualTo(memberTerm);
    }

    @DisplayName("agreedCommunityPolicyVersion으로 회원 약관 찾기")
    @Test
    void findByAgreedCommunityPolicyVersionTest() {
        // given
        MemberTermEntity memberTerm = createMemberTermUserEntity();

        // when
        memberTerm = memberTermRepository.save(memberTerm);

        // then
        assertThat(memberTermRepository.findByAgreedCommunityPolicyVersion(memberTerm.getAgreedCommunityPolicyVersion()).getFirst()).isEqualTo(memberTerm);
    }

    @DisplayName("lastModifiedAt으로 회원 약관 찾기")
    @Test
    void findByLastModifiedAtTest() {
        // given
        MemberTermEntity memberTerm = createMemberTermUserEntity();

        // when
        memberTerm = memberTermRepository.save(memberTerm);

        // then
        assertThat(memberTermRepository.findByLastModifiedAt(memberTerm.getLastModifiedAt()).getFirst()).isEqualTo(memberTerm);
    }

    @DisplayName("uuid로 회원 약관 삭제")
    @Test
    void deleteByUuidTest() {
        // given
        MemberTermEntity memberTerm = memberTermRepository.save(createMemberTermUserEntity());
        UUID uuid = memberTerm.getUuid();

        // when
        memberTermRepository.deleteByUuid(uuid);

        // then
        assertThat(memberTermRepository.findByUuid(uuid)).isEmpty();
    }

    @DisplayName("uuid로 회원 약관 확인")
    @Test
    void existsByUuidTest() {
        // given
        MemberTermEntity memberTerm = createMemberTermUserEntity();

        // when
        memberTerm = memberTermRepository.save(memberTerm);

        // then
        assertThat(memberTermRepository.existsByUuid(memberTerm.getUuid())).isEqualTo(true);
    }

    @DisplayName("회원 약관 엔터티 toString 호출 시 순환 오류 발생 여부 확인")
    @Test
    void testToString_givenSiteMemberTermEntity_willReturnRepresentative() {
        // given
        MemberTermEntity memberTerm = createMemberTermUserEntity();

        // when
        memberTerm = memberTermRepository.save(memberTerm);

        // then
        assertDoesNotThrow(memberTerm::toString);
    }
}