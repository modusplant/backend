package kr.modusplant.domains.communication.conversation.mapper;

import kr.modusplant.domains.communication.conversation.app.http.request.ConvCommentInsertRequest;
import kr.modusplant.domains.communication.conversation.app.http.response.ConvCommentResponse;
import kr.modusplant.domains.communication.conversation.common.util.app.http.request.ConvCommentInsertRequestTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.app.http.response.ConvCommentResponseTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvCategoryEntityTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvCommentEntityTestUtils;
import kr.modusplant.domains.communication.conversation.common.util.entity.ConvPostEntityTestUtils;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCategoryEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCommentEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvPostEntity;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvCategoryRepository;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvPostRepository;
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
public class ConvCommentAppInfraMapperTest implements
        ConvCommentInsertRequestTestUtils, ConvCommentResponseTestUtils, ConvCommentEntityTestUtils,
        ConvCategoryEntityTestUtils, ConvPostEntityTestUtils, SiteMemberEntityTestUtils {

    private final ConvCommentAppInfraMapper commentAppInfraMapper = new ConvCommentAppInfraMapperImpl();
    private final ConvCategoryRepository categoryRepository;
    private final ConvPostRepository postRepository;
    private final SiteMemberRepository memberRepository;

    @Autowired
    public ConvCommentAppInfraMapperTest(ConvCategoryRepository categoryRepository,
                                         ConvPostRepository postRepository, SiteMemberRepository memberRepository) {
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    private ConvPostEntity savedPostEntity;
    private SiteMemberEntity savedMemberEntity;

    @BeforeEach
    void setUp() {
        SiteMemberEntity member = createMemberBasicUserEntity();
        ConvCategoryEntity category = categoryRepository.save(createTestConvCategoryEntity());
        ConvPostEntity postEntity = createConvPostEntityBuilder()
                .category(category)
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
    void toConvCommentResponseTest() {
        // given
        ConvCommentEntity commentEntity = createConvCommentEntityBuilder()
                .postEntity(savedPostEntity)
                .authMember(savedMemberEntity)
                .createMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        ConvCommentResponse commentResponse = createConvCommentResponse(
                savedPostEntity.getUlid(), savedMemberEntity.getUuid(), savedMemberEntity.getNickname());

        // then
        assertThat(commentAppInfraMapper.toConvCommentResponse(commentEntity))
                .isEqualTo(commentResponse);
    }

    @DisplayName("삽입 요청을 엔티티로 전환함")
    @Test
    void toConvCommentEntityTest() {
        // given
        ConvCommentEntity commentEntity = createConvCommentEntityBuilder()
                .postEntity(savedPostEntity)
                .authMember(savedMemberEntity)
                .createMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        ConvCommentInsertRequest commentInsertRequest = createConvCommentInsertRequest(
                savedPostEntity.getUlid(), savedMemberEntity.getUuid());

        // then
        assertThat(commentAppInfraMapper.toConvCommentEntity(commentInsertRequest, postRepository, memberRepository))
                .isEqualTo(commentEntity);
    }

}
