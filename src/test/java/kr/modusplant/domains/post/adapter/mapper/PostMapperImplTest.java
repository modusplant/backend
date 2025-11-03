package kr.modusplant.domains.post.adapter.mapper;

import kr.modusplant.domains.post.common.util.usecase.model.PostReadModelTestUtils;
import kr.modusplant.domains.post.usecase.port.mapper.PostMapper;
import kr.modusplant.domains.post.usecase.response.PostDetailResponse;
import kr.modusplant.domains.post.usecase.response.PostSummaryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.TEST_POST_CONTENT_BINARY_DATA;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PostMapperImplTest implements PostReadModelTestUtils {
    private final PostMapper postMapper = new PostMapperImpl();

    @Test
    @DisplayName("toPostDetailResponse로 PostDetail응답 반환하기")
    void testToPostDetailResponse_givenPostDetailModelAndContentAndViewCount_willReturnPostDetailResponse() {
        // given
        long viewCount = 1L;

        // when
        PostDetailResponse result = postMapper.toPostDetailResponse(TEST_PUBLISHED_POST_DETAIL_READ_MODEL,TEST_POST_CONTENT_BINARY_DATA,viewCount);

        // then
        assertEquals(result.ulid(), TEST_PUBLISHED_POST_DETAIL_READ_MODEL.ulid());
        assertEquals(result.primaryCategoryUuid(), TEST_PUBLISHED_POST_DETAIL_READ_MODEL.primaryCategoryUuid());
        assertEquals(result.primaryCategory(), TEST_PUBLISHED_POST_DETAIL_READ_MODEL.primaryCategory());
        assertEquals(result.secondaryCategoryUuid(), TEST_PUBLISHED_POST_DETAIL_READ_MODEL.secondaryCategoryUuid());
        assertEquals(result.secondaryCategory(), TEST_PUBLISHED_POST_DETAIL_READ_MODEL.secondaryCategory());
        assertEquals(result.authorUuid(), TEST_PUBLISHED_POST_DETAIL_READ_MODEL.authorUuid());
        assertEquals(result.nickname(), TEST_PUBLISHED_POST_DETAIL_READ_MODEL.nickname());
        assertEquals(result.likeCount(), TEST_PUBLISHED_POST_DETAIL_READ_MODEL.likeCount());
        assertEquals(result.viewCount(),viewCount);
        assertEquals(result.title(), TEST_PUBLISHED_POST_DETAIL_READ_MODEL.title());
        assertEquals(result.content(),TEST_POST_CONTENT_BINARY_DATA);
        assertEquals(result.isPublished(), TEST_PUBLISHED_POST_DETAIL_READ_MODEL.isPublished());
        assertEquals(result.publishedAt(), TEST_PUBLISHED_POST_DETAIL_READ_MODEL.publishedAt());
    }

    @Test
    @DisplayName("toPostSummaryResponse PostSummary응답 반환하기")
    void testToPostSummaryResponse_givenPostSummaryModelAndContent_willReturnPostDetailResponse() {
        // when
        PostSummaryResponse result = postMapper.toPostSummaryResponse(TEST_POST_SUMMARY_READ_MODEL,TEST_POST_CONTENT_BINARY_DATA);

        // then
        assertEquals(result.ulid(),TEST_POST_SUMMARY_READ_MODEL.ulid());
        assertEquals(result.primaryCategory(),TEST_POST_SUMMARY_READ_MODEL.primaryCategory());
        assertEquals(result.secondaryCategory(),TEST_POST_SUMMARY_READ_MODEL.secondaryCategory());
        assertEquals(result.nickname(),TEST_POST_SUMMARY_READ_MODEL.nickname());
        assertEquals(result.title(),TEST_POST_SUMMARY_READ_MODEL.title());
        assertEquals(result.content(),TEST_POST_CONTENT_BINARY_DATA);
        assertEquals(result.publishedAt(),TEST_POST_SUMMARY_READ_MODEL.publishedAt());

    }


}