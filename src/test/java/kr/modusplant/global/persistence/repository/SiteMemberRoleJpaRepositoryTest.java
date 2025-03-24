package kr.modusplant.global.persistence.repository;

import kr.modusplant.global.persistence.entity.SiteMemberRoleEntity;
import kr.modusplant.support.context.RepositoryOnlyContext;
import kr.modusplant.support.util.entity.SiteMemberRoleEntityTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class SiteMemberRoleJpaRepositoryTest implements SiteMemberRoleEntityTestUtils {

    private final SiteMemberRoleJpaRepository memberRoleRepository;

    @Autowired
    SiteMemberRoleJpaRepositoryTest(SiteMemberRoleJpaRepository memberRoleRepository) {
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

        // when
        memberRoleRepository.deleteByUuid(memberRole.getUuid());

        // then
        assertThat(memberRoleRepository.findAll()).isEmpty();
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