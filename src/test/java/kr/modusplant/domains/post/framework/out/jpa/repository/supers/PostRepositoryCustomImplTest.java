package kr.modusplant.domains.post.framework.out.jpa.repository.supers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import kr.modusplant.domains.post.common.util.usecase.model.PostReadModelTestUtils;
import kr.modusplant.domains.post.framework.out.jpa.repository.PostRepositoryCustomImpl;
import kr.modusplant.domains.post.usecase.model.PostSummaryReadModel;
import kr.modusplant.framework.out.jpa.entity.CommPostEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static kr.modusplant.shared.persistence.common.constant.CommPrimaryCategoryConstant.TEST_COMM_PRIMARY_CATEGORY_UUID;
import static kr.modusplant.shared.persistence.common.constant.CommSecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORY_UUID;
import static kr.modusplant.shared.persistence.common.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostRepositoryCustomImplTest implements PostReadModelTestUtils {
    @Mock
    private EntityManager em;
    @Mock
    private Query query;
    @Mock
    private Query countQuery;
    @InjectMocks
    private PostRepositoryCustomImpl postRepositoryCustom;

    @Test
    @DisplayName("조회 조건 없이 발행된 게시글 조회하기")
    void testFindByDynamicConditionsAndIsPublishedTrue_givenNoFilter_willReturnAllPublishedPosts() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<PostSummaryReadModel> expectedResults = List.of(TEST_POST_SUMMARY_READ_MODEL);
        Long totalCount = 1L;

        given(em.createNativeQuery(anyString(), eq(CommPostEntity.class))).willReturn(query);
        given(em.createQuery(anyString())).willReturn(countQuery);
        given(query.setFirstResult(anyInt())).willReturn(query);
        given(query.setMaxResults(anyInt())).willReturn(query);
        given(query.getResultList()).willReturn(expectedResults);
        given(countQuery.getSingleResult()).willReturn(totalCount);

        // when
        Page<PostSummaryReadModel> result = postRepositoryCustom.findByDynamicConditionsAndIsPublishedTrue(
                null, null, null, pageable
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(totalCount);

        verify(em).createNativeQuery(anyString(), eq(CommPostEntity.class));
        verify(query).setFirstResult(0);
        verify(query).setMaxResults(10);
    }

    @Test
    @DisplayName("모든 필터 조건으로 발행된 게시글 조회하기")
    void testFindByDynamicConditionsAndIsPublishedTrue_givenAllFilters_willReturnFilteredPosts() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<UUID> secondaryCategoryUuids = List.of(TEST_COMM_SECONDARY_CATEGORY_UUID);
        List<PostSummaryReadModel> expectedResults = List.of(TEST_POST_SUMMARY_READ_MODEL);
        String keyword = "식물";
        Long totalCount = 1L;

        given(em.createNativeQuery(anyString(), eq(CommPostEntity.class))).willReturn(query);
        given(em.createQuery(anyString())).willReturn(countQuery);
        given(query.setParameter(anyString(), any())).willReturn(query);
        given(countQuery.setParameter(anyString(), any())).willReturn(countQuery);
        given(query.setFirstResult(anyInt())).willReturn(query);
        given(query.setMaxResults(anyInt())).willReturn(query);
        given(query.getResultList()).willReturn(expectedResults);
        given(countQuery.getSingleResult()).willReturn(totalCount);

        // when
        Page<PostSummaryReadModel> result = postRepositoryCustom.findByDynamicConditionsAndIsPublishedTrue(
                TEST_COMM_PRIMARY_CATEGORY_UUID, secondaryCategoryUuids, keyword, pageable
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(query).setParameter("primaryCategoryUuid", TEST_COMM_PRIMARY_CATEGORY_UUID);
        verify(query).setParameter("secondaryCategoryUuids", secondaryCategoryUuids);
        verify(query).setParameter("keyword", "%" + keyword + "%");
    }

    @Test
    @DisplayName("모든 필터 조건으로 회원별 발행된 게시글 조회하기")
    void testFindByAuthMemberAndDynamicConditionsAndIsPublishedTrue_givenAllFilters_willReturnFilteredMemberPosts() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<UUID> secondaryCategoryUuids = List.of(TEST_COMM_SECONDARY_CATEGORY_UUID);
        List<PostSummaryReadModel> expectedResults = List.of(TEST_POST_SUMMARY_READ_MODEL);
        String keyword = "식물";
        Long totalCount = 1L;

        given(em.createNativeQuery(anyString(), eq(CommPostEntity.class))).willReturn(query);
        given(em.createQuery(anyString())).willReturn(countQuery);
        given(query.setParameter(anyString(), any())).willReturn(query);
        given(countQuery.setParameter(anyString(), any())).willReturn(countQuery);
        given(query.setFirstResult(anyInt())).willReturn(query);
        given(query.setMaxResults(anyInt())).willReturn(query);
        given(query.getResultList()).willReturn(expectedResults);
        given(countQuery.getSingleResult()).willReturn(totalCount);

        // when
        Page<PostSummaryReadModel> result = postRepositoryCustom.findByAuthMemberAndDynamicConditionsAndIsPublishedTrue(
                MEMBER_BASIC_USER_UUID, TEST_COMM_PRIMARY_CATEGORY_UUID, secondaryCategoryUuids, keyword, pageable
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(query).setParameter("memberUuid", MEMBER_BASIC_USER_UUID);
        verify(query).setParameter("primaryCategoryUuid", TEST_COMM_PRIMARY_CATEGORY_UUID);
        verify(query).setParameter("secondaryCategoryUuids", secondaryCategoryUuids);
        verify(query).setParameter("keyword", "%" + keyword + "%");
    }


}