package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.*;
import kr.modusplant.framework.jpa.entity.common.util.*;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@RepositoryOnlyContext
public class CommCommentJpaRepositoryTest implements
        CommCommentEntityTestUtils, CommPrimaryCategoryEntityTestUtils, CommSecondaryCategoryEntityTestUtils, CommPostEntityTestUtils, SiteMemberEntityTestUtils {

    private final CommCommentJpaRepository commentRepository;
    private final CommPrimaryCategoryJpaRepository primaryCategoryRepository;
    private final CommSecondaryCategoryJpaRepository secondaryCategoryRepository;
    private final CommPostJpaRepository postRepository;
    private final SiteMemberJpaRepository memberRepository;

    @Autowired
    public CommCommentJpaRepositoryTest(CommCommentJpaRepository commentRepository, CommPrimaryCategoryJpaRepository primaryCategoryRepository, CommSecondaryCategoryJpaRepository secondaryCategoryRepository,
                                        CommPostJpaRepository postRepository, SiteMemberJpaRepository memberRepository) {
        this.commentRepository = commentRepository;
        this.primaryCategoryRepository = primaryCategoryRepository;
        this.secondaryCategoryRepository = secondaryCategoryRepository;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    private CommPostEntity savedPostEntity;
    private SiteMemberEntity savedMemberEntity;

    @BeforeEach
    void setUp() {
        SiteMemberEntity member = createMemberBasicUserEntity();
        CommPrimaryCategoryEntity primaryCategory = primaryCategoryRepository.save(createCommPrimaryCategoryEntity());
        CommSecondaryCategoryEntity secondaryCategory = secondaryCategoryRepository.save(createCommSecondaryCategoryEntityBuilder().primaryCategory(primaryCategory).build());
        CommPostEntity postEntity = createCommPostEntityBuilder()
                .primaryCategory(primaryCategory)
                .secondaryCategory(secondaryCategory)
                .authMember(member)
                .createMember(member)
                .likeCount(1)
                .viewCount(1L)
                .isPublished(true)
                .build();

        savedMemberEntity = memberRepository.save(member);
        savedPostEntity = postRepository.save(postEntity);
    }

    @Test
    @DisplayName("게시글 ulid와 구체화된 경로로 컨텐츠 댓글 찾기")
    void findByPostEntityAndPathTest() {
        // given
        CommCommentEntity commentEntity = createCommCommentEntityBuilder()
                .postEntity(savedPostEntity)
                .authMember(savedMemberEntity)
                .createMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        CommCommentEntity savedCommCommentEntity = commentRepository.save(commentEntity);
        CommCommentEntity result = commentRepository.findByPostUlidAndPath(
                savedCommCommentEntity.getPostUlid(), savedCommCommentEntity.getPath()).orElseThrow();

        // then
        assertThat(savedCommCommentEntity).isEqualTo(result);
    }

    @Test
    @DisplayName("게시글 ulid로 컨텐츠 댓글 찾기")
    void findByPostEntityTest() {
        // given
        CommCommentEntity commentEntity = createCommCommentEntityBuilder()
                .postEntity(savedPostEntity)
                .authMember(savedMemberEntity)
                .createMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        CommCommentEntity savedCommCommentEntity = commentRepository.save(commentEntity);
        List<CommCommentEntity> result = commentRepository.findByPostEntity(savedPostEntity);

        // then
        assertThat(List.of(savedCommCommentEntity)).isEqualTo(result);
    }

    @Test
    @DisplayName("인증된 사용자로 컨텐츠 댓글 찾기")
    void findByAuthMemberTest() {
        // given
        CommCommentEntity commentEntity = createCommCommentEntityBuilder()
                .postEntity(savedPostEntity)
                .authMember(savedMemberEntity)
                .createMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        CommCommentEntity savedCommCommentEntity = commentRepository.save(commentEntity);
        List<CommCommentEntity> result = commentRepository.findByAuthMember(savedMemberEntity);

        // then
        assertThat(List.of(savedCommCommentEntity)).isEqualTo(result);
    }

    @Test
    @DisplayName("댓글을 생성한 사용자로 컨텐츠 댓글 찾기")
    void findByCreateMemberTest() {
        // given
        CommCommentEntity commentEntity = createCommCommentEntityBuilder()
                .postEntity(savedPostEntity)
                .authMember(savedMemberEntity)
                .createMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        CommCommentEntity savedCommCommentEntity = commentRepository.save(commentEntity);
        List<CommCommentEntity> result = commentRepository.findByAuthMember(savedMemberEntity);

        // then
        assertThat(List.of(savedCommCommentEntity)).isEqualTo(result);
    }

//    @Test
//    @DisplayName("댓글 내용으로 컨텐츠 댓글 찾기")
//    void findByContentTest() {
//        // given
//        CommCommentEntity commentEntity = createCommCommentEntityBuilder()
//                .postEntity(savedPostEntity)
//                .authMember(savedMemberEntity)
//                .createMember(savedMemberEntity)
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
        CommCommentEntity commentEntity = createCommCommentEntityBuilder()
                .postEntity(savedPostEntity)
                .authMember(savedMemberEntity)
                .createMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        CommCommentEntity savedCommCommentEntity = commentRepository.save(commentEntity);

        // then
        assertDoesNotThrow(savedCommCommentEntity::toString);
    }
}