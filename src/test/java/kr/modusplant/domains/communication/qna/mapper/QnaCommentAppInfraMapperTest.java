package kr.modusplant.domains.communication.qna.mapper;

import kr.modusplant.domains.communication.qna.app.http.request.QnaCommentInsertRequest;
import kr.modusplant.domains.communication.qna.app.http.response.QnaCommentResponse;
import kr.modusplant.domains.communication.qna.common.util.app.http.request.QnaCommentInsertRequestTestUtils;
import kr.modusplant.domains.communication.qna.common.util.app.http.response.QnaCommentResponseTestUtils;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaCategoryEntityTestUtils;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaCommentEntityTestUtils;
import kr.modusplant.domains.communication.qna.common.util.entity.QnaPostEntityTestUtils;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCategoryEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCommentEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaCategoryRepository;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostRepository;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RepositoryOnlyContext
public class QnaCommentAppInfraMapperTest implements
        QnaCommentInsertRequestTestUtils, QnaCommentResponseTestUtils, QnaCommentEntityTestUtils,
        QnaCategoryEntityTestUtils, QnaPostEntityTestUtils, SiteMemberEntityTestUtils {

    private final QnaCommentAppInfraMapper commentAppInfraMapper = new QnaCommentAppInfraMapperImpl();
    private final QnaCategoryRepository categoryRepository;
    private final QnaPostRepository postRepository;
    private final SiteMemberRepository memberRepository;

    @Autowired
    public QnaCommentAppInfraMapperTest(QnaCategoryRepository categoryRepository,
                                        QnaPostRepository postRepository, SiteMemberRepository memberRepository) {
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    private QnaPostEntity savedPostEntity;
    private SiteMemberEntity savedMemberEntity;

    @BeforeEach
    void setUp() {
        SiteMemberEntity member = createMemberBasicUserEntity();
        QnaCategoryEntity category = categoryRepository.save(testQnaCategoryEntity);
        QnaPostEntity postEntity = createQnaPostEntityBuilder()
                .group(category)
                .authMember(member)
                .createMember(member)
                .likeCount(1)
                .viewCount(1L)
                .isDeleted(true)
                .build();

        savedPostEntity = postRepository.save(postEntity);
        savedMemberEntity = memberRepository.save(member);
    }

    @DisplayName("엔티티를 응답으로 전환함")
    @Test
    void toQnaCommentResponseTest() {
        // given
        QnaCommentEntity commentEntity = createQnaCommentEntityBuilder()
                .postEntity(savedPostEntity)
                .authMember(savedMemberEntity)
                .createMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        QnaCommentResponse commentResponse = createQnaCommentResponse(
                savedPostEntity.getUlid(), savedMemberEntity.getUuid(), savedMemberEntity.getUuid());

        // then
        assertThat(commentAppInfraMapper.toQnaCommentResponse(commentEntity))
                .isEqualTo(commentResponse);
    }

    @DisplayName("삽입 요청을 엔티티로 전환함")
    @Test
    void toQnaCommentEntityTest() {
        // given
        QnaCommentEntity commentEntity = createQnaCommentEntityBuilder()
                .postEntity(savedPostEntity)
                .authMember(savedMemberEntity)
                .createMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        QnaCommentInsertRequest commentInsertRequest = createQnaCommentInsertRequest(
                savedPostEntity.getUlid(), savedMemberEntity.getUuid());

        // then
        assertThat(commentAppInfraMapper.toQnaCommentEntity(commentInsertRequest, postRepository, memberRepository))
                .isEqualTo(commentEntity);
    }

}
