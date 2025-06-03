package kr.modusplant.domains.communication.qna.persistence.repository;

import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCategoryEntity;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaCategoryEntityTestUtils;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaCommentEntityTestUtils;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaPostEntityTestUtils;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCategoryEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCommentEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
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
public class QnaCommentRepositoryTest implements
        QnaCommentEntityTestUtils, QnaCategoryEntityTestUtils, QnaPostEntityTestUtils, SiteMemberEntityTestUtils {

    private final QnaCommentRepository commentRepository;
    private final QnaCategoryRepository categoryRepository;
    private final QnaPostRepository postRepository;
    private final SiteMemberRepository memberRepository;

    @Autowired
    public QnaCommentRepositoryTest(QnaCommentRepository commentRepository, QnaCategoryRepository categoryRepository,
                                    QnaPostRepository postRepository, SiteMemberRepository memberRepository) {
        this.commentRepository = commentRepository;
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    private QnaPostEntity savedPostEntity;
    private SiteMemberEntity savedMemberEntity;

    @BeforeEach
    void setUp() {
        SiteMemberEntity member = createMemberBasicUserEntity();
        QnaCategoryEntity category = categoryRepository.save(createTestQnaCategoryEntity());
        QnaPostEntity postEntity = createQnaPostEntityBuilder()
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
        QnaCommentEntity commentEntity = createQnaCommentEntityBuilder()
                .postEntity(savedPostEntity)
                .authMember(savedMemberEntity)
                .createMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        QnaCommentEntity savedCommentEntity = commentRepository.save(commentEntity);
        QnaCommentEntity result = commentRepository.findByPostUlidAndPath(
                savedCommentEntity.getPostUlid(), savedCommentEntity.getPath()).get();

        // then
        assertThat(savedCommentEntity).isEqualTo(result);
    }

    @Test
    @DisplayName("게시글 ulid로 팁 댓글 찾기")
    void findByPostEntityTest() {
        // given
        QnaCommentEntity commentEntity = createQnaCommentEntityBuilder()
                .postEntity(savedPostEntity)
                .authMember(savedMemberEntity)
                .createMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        QnaCommentEntity savedCommentEntity = commentRepository.save(commentEntity);
        List<QnaCommentEntity> result = commentRepository.findByPostEntity(savedPostEntity);

        // then
        assertThat(List.of(savedCommentEntity)).isEqualTo(result);
    }

    @Test
    @DisplayName("인증된 사용자로 팁 댓글 찾기")
    void findByAuthMemberTest() {
        // given
        QnaCommentEntity commentEntity = createQnaCommentEntityBuilder()
                .postEntity(savedPostEntity)
                .authMember(savedMemberEntity)
                .createMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        QnaCommentEntity savedCommentEntity = commentRepository.save(commentEntity);
        List<QnaCommentEntity> result = commentRepository.findByAuthMember(savedMemberEntity);

        // then
        assertThat(List.of(savedCommentEntity)).isEqualTo(result);
    }

    @Test
    @DisplayName("댓글을 생성한 사용자로 팁 댓글 찾기")
    void findByCreateMemberTest() {
        // given
        QnaCommentEntity commentEntity = createQnaCommentEntityBuilder()
                .postEntity(savedPostEntity)
                .authMember(savedMemberEntity)
                .createMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        QnaCommentEntity savedCommentEntity = commentRepository.save(commentEntity);
        List<QnaCommentEntity> result = commentRepository.findByAuthMember(savedMemberEntity);

        // then
        assertThat(List.of(savedCommentEntity)).isEqualTo(result);
    }

    @Test
    @DisplayName("댓글 내용으로 팁 댓글 찾기")
    void findByContentTest() {
        // given
        QnaCommentEntity commentEntity = createQnaCommentEntityBuilder()
                .postEntity(savedPostEntity)
                .authMember(savedMemberEntity)
                .createMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        QnaCommentEntity savedCommentEntity = commentRepository.save(commentEntity);
        List<QnaCommentEntity> result = commentRepository.findByContent(savedCommentEntity.getContent());

        // then
        assertThat(List.of(savedCommentEntity)).isEqualTo(result);
    }

}