package kr.modusplant.domains.communication.conversation.persistence.repository;

import kr.modusplant.domains.communication.conversation.common.util.entity.ConvCategoryEntityTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvCommentEntityTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvPostEntityTestUtils;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCategoryEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCommentEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvPostEntity;
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
public class ConvCommentRepositoryTest implements
        ConvCommentEntityTestUtils, ConvCategoryEntityTestUtils, ConvPostEntityTestUtils, SiteMemberEntityTestUtils {

    private final ConvCommentRepository commentRepository;
    private final ConvCategoryRepository categoryRepository;
    private final ConvPostRepository postRepository;
    private final SiteMemberRepository memberRepository;

    @Autowired
    public ConvCommentRepositoryTest(ConvCommentRepository commentRepository, ConvCategoryRepository categoryRepository,
                                    ConvPostRepository postRepository, SiteMemberRepository memberRepository) {
        this.commentRepository = commentRepository;
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    private ConvPostEntity savedPostEntity;
    private SiteMemberEntity savedMemberEntity;

    @BeforeEach
    void setUp() {
        SiteMemberEntity member = createMemberBasicUserEntity();
        ConvCategoryEntity category = categoryRepository.save(testConvCategoryEntity);
        ConvPostEntity postEntity = createConvPostEntityBuilder()
                .group(category)
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
        ConvCommentEntity commentEntity = createConvCommentEntityBuilder()
                .postEntity(savedPostEntity)
                .authMember(savedMemberEntity)
                .createMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        ConvCommentEntity savedCommentEntity = commentRepository.save(commentEntity);
        ConvCommentEntity result = commentRepository.findByPostUlidAndPath(
                savedCommentEntity.getPostUlid(), savedCommentEntity.getPath()).get();

        // then
        assertThat(savedCommentEntity).isEqualTo(result);
    }

    @Test
    @DisplayName("게시글 ulid로 팁 댓글 찾기")
    void findByPostEntityTest() {
        // given
        ConvCommentEntity commentEntity = createConvCommentEntityBuilder()
                .postEntity(savedPostEntity)
                .authMember(savedMemberEntity)
                .createMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        ConvCommentEntity savedCommentEntity = commentRepository.save(commentEntity);
        List<ConvCommentEntity> result = commentRepository.findByPostEntity(savedPostEntity);

        // then
        assertThat(List.of(savedCommentEntity)).isEqualTo(result);
    }

    @Test
    @DisplayName("인증된 사용자로 팁 댓글 찾기")
    void findByAuthMemberTest() {
        // given
        ConvCommentEntity commentEntity = createConvCommentEntityBuilder()
                .postEntity(savedPostEntity)
                .authMember(savedMemberEntity)
                .createMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        ConvCommentEntity savedCommentEntity = commentRepository.save(commentEntity);
        List<ConvCommentEntity> result = commentRepository.findByAuthMember(savedMemberEntity);

        // then
        assertThat(List.of(savedCommentEntity)).isEqualTo(result);
    }

    @Test
    @DisplayName("댓글을 생성한 사용자로 팁 댓글 찾기")
    void findByCreateMemberTest() {
        // given
        ConvCommentEntity commentEntity = createConvCommentEntityBuilder()
                .postEntity(savedPostEntity)
                .authMember(savedMemberEntity)
                .createMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        ConvCommentEntity savedCommentEntity = commentRepository.save(commentEntity);
        List<ConvCommentEntity> result = commentRepository.findByAuthMember(savedMemberEntity);

        // then
        assertThat(List.of(savedCommentEntity)).isEqualTo(result);
    }

    @Test
    @DisplayName("댓글 내용으로 팁 댓글 찾기")
    void findByContentTest() {
        // given
        ConvCommentEntity commentEntity = createConvCommentEntityBuilder()
                .postEntity(savedPostEntity)
                .authMember(savedMemberEntity)
                .createMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        ConvCommentEntity savedCommentEntity = commentRepository.save(commentEntity);
        List<ConvCommentEntity> result = commentRepository.findByContent(savedCommentEntity.getContent());

        // then
        assertThat(List.of(savedCommentEntity)).isEqualTo(result);
    }

}