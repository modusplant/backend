package kr.modusplant.domains.comment.framework.outbound.persistence.jpa.repository;

import kr.modusplant.domains.comment.common.util.framework.outbound.persistence.jpa.entity.CommentEntityTestUtils;
import kr.modusplant.domains.comment.framework.outbound.persistence.jpa.entity.CommentEntity;
import kr.modusplant.domains.member.common.util.framework.outbound.jpa.entity.MemberEntityTestUtils;
import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.MemberJpaRepository;
import kr.modusplant.domains.post.common.util.framework.outbound.jpa.entity.PostEntityTestUtils;
import kr.modusplant.domains.post.common.util.framework.outbound.jpa.entity.PrimaryCategoryEntityTestUtils;
import kr.modusplant.domains.post.common.util.framework.outbound.jpa.entity.SecondaryCategoryEntityTestUtils;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.PrimaryCategoryEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.entity.SecondaryCategoryEntity;
import kr.modusplant.domains.post.framework.outbound.jpa.mapper.PrimaryCategoryJpaRepository;
import kr.modusplant.domains.post.framework.outbound.jpa.mapper.SecondaryCategoryJpaRepository;
import kr.modusplant.domains.post.framework.outbound.jpa.repository.PostJpaRepository;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@RepositoryOnlyContext
public class CommentJpaRepositoryTest implements
        CommentEntityTestUtils, PrimaryCategoryEntityTestUtils, SecondaryCategoryEntityTestUtils, PostEntityTestUtils, MemberEntityTestUtils {

    private final CommentJpaRepository commentRepository;
    private final PrimaryCategoryJpaRepository primaryCategoryRepository;
    private final SecondaryCategoryJpaRepository secondaryCategoryRepository;
    private final PostJpaRepository postRepository;
    private final MemberJpaRepository memberRepository;

    @Autowired
    public CommentJpaRepositoryTest(CommentJpaRepository commentRepository, PrimaryCategoryJpaRepository primaryCategoryRepository, SecondaryCategoryJpaRepository secondaryCategoryRepository,
                                    PostJpaRepository postRepository, MemberJpaRepository memberRepository) {
        this.commentRepository = commentRepository;
        this.primaryCategoryRepository = primaryCategoryRepository;
        this.secondaryCategoryRepository = secondaryCategoryRepository;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    private PostEntity savedPostEntity;
    private MemberEntity savedMemberEntity;

    @BeforeEach
    void setUp() {
        MemberEntity member = createMemberBasicUserEntity();
        PrimaryCategoryEntity primaryCategory = primaryCategoryRepository.save(createPrimaryCategoryEntity());
        SecondaryCategoryEntity secondaryCategory = secondaryCategoryRepository.save(createSecondaryCategoryEntityBuilder().primaryCategory(primaryCategory).build());
        PostEntity postEntity = createPublishedPostEntityBuilder()
                .primaryCategory(primaryCategory)
                .secondaryCategory(secondaryCategory)
                .authMember(member)
                .likeCount(1)
                .viewCount(1L)
                .isPublished(true)
                .build();

        savedMemberEntity = memberRepository.save(member);
        savedPostEntity = postRepository.save(postEntity);
    }

    @Test
    @DisplayName("게시글 ulid와 구체화된 경로로 컨텐츠 댓글 찾기")
    void findByPostAndPathTest() {
        // given
        CommentEntity commentEntity = createCommentEntityBuilder()
                .post(savedPostEntity)
                .authMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        CommentEntity savedCommentEntity = commentRepository.save(commentEntity);
        CommentEntity result = commentRepository.findByPostUlidAndPath(
                savedCommentEntity.getPost().getUlid(), savedCommentEntity.getPath()).orElseThrow();

        // then
        assertThat(savedCommentEntity).isEqualTo(result);
    }

    @Test
    @DisplayName("게시글 ulid로 컨텐츠 댓글 찾기")
    void findByPostTest() {
        // given
        CommentEntity commentEntity = createCommentEntityBuilder()
                .post(savedPostEntity)
                .authMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        CommentEntity savedCommentEntity = commentRepository.save(commentEntity);
        List<CommentEntity> result = commentRepository.findByPost(savedPostEntity);

        // then
        assertThat(List.of(savedCommentEntity)).isEqualTo(result);
    }

    @Test
    @DisplayName("인증된 사용자로 컨텐츠 댓글 찾기")
    void findByAuthMemberTest() {
        // given
        CommentEntity commentEntity = createCommentEntityBuilder()
                .post(savedPostEntity)
                .authMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        CommentEntity savedCommentEntity = commentRepository.save(commentEntity);
        List<CommentEntity> result = commentRepository.findByAuthMember(savedMemberEntity);

        // then
        assertThat(List.of(savedCommentEntity)).isEqualTo(result);
    }

    @Test
    @DisplayName("댓글을 생성한 사용자로 컨텐츠 댓글 찾기")
    void findByCreateMemberTest() {
        // given
        CommentEntity commentEntity = createCommentEntityBuilder()
                .post(savedPostEntity)
                .authMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        CommentEntity savedCommentEntity = commentRepository.save(commentEntity);
        List<CommentEntity> result = commentRepository.findByAuthMember(savedMemberEntity);

        // then
        assertThat(List.of(savedCommentEntity)).isEqualTo(result);
    }

//    @Test
//    @DisplayName("댓글 내용으로 컨텐츠 댓글 찾기")
//    void findByContentTest() {
//        // given
//        CommCommentEntity commentEntity = createCommCommentEntityBuilder()
//                .postEntity(savedPostEntity)
//                .authMember(savedMemberEntity)
//                .isDeleted(true)
//                .build();
//
//        // when
//        CommCommentEntity savedCommCommentEntity = commentRepository.save(commentEntity);
//        List<CommCommentEntity> result = commentRepository.findByContent(savedCommCommentEntity.getContent());
//
//        // then
//        assertThat(List.of(savedCommCommentEntity)).isEqualTo(result);
//    }

    @Test
    @DisplayName("댓글 엔터티 toString 호출 시 순환 오류 발생 여부 확인")
    void testToString_givenCommCommentEntity_willReturnRepresentative() {
        // given
        CommentEntity commentEntity = createCommentEntityBuilder()
                .post(savedPostEntity)
                .authMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        CommentEntity savedCommentEntity = commentRepository.save(commentEntity);

        // then
        assertDoesNotThrow(savedCommentEntity::toString);
    }
}