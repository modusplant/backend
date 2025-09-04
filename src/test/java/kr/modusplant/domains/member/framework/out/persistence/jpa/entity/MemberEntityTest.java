package kr.modusplant.domains.member.framework.out.persistence.jpa.entity;

import kr.modusplant.domains.member.common.utils.framework.MemberEntityTestUtils;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@RepositoryOnlyContext
class MemberEntityTest implements MemberEntityTestUtils {

    private final TestEntityManager entityManager;

    @Autowired
    MemberEntityTest(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @DisplayName("null 값으로 PrePersist 호출")
    @Test
    void callPrePersist_withNull_returnsVoid() {
        // given
        MemberEntity member = MemberEntity.builder().memberEntity(createMemberEntity()).isActive(null).isDisabledByLinking(null).isBanned(null).isDeleted(null).build();

        // when
        entityManager.persist(member);
        entityManager.flush();

        // then
        assertThat(member.getIsActive()).isEqualTo(true);
        assertThat(member.getIsDisabledByLinking()).isEqualTo(false);
        assertThat(member.getIsBanned()).isEqualTo(false);
        assertThat(member.getIsDeleted()).isEqualTo(false);
    }

    @DisplayName("null이 아닌 값으로 PrePersist 호출")
    @Test
    void callPrePersist_withNotNull_returnsVoid() {
        // given
        MemberEntity member = MemberEntity.builder().memberEntity(createMemberEntity()).isActive(false).isDisabledByLinking(true).isBanned(false).isDeleted(false).build();

        // when
        entityManager.persist(member);
        entityManager.flush();

        // then
        assertThat(member.getIsActive()).isEqualTo(false);
        assertThat(member.getIsDisabledByLinking()).isEqualTo(true);
        assertThat(member.getIsBanned()).isEqualTo(false);
        assertThat(member.getIsDeleted()).isEqualTo(false);
    }

    @DisplayName("null 값으로 PreUpdate 호출")
    @Test
    void callPreUpdate_withNull_returnsVoid() {
        // given
        MemberEntity member = MemberEntity.builder().memberEntity(createMemberEntity()).build();
        entityManager.persist(member);

        // when
        entityManager.merge(MemberEntity.builder().memberEntity(member).isActive(null).isDisabledByLinking(null).isBanned(null).isDeleted(null).build());
        entityManager.flush();

        // then
        assertThat(member.getIsActive()).isEqualTo(true);
        assertThat(member.getIsDisabledByLinking()).isEqualTo(false);
        assertThat(member.getIsBanned()).isEqualTo(false);
        assertThat(member.getIsDeleted()).isEqualTo(false);
    }

    @DisplayName("null이 아닌 값으로 PreUpdate 호출")
    @Test
    void callPreUpdate_withNotNull_returnsVoid() {
        // given
        MemberEntity member = MemberEntity.builder().memberEntity(createMemberEntity()).isActive(null).isDisabledByLinking(null).isBanned(null).isDeleted(null).build();
        entityManager.persist(member);

        // when
        entityManager.merge(MemberEntity.builder().memberEntity(createMemberEntity()).build());
        entityManager.flush();

        // then
        assertThat(member.getIsActive()).isEqualTo(true);
        assertThat(member.getIsDisabledByLinking()).isEqualTo(false);
        assertThat(member.getIsBanned()).isEqualTo(false);
        assertThat(member.getIsDeleted()).isEqualTo(false);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void useEqual_withSameObject_returnsTrue() {
        // given
        MemberEntity memberEntity = createMemberEntityWithUuid();

        // when & then
        //noinspection EqualsWithItself
        assertEquals(memberEntity, memberEntity);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void useEqual_withObjectOfDifferentClass_returnsFalse() {
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(createMemberEntityWithUuid(), testMemberId);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void useEqual_withObjectContainingDifferentProperty_returnsFalse() {
        assertNotEquals(createMemberEntityWithUuid(), MemberEntity.builder().memberEntity(createMemberEntity()).uuid(UUID.randomUUID()).build());
    }
}