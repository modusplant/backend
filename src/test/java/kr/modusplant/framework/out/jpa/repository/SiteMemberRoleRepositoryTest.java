package kr.modusplant.framework.out.jpa.repository;

import kr.modusplant.framework.out.jpa.entity.SiteMemberRoleEntity;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import kr.modusplant.legacy.domains.member.common.util.entity.SiteMemberRoleEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class SiteMemberRoleRepositoryTest implements SiteMemberRoleEntityTestUtils {

    private final SiteMemberRoleRepository memberRoleRepository;

    @Autowired
    SiteMemberRoleRepositoryTest(SiteMemberRoleRepository memberRoleRepository) {
        this.memberRoleRepository = memberRoleRepository;
    }

    @DisplayName("uuid로 회원 역할 찾기")
    @Test
    void findByUuidTest() {
        // given
        SiteMemberRoleEntity memberRole = createMemberRoleUserEntity();

        // when
        memberRoleRepository.save(memberRole);

        // then
        assertThat(memberRoleRepository.findByUuid(memberRole.getUuid()).orElseThrow()).isEqualTo(memberRole);
    }

    @DisplayName("member로 회원 역할 찾기")
    @Test
    void findByMemberTest() {
        // given
        SiteMemberRoleEntity memberRole = createMemberRoleUserEntity();

        // when
        memberRoleRepository.save(memberRole);

        // then
        assertThat(memberRoleRepository.findByMember(memberRole.getMember()).orElseThrow()).isEqualTo(memberRole);
    }

    @DisplayName("role로 회원 역할 찾기")
    @Test
    void findByRoleTest() {
        // given
        SiteMemberRoleEntity memberRole = createMemberRoleUserEntity();

        // when
        memberRoleRepository.save(memberRole);

        // then
        assertThat(memberRoleRepository.findByRole(memberRole.getRole()).getFirst()).isEqualTo(memberRole);
    }

    @DisplayName("uuid로 회원 역할 삭제")
    @Test
    void deleteByUuidTest() {
        // given
        SiteMemberRoleEntity memberRole = memberRoleRepository.save(createMemberRoleUserEntity());
        UUID uuid = memberRole.getUuid();

        // when
        memberRoleRepository.deleteByUuid(uuid);

        // then
        assertThat(memberRoleRepository.findByUuid(uuid)).isEmpty();
    }

    @DisplayName("uuid로 회원 역할 확인")
    @Test
    void existsByUuidTest() {
        // given
        SiteMemberRoleEntity memberRole = createMemberRoleUserEntity();

        // when
        memberRoleRepository.save(memberRole);

        // then
        assertThat(memberRoleRepository.existsByUuid(memberRole.getUuid())).isEqualTo(true);
    }
}