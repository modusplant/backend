package kr.modusplant.domains.post.adapter.mapper;

import kr.modusplant.domains.post.common.util.usecase.model.PostReadModelTestUtils;
import kr.modusplant.domains.post.usecase.port.mapper.PostMapper;
import kr.modusplant.domains.post.usecase.response.DraftPostResponse;
import kr.modusplant.domains.post.usecase.response.PostDetailResponse;
import kr.modusplant.domains.post.usecase.response.PostSummaryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kr.modusplant.domains.post.common.constant.PostJsonNodeConstant.*;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberProfileConstant.MEMBER_PROFILE_BASIC_USER_IMAGE_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PostMapperImplTest implements PostReadModelTestUtils {
    private final PostMapper postMapper = new PostMapperImpl();

    @Test
    @DisplayName("toPostDetailResponse로 PostDetailResponse 반환하기")
    void testPostDetailReadNodelToPostDetailResponse_givenPostDetailModelAndContentAndViewCount_willReturnPostDetailResponse() {
        // given
        long viewCount = 1L;

        // when
        PostDetailResponse result = postMapper.postDetailReadModelToPostDetailResponse(TEST_PUBLISHED_POST_DETAIL_READ_MODEL, MEMBER_PROFILE_BASIC_USER_IMAGE_URL ,TEST_POST_CONTENT,viewCount);

        // then
        assertEquals(result.ulid(), TEST_PUBLISHED_POST_DETAIL_READ_MODEL.ulid());
        assertEquals(result.primaryCategoryId(), TEST_PUBLISHED_POST_DETAIL_READ_MODEL.primaryCategoryId());
        assertEquals(result.primaryCategory(), TEST_PUBLISHED_POST_DETAIL_READ_MODEL.primaryCategory());
        assertEquals(result.secondaryCategoryId(), TEST_PUBLISHED_POST_DETAIL_READ_MODEL.secondaryCategoryId());
        assertEquals(result.secondaryCategory(), TEST_PUBLISHED_POST_DETAIL_READ_MODEL.secondaryCategory());
        assertEquals(result.authorUuid(), TEST_PUBLISHED_POST_DETAIL_READ_MODEL.authorUuid());
        assertEquals(result.nickname(), TEST_PUBLISHED_POST_DETAIL_READ_MODEL.nickname());
        assertEquals(result.authorImageUrl(), MEMBER_PROFILE_BASIC_USER_IMAGE_URL);
        assertEquals(result.likeCount(), TEST_PUBLISHED_POST_DETAIL_READ_MODEL.likeCount());
        assertEquals(result.viewCount(),viewCount);
        assertEquals(result.title(), TEST_PUBLISHED_POST_DETAIL_READ_MODEL.title());
        assertEquals(result.content(),TEST_POST_CONTENT);
        assertEquals(result.thumbnailFilename(), null);
        assertEquals(result.isPublished(), TEST_PUBLISHED_POST_DETAIL_READ_MODEL.isPublished());
        assertEquals(result.publishedAt(), TEST_PUBLISHED_POST_DETAIL_READ_MODEL.publishedAt());
        assertEquals(result.updatedAt(), TEST_PUBLISHED_POST_DETAIL_READ_MODEL.updatedAt());
        assertEquals(result.isLiked(), TEST_PUBLISHED_POST_DETAIL_READ_MODEL.isLiked());
        assertEquals(result.isBookmarked(), TEST_PUBLISHED_POST_DETAIL_READ_MODEL.isBookmarked());
    }

    @Test
    @DisplayName("toPostDetailResponse로 PostDetailResponse 반환하기")
    void testPostDetailDataReadModelToPostDetailResponse_givenPostDetailModelAndContentAndViewCount_willReturnPostDetailDataResponse() {
        // when
        PostDetailResponse result = postMapper.postDetailDataReadModelToPostDetailResponse(TEST_PUBLISHED_POST_DETAIL_DATA_READ_MODEL ,TEST_POST_CONTENT, TEST_POST_CONTENT_THUMBNAIL_FILENAME);

        // then
        assertEquals(result.ulid(), TEST_PUBLISHED_POST_DETAIL_DATA_READ_MODEL.ulid());
        assertEquals(result.primaryCategoryId(), TEST_PUBLISHED_POST_DETAIL_DATA_READ_MODEL.primaryCategoryId());
        assertEquals(result.primaryCategory(), TEST_PUBLISHED_POST_DETAIL_DATA_READ_MODEL.primaryCategory());
        assertEquals(result.secondaryCategoryId(), TEST_PUBLISHED_POST_DETAIL_DATA_READ_MODEL.secondaryCategoryId());
        assertEquals(result.secondaryCategory(), TEST_PUBLISHED_POST_DETAIL_DATA_READ_MODEL.secondaryCategory());
        assertEquals(result.authorUuid(), TEST_PUBLISHED_POST_DETAIL_DATA_READ_MODEL.authorUuid());
        assertEquals(result.nickname(), TEST_PUBLISHED_POST_DETAIL_DATA_READ_MODEL.nickname());
        assertEquals(result.authorImageUrl(), null);
        assertEquals(result.likeCount(), null);
        assertEquals(result.viewCount(),null);
        assertEquals(result.title(), TEST_PUBLISHED_POST_DETAIL_DATA_READ_MODEL.title());
        assertEquals(result.content(),TEST_POST_CONTENT);
        assertEquals(result.thumbnailFilename(), TEST_POST_CONTENT_THUMBNAIL_FILENAME);
        assertEquals(result.isPublished(), TEST_PUBLISHED_POST_DETAIL_DATA_READ_MODEL.isPublished());
        assertEquals(result.publishedAt(), TEST_PUBLISHED_POST_DETAIL_DATA_READ_MODEL.publishedAt());
        assertEquals(result.updatedAt(), TEST_PUBLISHED_POST_DETAIL_DATA_READ_MODEL.updatedAt());
        assertEquals(result.isLiked(), null);
        assertEquals(result.isBookmarked(),null);
    }


    @Test
    @DisplayName("toPostSummaryResponse PostSummaryResponse 반환하기")
    void testToPostSummaryResponse_givenPostSummaryModelAndContent_willReturnPostDetailResponse() {
        // when
        PostSummaryResponse result = postMapper.toPostSummaryResponse(TEST_POST_SUMMARY_READ_MODEL,TEST_POST_CONTENT);

        // then
        assertEquals(result.ulid(),TEST_POST_SUMMARY_READ_MODEL.ulid());
        assertEquals(result.primaryCategory(),TEST_POST_SUMMARY_READ_MODEL.primaryCategory());
        assertEquals(result.secondaryCategory(),TEST_POST_SUMMARY_READ_MODEL.secondaryCategory());
        assertEquals(result.nickname(),TEST_POST_SUMMARY_READ_MODEL.nickname());
        assertEquals(result.title(),TEST_POST_SUMMARY_READ_MODEL.title());
        assertEquals(result.content(),TEST_POST_CONTENT);
        assertEquals(result.publishedAt(),TEST_POST_SUMMARY_READ_MODEL.publishedAt());

    }

    @Test
    @DisplayName("toDraftPostResponse로 DraftPostResponse 반환하기")
    void testToDraftPostResponse_givenDraftPostReadModelAndContent_willReturnDraftPostResponse() {
        // when
        DraftPostResponse result = postMapper.toDraftPostResponse(TEST_DRAFT_POST_READ_MODEL,TEST_POST_CONTENT);

        // then
        assertEquals(result.ulid(),TEST_DRAFT_POST_READ_MODEL.ulid());
        assertEquals(result.primaryCategory(),TEST_DRAFT_POST_READ_MODEL.primaryCategory());
        assertEquals(result.secondaryCategory(),TEST_DRAFT_POST_READ_MODEL.secondaryCategory());
        assertEquals(result.title(),TEST_DRAFT_POST_READ_MODEL.title());
        assertEquals(result.content(),TEST_POST_CONTENT);
        assertEquals(result.updatedAt(), TEST_DRAFT_POST_READ_MODEL.updatedAt());
    }


}