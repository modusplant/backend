package kr.modusplant.framework.out.jpa.repository;

import kr.modusplant.framework.out.jpa.entity.SiteMemberTermEntity;
import kr.modusplant.framework.out.jpa.entity.common.util.SiteMemberTermEntityTestUtils;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class SiteMemberTermJpaRepositoryTest implements SiteMemberTermEntityTestUtils {

    private final SiteMemberTermJpaRepository memberTermRepository;

    @Autowired
    SiteMemberTermJpaRepositoryTest(SiteMemberTermJpaRepository memberTermRepository) {
        this.memberTermRepository = memberTermRepository;
    }

    @DisplayName("uuid로 회원 약관 찾기")
    @Test
    void findByUuidTest() {
        // given
        SiteMemberTermEntity memberTerm = createMemberTermUserEntity();

        // when
        memberTerm = memberTermRepository.save(memberTerm);

        // then
        assertThat(memberTermRepository.findByUuid(memberTerm.getUuid()).orElseThrow()).isEqualTo(memberTerm);
    }

    @DisplayName("member로 회원 역할 찾기")
    @Test
    void findByMemberTest() {
        // given
        SiteMemberTermEntity memberRole = createMemberTermUserEntity();

        // when
        memberTermRepository.save(memberRole);

        // then
        assertThat(memberTermRepository.findByMember(memberRole.getMember()).orElseThrow()).isEqualTo(memberRole);
    }

    @DisplayName("agreedTermsOfUseVersion으로 회원 약관 찾기")
    @Test
    void findByAgreedTermsOfUseVersionTest() {
        // given
        SiteMemberTermEntity memberTerm = createMemberTermUserEntity();

        // when
        memberTerm = memberTermRepository.save(memberTerm);

        // then
        assertThat(memberTermRepository.findByAgreedTermsOfUseVersion(memberTerm.getAgreedTermsOfUseVersion()).getFirst()).isEqualTo(memberTerm);
    }

    @DisplayName("agreedPrivacyPolicyVersion으로 회원 약관 찾기")
    @Test
    void findByAgreedPrivacyPolicyVersionTest() {
        // given
        SiteMemberTermEntity memberTerm = createMemberTermUserEntity();

        // when
        memberTerm = memberTermRepository.save(memberTerm);

        // then
        assertThat(memberTermRepository.findByAgreedPrivacyPolicyVersion(memberTerm.getAgreedPrivacyPolicyVersion()).getFirst()).isEqualTo(memberTerm);
    }

    @DisplayName("agreedAdInfoReceivingVersion으로 회원 약관 찾기")
    @Test
    void findByAgreedAdInfoReceivingVersionTest() {
        // given
        SiteMemberTermEntity memberTerm = createMemberTermUserEntity();

        // when
        memberTerm = memberTermRepository.save(memberTerm);

        // then
        assertThat(memberTermRepository.findByAgreedAdInfoReceivingVersion(memberTerm.getAgreedAdInfoReceivingVersion()).getFirst()).isEqualTo(memberTerm);
    }

    @DisplayName("lastModifiedAt으로 회원 약관 찾기")
    @Test
    void findByLastModifiedAtTest() {
        // given
        SiteMemberTermEntity memberTerm = createMemberTermUserEntity();

        // when
        memberTerm = memberTermRepository.save(memberTerm);

        // then
        assertThat(memberTermRepository.findByLastModifiedAt(memberTerm.getLastModifiedAt()).getFirst()).isEqualTo(memberTerm);
    }

    @DisplayName("uuid로 회원 약관 삭제")
    @Test
    void deleteByUuidTest() {
        // given
        SiteMemberTermEntity memberTerm = memberTermRepository.save(createMemberTermUserEntity());
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
        SiteMemberTermEntity memberTerm = createMemberTermUserEntity();

        // when
        memberTerm = memberTermRepository.save(memberTerm);

        // then
        assertThat(memberTermRepository.existsByUuid(memberTerm.getUuid())).isEqualTo(true);
    }
}