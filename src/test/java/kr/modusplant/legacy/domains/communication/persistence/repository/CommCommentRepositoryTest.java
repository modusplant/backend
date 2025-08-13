package kr.modusplant.legacy.domains.communication.persistence.repository;

import kr.modusplant.global.context.RepositoryOnlyContext;
import kr.modusplant.legacy.domains.communication.common.util.entity.CommCommentEntityTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.entity.CommPostEntityTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.entity.CommPrimaryCategoryEntityTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.entity.CommSecondaryCategoryEntityTestUtils;
import kr.modusplant.legacy.domains.communication.persistence.entity.CommCommentEntity;
import kr.modusplant.legacy.domains.communication.persistence.entity.CommPostEntity;
import kr.modusplant.legacy.domains.communication.persistence.entity.CommPrimaryCategoryEntity;
import kr.modusplant.legacy.domains.communication.persistence.entity.CommSecondaryCategoryEntity;
import kr.modusplant.legacy.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.legacy.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.legacy.domains.member.persistence.repository.SiteMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RepositoryOnlyContext
public class CommCommentRepositoryTest implements
        CommCommentEntityTestUtils, CommPrimaryCategoryEntityTestUtils, CommSecondaryCategoryEntityTestUtils, CommPostEntityTestUtils, SiteMemberEntityTestUtils {

    private final CommCommentRepository commentRepository;
    private final CommPrimaryCategoryRepository primaryCategoryRepository;
    private final CommSecondaryCategoryRepository secondaryCategoryRepository;
    private final CommPostRepository postRepository;
    private final SiteMemberRepository memberRepository;

    @Autowired
    public CommCommentRepositoryTest(CommCommentRepository commentRepository, CommPrimaryCategoryRepository primaryCategoryRepository, CommSecondaryCategoryRepository secondaryCategoryRepository,
                                     CommPostRepository postRepository, SiteMemberRepository memberRepository) {
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
        CommPrimaryCategoryEntity primaryCategory = primaryCategoryRepository.save(createTestCommPrimaryCategoryEntity());
        CommSecondaryCategoryEntity secondaryCategory = secondaryCategoryRepository.save(createTestCommSecondaryCategoryEntity());
        CommPostEntity postEntity = createCommPostEntityBuilder()
                .primaryCategory(primaryCategory)
                .secondaryCategory(secondaryCategory)
                .authMember(member)
                .createMember(member)
                .likeCount(1)
                .viewCount(1L)
                .isDeleted(true)
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
        CommCommentEntity savedCommentEntity = commentRepository.save(commentEntity);
        CommCommentEntity result = commentRepository.findByPostUlidAndPath(
                savedCommentEntity.getPostUlid(), savedCommentEntity.getPath()).orElseThrow();

        // then
        assertThat(savedCommentEntity).isEqualTo(result);
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
        CommCommentEntity savedCommentEntity = commentRepository.save(commentEntity);
        List<CommCommentEntity> result = commentRepository.findByPostEntity(savedPostEntity);

        // then
        assertThat(List.of(savedCommentEntity)).isEqualTo(result);
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
        CommCommentEntity savedCommentEntity = commentRepository.save(commentEntity);
        List<CommCommentEntity> result = commentRepository.findByAuthMember(savedMemberEntity);

        // then
        assertThat(List.of(savedCommentEntity)).isEqualTo(result);
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
        CommCommentEntity savedCommentEntity = commentRepository.save(commentEntity);
        List<CommCommentEntity> result = commentRepository.findByAuthMember(savedMemberEntity);

        // then
        assertThat(List.of(savedCommentEntity)).isEqualTo(result);
    }

    @Test
    @DisplayName("댓글 내용으로 컨텐츠 댓글 찾기")
    void findByContentTest() {
        // given
        CommCommentEntity commentEntity = createCommCommentEntityBuilder()
                .postEntity(savedPostEntity)
                .authMember(savedMemberEntity)
                .createMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        CommCommentEntity savedCommentEntity = commentRepository.save(commentEntity);
        List<CommCommentEntity> result = commentRepository.findByContent(savedCommentEntity.getContent());

        // then
        assertThat(List.of(savedCommentEntity)).isEqualTo(result);
    }

}