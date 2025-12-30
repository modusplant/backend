package kr.modusplant.domains.post.framework.out.jooq.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.domains.post.common.util.framework.out.jpa.entity.PostEntityTestUtils;
import kr.modusplant.domains.post.framework.out.jooq.mapper.supers.PostJooqMapper;
import kr.modusplant.domains.post.usecase.record.DraftPostReadModel;
import kr.modusplant.domains.post.usecase.record.PostDetailReadModel;
import kr.modusplant.domains.post.usecase.record.PostSummaryReadModel;
import kr.modusplant.framework.jpa.entity.CommPostEntity;
import org.jooq.Record;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.jooq.Tables.COMM_POST;
import static kr.modusplant.jooq.Tables.SITE_MEMBER;
import static kr.modusplant.shared.persistence.common.util.constant.CommPrimaryCategoryConstant.TEST_COMM_PRIMARY_CATEGORY_CATEGORY;
import static kr.modusplant.shared.persistence.common.util.constant.CommPrimaryCategoryConstant.TEST_COMM_PRIMARY_CATEGORY_ID;
import static kr.modusplant.shared.persistence.common.util.constant.CommSecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORY_CATEGORY;
import static kr.modusplant.shared.persistence.common.util.constant.CommSecondaryCategoryConstant.TEST_COMM_SECONDARY_CATEGORY_ID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_NICKNAME;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class PostJooqMapperImplTest implements PostEntityTestUtils {
    private final PostJooqMapper postJooqMapper = new PostJooqMapperImpl();

    @Test
    @DisplayName("toPostSummaryReadModel로 PostSummaryReadModel 반환하기")
    void testToPostSummaryReadModel_givenRecord_willReturnPostSummaryReadModel() {
        // given
        Record record = mock(Record.class);
        String primaryCategory = TEST_COMM_PRIMARY_CATEGORY_CATEGORY;
        String secondaryCategory = TEST_COMM_SECONDARY_CATEGORY_CATEGORY;
        String nickname = MEMBER_BASIC_USER_NICKNAME ;
        LocalDateTime publishedAt = LocalDateTime.now();
        Integer commentCount = 5;
        Boolean isLiked = true;
        Boolean isBookmarked = false;
        CommPostEntity postEntity = createPublishedPostEntityBuilderWithUuid().build();

        // when
        given(record.get(COMM_POST.ULID)).willReturn(postEntity.getUlid());
        given(record.get("primaryCategory", String.class)).willReturn(primaryCategory);
        given(record.get("secondaryCategory", String.class)).willReturn(secondaryCategory);
        given(record.get(SITE_MEMBER.NICKNAME)).willReturn(nickname);
        given(record.get(COMM_POST.TITLE)).willReturn(postEntity.getTitle());
        given(record.get("content", JsonNode.class)).willReturn(postEntity.getContent());
        given(record.get(COMM_POST.LIKE_COUNT)).willReturn(postEntity.getLikeCount());
        given(record.get(COMM_POST.PUBLISHED_AT)).willReturn(publishedAt);
        given(record.get("commentCount", Integer.class)).willReturn(commentCount);
        given(record.get("isLiked", Boolean.class)).willReturn(isLiked);
        given(record.get("isBookmarked", Boolean.class)).willReturn(isBookmarked);

        PostSummaryReadModel result = postJooqMapper.toPostSummaryReadModel(record);

        // then
        assertThat(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("ulid", postEntity.getUlid())
                .hasFieldOrPropertyWithValue("primaryCategory", primaryCategory)
                .hasFieldOrPropertyWithValue("secondaryCategory", secondaryCategory)
                .hasFieldOrPropertyWithValue("nickname", nickname)
                .hasFieldOrPropertyWithValue("title", postEntity.getTitle())
                .hasFieldOrPropertyWithValue("content", postEntity.getContent())
                .hasFieldOrPropertyWithValue("likeCount", postEntity.getLikeCount())
                .hasFieldOrPropertyWithValue("publishedAt", publishedAt)
                .hasFieldOrPropertyWithValue("commentCount", commentCount)
                .hasFieldOrPropertyWithValue("isLiked", isLiked)
                .hasFieldOrPropertyWithValue("isBookmarked", isBookmarked);
    }

    @Test
    @DisplayName("toPostSummaryReadModel로 PostSummaryReadModel 반환하기 (publishedAt이 null인 경우)")
    void testToPostSummaryReadModel_givenRecordWithNullPublishedAt_willReturnPostSummaryReadModel() {
        // given
        Record record = mock(Record.class);
        String primaryCategory = TEST_COMM_PRIMARY_CATEGORY_CATEGORY;
        String secondaryCategory = TEST_COMM_SECONDARY_CATEGORY_CATEGORY;
        String nickname = MEMBER_BASIC_USER_NICKNAME ;
        Integer commentCount = 0;
        Boolean isLiked = false;
        Boolean isBookmarked = false;
        CommPostEntity postEntity = createPublishedPostEntityBuilderWithUuid().build();

        // when
        given(record.get(COMM_POST.ULID)).willReturn(postEntity.getUlid());
        given(record.get("primaryCategory", String.class)).willReturn(primaryCategory);
        given(record.get("secondaryCategory", String.class)).willReturn(secondaryCategory);
        given(record.get(SITE_MEMBER.NICKNAME)).willReturn(nickname);
        given(record.get(COMM_POST.TITLE)).willReturn(postEntity.getTitle());
        given(record.get("content", JsonNode.class)).willReturn(postEntity.getContent());
        given(record.get(COMM_POST.LIKE_COUNT)).willReturn(postEntity.getLikeCount());
        given(record.get(COMM_POST.PUBLISHED_AT)).willReturn(null);
        given(record.get("commentCount", Integer.class)).willReturn(commentCount);
        given(record.get("isLiked", Boolean.class)).willReturn(isLiked);
        given(record.get("isBookmarked", Boolean.class)).willReturn(isBookmarked);

        PostSummaryReadModel result = postJooqMapper.toPostSummaryReadModel(record);

        // then
        assertThat(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("ulid", postEntity.getUlid())
                .hasFieldOrPropertyWithValue("primaryCategory", primaryCategory)
                .hasFieldOrPropertyWithValue("secondaryCategory", secondaryCategory)
                .hasFieldOrPropertyWithValue("nickname", nickname)
                .hasFieldOrPropertyWithValue("title", postEntity.getTitle())
                .hasFieldOrPropertyWithValue("content", postEntity.getContent())
                .hasFieldOrPropertyWithValue("likeCount", postEntity.getLikeCount())
                .hasFieldOrPropertyWithValue("publishedAt", null)
                .hasFieldOrPropertyWithValue("commentCount", commentCount)
                .hasFieldOrPropertyWithValue("isLiked", isLiked)
                .hasFieldOrPropertyWithValue("isBookmarked", isBookmarked);
    }

    @Test
    @DisplayName("toPostDetailReadModel로 PostDetailReadModel 반환하기")
    void testToPostDetailReadModel_givenRecord_willReturnPostDetailReadModel() {
        // given
        Record record = mock(Record.class);
        Integer primaryCategoryId = TEST_COMM_PRIMARY_CATEGORY_ID;
        String primaryCategory = TEST_COMM_PRIMARY_CATEGORY_CATEGORY;
        Integer secondaryCategoryId = TEST_COMM_SECONDARY_CATEGORY_ID;
        String secondaryCategory = TEST_COMM_SECONDARY_CATEGORY_CATEGORY;
        UUID authorUuid = MEMBER_BASIC_USER_UUID;
        String nickname = MEMBER_BASIC_USER_NICKNAME;
        LocalDateTime publishedAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        Boolean isLiked = true;
        Boolean isBookmarked = false;
        CommPostEntity postEntity = createPublishedPostEntityBuilderWithUuid().build();

        // when
        given(record.get(COMM_POST.ULID)).willReturn(postEntity.getUlid());
        given(record.get("primaryCategoryId", Integer.class)).willReturn(primaryCategoryId);
        given(record.get("primaryCategory", String.class)).willReturn(primaryCategory);
        given(record.get("secondaryCategoryId", Integer.class)).willReturn(secondaryCategoryId);
        given(record.get("secondaryCategory", String.class)).willReturn(secondaryCategory);
        given(record.get("authorUuid", UUID.class)).willReturn(authorUuid);
        given(record.get(SITE_MEMBER.NICKNAME)).willReturn(nickname);
        given(record.get(COMM_POST.TITLE)).willReturn(postEntity.getTitle());
        given(record.get("content", JsonNode.class)).willReturn(postEntity.getContent());
        given(record.get(COMM_POST.LIKE_COUNT)).willReturn(postEntity.getLikeCount());
        given(record.get(COMM_POST.IS_PUBLISHED)).willReturn(postEntity.getIsPublished());
        given(record.get(COMM_POST.PUBLISHED_AT)).willReturn(publishedAt);
        given(record.get(COMM_POST.UPDATED_AT)).willReturn(updatedAt);
        given(record.get("isLiked", Boolean.class)).willReturn(isLiked);
        given(record.get("isBookmarked", Boolean.class)).willReturn(isBookmarked);

        PostDetailReadModel result = postJooqMapper.toPostDetailReadModel(record);

        // then
        assertThat(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("ulid", postEntity.getUlid())
                .hasFieldOrPropertyWithValue("primaryCategoryId", primaryCategoryId)
                .hasFieldOrPropertyWithValue("primaryCategory", primaryCategory)
                .hasFieldOrPropertyWithValue("secondaryCategoryId", secondaryCategoryId)
                .hasFieldOrPropertyWithValue("secondaryCategory", secondaryCategory)
                .hasFieldOrPropertyWithValue("authorUuid", authorUuid)
                .hasFieldOrPropertyWithValue("nickname", nickname)
                .hasFieldOrPropertyWithValue("title", postEntity.getTitle())
                .hasFieldOrPropertyWithValue("content", postEntity.getContent())
                .hasFieldOrPropertyWithValue("likeCount", postEntity.getLikeCount())
                .hasFieldOrPropertyWithValue("isPublished", postEntity.getIsPublished())
                .hasFieldOrPropertyWithValue("publishedAt", publishedAt)
                .hasFieldOrPropertyWithValue("updatedAt", updatedAt)
                .hasFieldOrPropertyWithValue("isLiked", isLiked)
                .hasFieldOrPropertyWithValue("isBookmarked", isBookmarked);
    }

    @Test
    @DisplayName("toPostDetailReadModel로 PostDetailReadModel 반환하기 (publishedAt이 null인 경우)")
    void testToPostDetailReadModel_givenRecordWithNullPublishedAt_willReturnPostDetailReadModel() {
        // given
        Record record = mock(Record.class);
        Integer primaryCategoryId = TEST_COMM_PRIMARY_CATEGORY_ID;
        String primaryCategory = TEST_COMM_PRIMARY_CATEGORY_CATEGORY;
        Integer secondaryCategoryId = TEST_COMM_SECONDARY_CATEGORY_ID;
        String secondaryCategory = TEST_COMM_SECONDARY_CATEGORY_CATEGORY;
        UUID authorUuid = MEMBER_BASIC_USER_UUID;
        String nickname = MEMBER_BASIC_USER_NICKNAME;
        LocalDateTime updatedAt = LocalDateTime.now();
        Boolean isLiked = false;
        Boolean isBookmarked = false;
        CommPostEntity postEntity = createDraftPostEntityBuilderWithUuid().build();

        // when
        given(record.get(COMM_POST.ULID)).willReturn(postEntity.getUlid());
        given(record.get("primaryCategoryId", Integer.class)).willReturn(primaryCategoryId);
        given(record.get("primaryCategory", String.class)).willReturn(primaryCategory);
        given(record.get("secondaryCategoryId", Integer.class)).willReturn(secondaryCategoryId);
        given(record.get("secondaryCategory", String.class)).willReturn(secondaryCategory);
        given(record.get("authorUuid", UUID.class)).willReturn(authorUuid);
        given(record.get(SITE_MEMBER.NICKNAME)).willReturn(nickname);
        given(record.get(COMM_POST.TITLE)).willReturn(postEntity.getTitle());
        given(record.get("content", JsonNode.class)).willReturn(postEntity.getContent());
        given(record.get(COMM_POST.LIKE_COUNT)).willReturn(postEntity.getLikeCount());
        given(record.get(COMM_POST.IS_PUBLISHED)).willReturn(false);
        given(record.get(COMM_POST.PUBLISHED_AT)).willReturn(null);
        given(record.get(COMM_POST.UPDATED_AT)).willReturn(updatedAt);
        given(record.get("isLiked", Boolean.class)).willReturn(isLiked);
        given(record.get("isBookmarked", Boolean.class)).willReturn(isBookmarked);

        PostDetailReadModel result = postJooqMapper.toPostDetailReadModel(record);

        // then
        assertThat(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("ulid", postEntity.getUlid())
                .hasFieldOrPropertyWithValue("primaryCategoryId", primaryCategoryId)
                .hasFieldOrPropertyWithValue("primaryCategory", primaryCategory)
                .hasFieldOrPropertyWithValue("secondaryCategoryId", secondaryCategoryId)
                .hasFieldOrPropertyWithValue("secondaryCategory", secondaryCategory)
                .hasFieldOrPropertyWithValue("authorUuid", authorUuid)
                .hasFieldOrPropertyWithValue("nickname", nickname)
                .hasFieldOrPropertyWithValue("title", postEntity.getTitle())
                .hasFieldOrPropertyWithValue("content", postEntity.getContent())
                .hasFieldOrPropertyWithValue("likeCount", postEntity.getLikeCount())
                .hasFieldOrPropertyWithValue("isPublished", false)
                .hasFieldOrPropertyWithValue("publishedAt", null)
                .hasFieldOrPropertyWithValue("updatedAt", updatedAt)
                .hasFieldOrPropertyWithValue("isLiked", isLiked)
                .hasFieldOrPropertyWithValue("isBookmarked", isBookmarked);
    }




    @Test
    @DisplayName("toDraftPostReadModel로 DraftPostReadModel 반환하기")
    void testToDraftPostReadModel_givenRecord_willReturnDraftPostReadModel() {
        // given
        Record record = mock(Record.class);
        String primaryCategory = TEST_COMM_PRIMARY_CATEGORY_CATEGORY;
        String secondaryCategory = TEST_COMM_SECONDARY_CATEGORY_CATEGORY;
        LocalDateTime updatedAt = LocalDateTime.now();
        CommPostEntity postEntity = createDraftPostEntityBuilderWithUuid().build();

        // when
        given(record.get(COMM_POST.ULID)).willReturn(postEntity.getUlid());
        given(record.get("primaryCategory", String.class)).willReturn(primaryCategory);
        given(record.get("secondaryCategory", String.class)).willReturn(secondaryCategory);
        given(record.get(COMM_POST.TITLE)).willReturn(postEntity.getTitle());
        given(record.get("content", JsonNode.class)).willReturn(postEntity.getContent());
        given(record.get(COMM_POST.UPDATED_AT)).willReturn(updatedAt);

        DraftPostReadModel result = postJooqMapper.toDraftPostReadModel(record);

        // then
        assertThat(result)
                .isNotNull()
                .hasFieldOrPropertyWithValue("ulid", postEntity.getUlid())
                .hasFieldOrPropertyWithValue("primaryCategory", primaryCategory)
                .hasFieldOrPropertyWithValue("secondaryCategory", secondaryCategory)
                .hasFieldOrPropertyWithValue("title", postEntity.getTitle())
                .hasFieldOrPropertyWithValue("content", postEntity.getContent())
                .hasFieldOrPropertyWithValue("updatedAt", updatedAt);
    }

}