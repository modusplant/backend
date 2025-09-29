package kr.modusplant.domains.comment.framework.out.persistence.jpa.entity;

import kr.modusplant.domains.comment.framework.out.persistence.jpa.compositekey.CommentCompositeKey;
import kr.modusplant.domains.comment.support.utils.framework.CommentEntityTestUtils;
import kr.modusplant.framework.out.jpa.entity.CommPostEntity;
import kr.modusplant.framework.out.jpa.entity.CommPrimaryCategoryEntity;
import kr.modusplant.framework.out.jpa.entity.CommSecondaryCategoryEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import kr.modusplant.legacy.domains.communication.common.util.domain.CommPostTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.entity.CommPrimaryCategoryEntityTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.entity.CommSecondaryCategoryEntityTestUtils;
import kr.modusplant.legacy.domains.member.common.util.entity.SiteMemberEntityConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@RepositoryOnlyContext
public class CommentEntityTest implements CommentEntityTestUtils, CommPostTestUtils,
        CommPrimaryCategoryEntityTestUtils, CommSecondaryCategoryEntityTestUtils,
        SiteMemberEntityConstant {

    private final TestEntityManager entityManager;
    private SiteMemberEntity siteMember;
    private CommPostEntity post;
    private CommentEntity comment;

    @Autowired
    CommentEntityTest(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @BeforeEach
    public void setUp() {
        CommPrimaryCategoryEntity primaryCategory = createTestCommPrimaryCategoryEntity();
        CommSecondaryCategoryEntity secondaryCategory = createTestCommSecondaryCategoryEntity();
        entityManager.persist(primaryCategory);
        entityManager.persist(secondaryCategory);

        siteMember = createMemberBasicUserEntity();
        entityManager.persist(siteMember);

        post = createCommPostEntityBuilder()
                .primaryCategory(primaryCategory)
                .secondaryCategory(secondaryCategory)
                .authMember(siteMember)
                .createMember(siteMember)
                .build();
        entityManager.persist(post);

        comment = CommentEntity.builder()
                .CommentEntity(createCommentEntityBuilder()
                        .postEntity(post)
                        .authMember(siteMember)
                        .createMember(siteMember).build())
                .isDeleted(null).build();
        entityManager.persist(comment);
        entityManager.flush();
    }

    @Test
    @DisplayName("isDeleted를 null로 하고 prePersist 호출")
    public void testPrePersist_givenNull_willReturnVoid() {
        assertThat(comment.getIsDeleted()).isEqualTo(false);
    }

    @DisplayName("isDeleted를 false로 하고 prePersist 호출")
    @Test
    void testPrePersist_withNotNull_returnsVoid() {
        assertThat(comment.getIsDeleted()).isEqualTo(false);
    }

    @Test
    @DisplayName("같은 객체에 대한 equals 호출")
    void testEquals_givenSameObject_willReturnTrue() {
        // given
        CommentEntity comment = createCommentEntity();

        // when & then
        assertEquals(comment, comment);
    }

    @Test
    @DisplayName("다른 클래스의 인스턴스에 대한 equals 호출")
    void testEquals_givenDifferentObject_willReturnFalse() {
        assertNotEquals(comment, siteMember);
    }

    @Test
    @DisplayName("다른 프로퍼티를 갖는 인스턴스에 대한 equals 호출")
    void testEquals_givenDifferentProperty_willReturnFalse() {
        // given
        CommentCompositeKey testKey = CommentCompositeKey.builder()
                .postUlid("").path("").build();

        CommentEntity testEntity = CommentEntity.builder()
                .CommentEntity(createCommentEntityBuilder()
                        .postEntity(post)
                        .authMember(siteMember)
                        .createMember(siteMember)
                        .id(testKey).build())
                .isDeleted(null).build();

        assertNotEquals(createCommentEntity(), testEntity);
    }
}
