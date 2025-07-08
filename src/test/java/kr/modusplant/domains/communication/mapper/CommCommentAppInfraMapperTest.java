package kr.modusplant.domains.communication.mapper;

import kr.modusplant.domains.communication.app.http.response.CommCommentResponse;
import kr.modusplant.domains.communication.common.util.app.http.request.CommCommentInsertRequestTestUtils;
import kr.modusplant.domains.communication.common.util.app.http.response.CommCommentResponseTestUtils;
import kr.modusplant.domains.communication.common.util.entity.CommCommentEntityTestUtils;
import kr.modusplant.domains.communication.common.util.entity.CommPostEntityTestUtils;
import kr.modusplant.domains.communication.common.util.entity.CommSecondaryCategoryEntityTestUtils;
import kr.modusplant.domains.communication.persistence.entity.CommCommentEntity;
import kr.modusplant.domains.communication.persistence.entity.CommPostEntity;
import kr.modusplant.domains.communication.persistence.entity.CommSecondaryCategoryEntity;
import kr.modusplant.domains.communication.persistence.repository.CommPostRepository;
import kr.modusplant.domains.communication.persistence.repository.CommSecondaryCategoryRepository;
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
public class CommCommentAppInfraMapperTest implements
        CommCommentInsertRequestTestUtils, CommCommentResponseTestUtils, CommCommentEntityTestUtils,
        CommSecondaryCategoryEntityTestUtils, CommPostEntityTestUtils, SiteMemberEntityTestUtils {

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
