package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.MemberEntity;
import kr.modusplant.framework.jpa.entity.common.util.MemberEntityTestUtils;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@RepositoryOnlyContext
class MemberJpaRepositoryTest implements MemberEntityTestUtils {

    private final MemberJpaRepository memberRepository;

    @Autowired
    MemberJpaRepositoryTest(MemberJpaRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @DisplayName("uuid로 회원 찾기")
    @Test
    void findByUuidTest() {
        // given
        MemberEntity member = createMemberBasicUserEntity();

        // when
        memberRepository.save(member);

        // then
        assertThat(memberRepository.findByUuid(member.getUuid()).orElseThrow()).isEqualTo(member);
    }

    @DisplayName("nickname으로 회원 찾기")
    @Test
    void findByNameTest() {
        // given
        MemberEntity member = createMemberBasicUserEntity();

        // when
        memberRepository.save(member);

        // then
        assertThat(memberRepository.findByNickname(member.getNickname()).orElseThrow()).isEqualTo(member);
    }

    @DisplayName("isActive로 회원 찾기")
    @Test
    void findByIsActiveTest() {
        // given
        MemberEntity member = createMemberBasicUserEntity();

        // when
        memberRepository.save(member);

        // then
        assertThat(member.getUuid()).isIn(memberRepository.findByIsActive(member.getIsActive()).stream().map(MemberEntity::getUuid).toList());
    }

    @DisplayName("isBanned으로 회원 찾기")
    @Test
    void findByIsBannedTest() {
        // given
        MemberEntity member = createMemberBasicUserEntity();

        // when
        memberRepository.save(member);

        // then
        assertThat(member.getUuid()).isIn(memberRepository.findByIsBanned(member.getIsBanned()).stream().map(MemberEntity::getUuid).toList());
    }

    @DisplayName("loggedInAt으로 회원 찾기")
    @Test
    void findByLoggedInAtTest() {
        // given
        MemberEntity member = createMemberBasicUserEntity();

        // when
        memberRepository.save(member);

        // then
        assertThat(member.getUuid()).isIn(memberRepository.findByLoggedInAt(member.getLoggedInAt()).stream().map(MemberEntity::getUuid).toList());
    }

    @DisplayName("createdAt으로 회원 찾기")
    @Test
    void findByCreatedAtTest() {
        // given
        MemberEntity member = createMemberBasicUserEntity();

        // when
        memberRepository.save(member);

        // then
        assertThat(member.getUuid()).isIn(memberRepository.findByCreatedAt(member.getCreatedAt()).stream().map(MemberEntity::getUuid).toList());
    }

    @DisplayName("lastModifiedAt으로 회원 찾기")
    @Test
    void findByLastModifiedAtTest() {
        // given
        MemberEntity member = createMemberBasicUserEntity();

        // when
        memberRepository.save(member);

        // then
        assertThat(member.getUuid()).isIn(memberRepository.findByLastModifiedAt(member.getLastModifiedAt()).stream().map(MemberEntity::getUuid).toList());
    }

    @DisplayName("uuid로 회원 삭제")
    @Test
    void deleteByUuidTest() {
        // given
        MemberEntity member = memberRepository.save(createMemberBasicUserEntity());
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
        MemberEntity member = createMemberBasicUserEntity();

        // when
        memberRepository.save(member);

        // then
        assertThat(memberRepository.existsByUuid(member.getUuid())).isEqualTo(true);
    }

    @DisplayName("회원 엔터티 toString 호출 시 순환 오류 발생 여부 확인")
    @Test
    void testToString_givenSiteMemberEntity_willReturnRepresentative() {
        // given
        MemberEntity member = createMemberBasicUserEntity();

        // when
        MemberEntity memberEntity = memberRepository.save(member);

        // then
        assertDoesNotThrow(memberEntity::toString);
    }
}