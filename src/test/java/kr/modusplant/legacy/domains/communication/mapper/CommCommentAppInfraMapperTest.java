package kr.modusplant.legacy.domains.communication.mapper;

import kr.modusplant.framework.out.jpa.entity.CommCommentEntity;
import kr.modusplant.framework.out.jpa.entity.CommPostEntity;
import kr.modusplant.framework.out.jpa.entity.CommSecondaryCategoryEntity;
import kr.modusplant.framework.out.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.jpa.repository.CommPostRepository;
import kr.modusplant.framework.out.jpa.repository.CommSecondaryCategoryRepository;
import kr.modusplant.framework.out.jpa.repository.SiteMemberRepository;
import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import kr.modusplant.legacy.domains.communication.app.http.response.CommCommentResponse;
import kr.modusplant.legacy.domains.communication.common.util.app.http.request.CommCommentInsertRequestTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.app.http.response.CommCommentResponseTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.entity.CommCommentEntityTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.entity.CommPostEntityTestUtils;
import kr.modusplant.legacy.domains.communication.common.util.entity.CommSecondaryCategoryEntityTestUtils;
import kr.modusplant.legacy.domains.member.common.util.entity.SiteMemberEntityConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RepositoryOnlyContext
public class CommCommentAppInfraMapperTest implements
        CommCommentInsertRequestTestUtils, CommCommentResponseTestUtils, CommCommentEntityTestUtils,
        CommSecondaryCategoryEntityTestUtils, CommPostEntityTestUtils, SiteMemberEntityConstant {

    private final CommCommentAppInfraMapper commentAppInfraMapper = new CommCommentAppInfraMapperImpl();
    private final CommSecondaryCategoryRepository categoryRepository;
    private final CommPostRepository postRepository;
    private final SiteMemberRepository memberRepository;

    @Autowired
    public CommCommentAppInfraMapperTest(CommSecondaryCategoryRepository categoryRepository,
                                         CommPostRepository postRepository, SiteMemberRepository memberRepository) {
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    private CommPostEntity savedPostEntity;
    private SiteMemberEntity savedMemberEntity;

    @BeforeEach
    void setUp() {
        SiteMemberEntity member = createMemberBasicUserEntity();
        CommSecondaryCategoryEntity category = categoryRepository.save(createTestCommSecondaryCategoryEntity());
        CommPostEntity postEntity = createCommPostEntityBuilder()
                .secondaryCategory(category)
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
    void toCommCommentResponseTest() {
        // given
        CommCommentEntity commentEntity = createCommCommentEntityBuilder()
                .postEntity(savedPostEntity)
                .authMember(savedMemberEntity)
                .createMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        CommCommentResponse commentResponse = createCommCommentResponse(
                savedPostEntity.getUlid(), savedMemberEntity.getUuid(), savedMemberEntity.getNickname());

        // then
        assertThat(commentAppInfraMapper.toCommCommentResponse(commentEntity))
                .isEqualTo(commentResponse);
    }
}
