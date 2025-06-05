package kr.modusplant.domains.communication.tip.persistence.repository;

import kr.modusplant.domains.communication.tip.common.util.entity.TipCategoryEntityTestUtils;
import kr.modusplant.domains.communication.tip.common.util.entity.TipCommentEntityTestUtils;
import kr.modusplant.domains.communication.tip.common.util.entity.TipPostEntityTestUtils;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCategoryEntity;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCommentEntity;
import kr.modusplant.domains.communication.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RepositoryOnlyContext
public class TipCommentRepositoryTest implements
        TipCommentEntityTestUtils, TipCategoryEntityTestUtils, TipPostEntityTestUtils, SiteMemberEntityTestUtils {

    private final TipCommentRepository commentRepository;
    private final TipCategoryRepository categoryRepository;
    private final TipPostRepository postRepository;
    private final SiteMemberRepository memberRepository;

    @Autowired
    public TipCommentRepositoryTest(TipCommentRepository commentRepository, TipCategoryRepository categoryRepository,
                                    TipPostRepository postRepository,SiteMemberRepository memberRepository) {
        this.commentRepository = commentRepository;
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    private TipPostEntity savedPostEntity;
    private SiteMemberEntity savedMemberEntity;

    @BeforeEach
    void setUp() {
        SiteMemberEntity member = createMemberBasicUserEntity();
        TipCategoryEntity category = categoryRepository.save(createTestTipCategoryEntity());
        TipPostEntity postEntity = createTipPostEntityBuilder()
                .category(category)
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
    @DisplayName("게시글 ulid와 구체화된 경로로 팁 댓글 찾기")
    void findByPostEntityAndPathTest() {
        // given
        TipCommentEntity commentEntity = createTipCommentEntityBuilder()
                .postEntity(savedPostEntity)
                .authMember(savedMemberEntity)
                .createMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        TipCommentEntity savedCommentEntity = commentRepository.save(commentEntity);
        TipCommentEntity result = commentRepository.findByPostUlidAndPath(
                savedCommentEntity.getPostUlid(), savedCommentEntity.getPath()).get();

        // then
        assertThat(savedCommentEntity).isEqualTo(result);
    }

    @Test
    @DisplayName("게시글 ulid로 팁 댓글 찾기")
    void findByPostEntityTest() {
        // given
        TipCommentEntity commentEntity = createTipCommentEntityBuilder()
                .postEntity(savedPostEntity)
                .authMember(savedMemberEntity)
                .createMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        TipCommentEntity savedCommentEntity = commentRepository.save(commentEntity);
        List<TipCommentEntity> result = commentRepository.findByPostEntity(savedPostEntity);

        // then
        assertThat(List.of(savedCommentEntity)).isEqualTo(result);
    }

    @Test
    @DisplayName("인증된 사용자로 팁 댓글 찾기")
    void findByAuthMemberTest() {
        // given
        TipCommentEntity commentEntity = createTipCommentEntityBuilder()
                .postEntity(savedPostEntity)
                .authMember(savedMemberEntity)
                .createMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        TipCommentEntity savedCommentEntity = commentRepository.save(commentEntity);
        List<TipCommentEntity> result = commentRepository.findByAuthMember(savedMemberEntity);

        // then
        assertThat(List.of(savedCommentEntity)).isEqualTo(result);
    }

    @Test
    @DisplayName("댓글을 생성한 사용자로 팁 댓글 찾기")
    void findByCreateMemberTest() {
        // given
        TipCommentEntity commentEntity = createTipCommentEntityBuilder()
                .postEntity(savedPostEntity)
                .authMember(savedMemberEntity)
                .createMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        TipCommentEntity savedCommentEntity = commentRepository.save(commentEntity);
        List<TipCommentEntity> result = commentRepository.findByAuthMember(savedMemberEntity);

        // then
        assertThat(List.of(savedCommentEntity)).isEqualTo(result);
    }

    @Test
    @DisplayName("댓글 내용으로 팁 댓글 찾기")
    void findByContentTest() {
        // given
        TipCommentEntity commentEntity = createTipCommentEntityBuilder()
                .postEntity(savedPostEntity)
                .authMember(savedMemberEntity)
                .createMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        TipCommentEntity savedCommentEntity = commentRepository.save(commentEntity);
        List<TipCommentEntity> result = commentRepository.findByContent(savedCommentEntity.getContent());

        // then
        assertThat(List.of(savedCommentEntity)).isEqualTo(result);
    }

}