package kr.modusplant.domains.tip.mapper;

import kr.modusplant.domains.group.persistence.entity.PlantGroupEntity;
import kr.modusplant.domains.member.common.util.entity.SiteMemberEntityTestUtils;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.domains.tip.app.http.request.TipCommentInsertRequest;
import kr.modusplant.domains.tip.app.http.response.TipCommentResponse;
import kr.modusplant.domains.tip.common.util.app.http.request.TipCommentInsertRequestTestUtils;
import kr.modusplant.domains.tip.common.util.app.http.response.TipCommentResponseTestUtils;
import kr.modusplant.domains.tip.common.util.entity.TipCommentEntityTestUtils;
import kr.modusplant.domains.tip.common.util.entity.TipPostEntityTestUtils;
import kr.modusplant.domains.tip.persistence.entity.TipCommentEntity;
import kr.modusplant.domains.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.tip.persistence.repository.TipPostRepository;
import kr.modusplant.global.context.RepositoryOnlyContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RepositoryOnlyContext
public class TipCommentAppInfraMapperTest implements
        TipCommentInsertRequestTestUtils, TipCommentResponseTestUtils, TipCommentEntityTestUtils,
        TipPostEntityTestUtils, SiteMemberEntityTestUtils {

    private final TipCommentAppInfraMapper commentAppInfraMapper = new TipCommentAppInfraMapperImpl();
    private final TipPostRepository postRepository;
    private final SiteMemberRepository memberRepository;

    @Autowired
    public TipCommentAppInfraMapperTest(TipPostRepository postRepository, SiteMemberRepository memberRepository) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    private TipPostEntity savedPostEntity;
    private SiteMemberEntity savedMemberEntity;

    @BeforeEach
    void setUp() {
        SiteMemberEntity member = createMemberBasicUserEntity();
        PlantGroupEntity plantGroup = createPlantGroupEntity();
        TipPostEntity postEntity = createTipPostEntityBuilder()
                .group(plantGroup)
                .authMember(member)
                .createMember(member)
                .recommendationNumber(1)
                .viewCount(1)
                .isDeleted(true)
                .build();

        savedPostEntity = postRepository.save(postEntity);
        savedMemberEntity = memberRepository.save(member);
    }

    @DisplayName("엔티티를 응답으로 전환함")
    @Test
    void toTipCommentResponseTest() {
        // given
        TipCommentEntity commentEntity = createTipCommentEntityBuilder()
                .postEntity(savedPostEntity)
                .authMember(savedMemberEntity)
                .createMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        TipCommentResponse commentResponse = createTipCommentResponse(
                savedPostEntity.getUlid(), savedMemberEntity.getUuid(), savedMemberEntity.getUuid());

        // then
        assertThat(commentAppInfraMapper.toTipCommentResponse(commentEntity))
                .isEqualTo(commentResponse);
    }

    @DisplayName("삽입 요청을 엔티티로 전환하는 메서드 테스트")
    @Test
    void toTipCommentEntityTest() {
        // given
        TipCommentEntity commentEntity = createTipCommentEntityBuilder()
                .postEntity(savedPostEntity)
                .authMember(savedMemberEntity)
                .createMember(savedMemberEntity)
                .isDeleted(true)
                .build();

        // when
        TipCommentInsertRequest commentInsertRequest = createTipCommentInsertRequest(
                savedPostEntity.getUlid(), savedMemberEntity.getUuid());

        // then
        assertThat(commentAppInfraMapper.toTipCommentEntity(commentInsertRequest, postRepository, memberRepository))
                .isEqualTo(commentEntity);
    }

}
