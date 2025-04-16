package kr.modusplant.api.crud.member.persistence.repository;

import kr.modusplant.api.crud.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.api.crud.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryOnlyContext
class SiteMemberCrudJpaRepositoryTest implements SiteMemberEntityTestUtils {

    private final SiteMemberCrudJpaRepository memberRepository;

    @Autowired
    SiteMemberCrudJpaRepositoryTest(SiteMemberCrudJpaRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @DisplayName("uuid로 회원 찾기")
    @Test
    void findByUuidTest() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();

        // when
        memberRepository.save(member);

        // then
        assertThat(memberRepository.findByUuid(member.getUuid()).orElseThrow()).isEqualTo(member);
    }

    @DisplayName("nickname으로 회원 찾기")
    @Test
    void findByNameTest() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();

        // when
        memberRepository.save(member);

        // then
        assertThat(memberRepository.findByNickname(member.getNickname()).getFirst()).isEqualTo(member);
    }

    @DisplayName("birthDate으로 회원 찾기")
    @Test
    void findByVersionTest() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();

        // when
        memberRepository.save(member);

        // then
        assertThat(memberRepository.findByBirthDate(member.getBirthDate()).getFirst()).isEqualTo(member);
    }

    @DisplayName("isActive로 회원 찾기")
    @Test
    void findByIsActiveTest() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();

        // when
        memberRepository.save(member);

        // then
        assertThat(memberRepository.findByIsActive(member.getIsActive()).getFirst()).isEqualTo(member);
    }

    @DisplayName("isDisabledByLinking으로 회원 찾기")
    @Test
    void findByIsDisabledByLinkingTest() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();

        // when
        memberRepository.save(member);

        // then
        assertThat(memberRepository.findByIsDisabledByLinking(member.getIsDisabledByLinking()).getFirst()).isEqualTo(member);
    }

    @DisplayName("isBanned으로 회원 찾기")
    @Test
    void findByIsBannedTest() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();

        // when
        memberRepository.save(member);

        // then
        assertThat(memberRepository.findByIsBanned(member.getIsBanned()).getFirst()).isEqualTo(member);
    }

    @DisplayName("isDeleted으로 회원 찾기")
    @Test
    void findByIsDeletedTest() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();

        // when
        memberRepository.save(member);

        // then
        assertThat(memberRepository.findByIsDeleted(member.getIsDeleted()).getFirst()).isEqualTo(member);
    }

    @DisplayName("loggedInAt으로 회원 찾기")
    @Test
    void findByLoggedInAtTest() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();

        // when
        memberRepository.save(member);

        // then
        assertThat(memberRepository.findByLoggedInAt(member.getLoggedInAt()).getFirst()).isEqualTo(member);
    }

    @DisplayName("createdAt으로 회원 찾기")
    @Test
    void findByCreatedAtTest() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();

        // when
        memberRepository.save(member);

        // then
        assertThat(memberRepository.findByCreatedAt(member.getCreatedAt()).getFirst()).isEqualTo(member);
    }

    @DisplayName("lastModifiedAt으로 회원 찾기")
    @Test
    void findByLastModifiedAtTest() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();

        // when
        memberRepository.save(member);

        // then
        assertThat(memberRepository.findByLastModifiedAt(member.getLastModifiedAt()).getFirst()).isEqualTo(member);
    }

    @DisplayName("uuid로 회원 삭제")
    @Test
    void deleteByUuidTest() {
        // given
        SiteMemberEntity member = memberRepository.save(createMemberBasicUserEntity());
        UUID uuid = member.getUuid();

        // when
        memberRepository.deleteByUuid(uuid);

        // then
        assertThat(memberRepository.findByUuid(uuid)).isEmpty();
    }

    @DisplayName("uuid로 회원 확인")
    @Test
    void existsByUuidTest() {
        // given
        SiteMemberEntity member = createMemberBasicUserEntity();

        // when
        memberRepository.save(member);

        // then
        assertThat(memberRepository.existsByUuid(member.getUuid())).isEqualTo(true);
    }
}