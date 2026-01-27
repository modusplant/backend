package kr.modusplant.domains.post.framework.out.jooq.repository;

import kr.modusplant.domains.post.domain.exception.EmptyCategoryIdException;
import kr.modusplant.domains.post.domain.exception.PostNotFoundException;
import kr.modusplant.domains.post.domain.vo.PostId;
import kr.modusplant.domains.post.framework.out.jooq.mapper.supers.PostJooqMapper;
import kr.modusplant.domains.post.usecase.port.repository.PostQueryRepository;
import kr.modusplant.domains.post.usecase.record.PostDetailDataReadModel;
import kr.modusplant.domains.post.usecase.record.PostDetailReadModel;
import kr.modusplant.domains.post.usecase.record.PostSummaryReadModel;
import kr.modusplant.framework.jooq.converter.JsonbJsonNodeConverter;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Name;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kr.modusplant.jooq.Tables.*;
import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class PostQueryJooqRepository implements PostQueryRepository {

    private final DSLContext dsl;
    private final PostJooqMapper postJooqMapper;
    private static final JsonbJsonNodeConverter JSON_CONVERTER = new JsonbJsonNodeConverter();

    public List<PostSummaryReadModel> findByCategoryWithCursor(Integer primaryCategoryId, List<Integer> secondaryCategoryIds, UUID currentMemberUuid, String cursorUlid, int size) {
        return dsl
                .select(
                        COMM_POST.ULID,
                        COMM_PRI_CATE.CATEGORY.as("primaryCategory"),
                        COMM_SECO_CATE.CATEGORY.as("secondaryCategory"),
                        SITE_MEMBER.NICKNAME,
                        COMM_POST.TITLE,
                        COMM_POST.CONTENT.convert(JSON_CONVERTER).as("content"),
                        COMM_POST.LIKE_COUNT,
                        COMM_POST.PUBLISHED_AT,
                        coalesce(field("cc.comment_count",Integer.class), 0).as("commentCount"),
                        exists(
                                selectOne().from(COMM_POST_LIKE)
                                        .where(COMM_POST_LIKE.POST_ULID.eq(COMM_POST.ULID))
                                        .and(COMM_POST_LIKE.MEMB_UUID.eq(currentMemberUuid))
                        ).as("isLiked"),
                        exists(
                                selectOne().from(COMM_POST_BOOKMARK)
                                        .where(COMM_POST_BOOKMARK.POST_ULID.eq(COMM_POST.ULID))
                                        .and(COMM_POST_BOOKMARK.MEMB_UUID.eq(currentMemberUuid))
                        ).as("isBookmarked")
                )
                .from(COMM_POST)
                .join(COMM_PRI_CATE).on(COMM_POST.PRI_CATE_ID.eq(COMM_PRI_CATE.ID))
                .join(COMM_SECO_CATE).on(COMM_POST.SECO_CATE_ID.eq(COMM_SECO_CATE.ID))
                .join(SITE_MEMBER).on(COMM_POST.AUTH_MEMB_UUID.eq(SITE_MEMBER.UUID))
                .leftJoin(
                        select(COMM_COMMENT.POST_ULID, count().as("comment_count"))
                                .from(COMM_COMMENT)
                                .where(COMM_COMMENT.IS_DELETED.isFalse())
                                .groupBy(COMM_COMMENT.POST_ULID)
                                .asTable("cc")
                ).on(COMM_POST.ULID.eq(field("cc.post_ulid",String.class)))
                .where(COMM_POST.IS_PUBLISHED.isTrue())
                .and(buildCategoryConditions(primaryCategoryId, secondaryCategoryIds))
                .and(buildCursorCondition(cursorUlid))
                .orderBy(COMM_POST.PUBLISHED_AT.desc(), COMM_POST.ULID.desc())
                .limit(size+1)
                .fetch()
                .map(postJooqMapper::toPostSummaryReadModel);
    }

    public List<PostSummaryReadModel> findByKeywordWithCursor(String keyword, UUID currentMemberUuid, String cursorUlid, int size) {
        return dsl
                .select(
                        COMM_POST.ULID,
                        COMM_PRI_CATE.CATEGORY.as("primaryCategory"),
                        COMM_SECO_CATE.CATEGORY.as("secondaryCategory"),
                        SITE_MEMBER.NICKNAME,
                        COMM_POST.TITLE,
                        COMM_POST.CONTENT.convert(JSON_CONVERTER).as("content"),
                        COMM_POST.LIKE_COUNT,
                        COMM_POST.PUBLISHED_AT,
                        coalesce(field("cc.comment_count",Integer.class), 0).as("commentCount"),
                        exists(
                                selectOne().from(COMM_POST_LIKE)
                                        .where(COMM_POST_LIKE.POST_ULID.eq(COMM_POST.ULID))
                                        .and(COMM_POST_LIKE.MEMB_UUID.eq(currentMemberUuid))
                        ).as("isLiked"),
                        exists(
                                selectOne().from(COMM_POST_BOOKMARK)
                                        .where(COMM_POST_BOOKMARK.POST_ULID.eq(COMM_POST.ULID))
                                        .and(COMM_POST_BOOKMARK.MEMB_UUID.eq(currentMemberUuid))
                        ).as("isBookmarked")
                )
                .from(COMM_POST)
                .join(COMM_PRI_CATE).on(COMM_POST.PRI_CATE_ID.eq(COMM_PRI_CATE.ID))
                .join(COMM_SECO_CATE).on(COMM_POST.SECO_CATE_ID.eq(COMM_SECO_CATE.ID))
                .join(SITE_MEMBER).on(COMM_POST.AUTH_MEMB_UUID.eq(SITE_MEMBER.UUID))
                .leftJoin(
                        select(COMM_COMMENT.POST_ULID, count().as("comment_count"))
                                .from(COMM_COMMENT)
                                .where(COMM_COMMENT.IS_DELETED.isFalse())
                                .groupBy(COMM_COMMENT.POST_ULID)
                                .asTable("cc")
                ).on(COMM_POST.ULID.eq(field("cc.post_ulid",String.class)))
                .where(COMM_POST.IS_PUBLISHED.isTrue())
                .and(buildKeywordCondition(keyword))
                .and(buildCursorCondition(cursorUlid))
                .orderBy(COMM_POST.PUBLISHED_AT.desc(), COMM_POST.ULID.desc())
                .limit(size+1)
                .fetch()
                .map(postJooqMapper::toPostSummaryReadModel);
    }

    public Optional<PostDetailReadModel> findPostDetailByPostId(PostId postId, UUID currentMemberUuid) {
        return Optional.ofNullable(dsl
                .select(
                        COMM_POST.ULID,
                        COMM_PRI_CATE.ID.as("primaryCategoryId"),
                        COMM_PRI_CATE.CATEGORY.as("primaryCategory"),
                        COMM_SECO_CATE.ID.as("secondaryCategoryId"),
                        COMM_SECO_CATE.CATEGORY.as("secondaryCategory"),
                        SITE_MEMBER.UUID.as("authorUuid"),
                        SITE_MEMBER.NICKNAME,
                        SITE_MEMBER_PROF.IMAGE_PATH,
                        COMM_POST.TITLE,
                        COMM_POST.CONTENT.convert(JSON_CONVERTER).as("content"),
                        COMM_POST.LIKE_COUNT,
                        COMM_POST.IS_PUBLISHED,
                        COMM_POST.PUBLISHED_AT,
                        COMM_POST.UPDATED_AT,
                        exists(
                                selectOne().from(COMM_POST_LIKE)
                                        .where(COMM_POST_LIKE.POST_ULID.eq(COMM_POST.ULID))
                                        .and(COMM_POST_LIKE.MEMB_UUID.eq(currentMemberUuid))
                        ).as("isLiked"),
                        exists(
                                selectOne().from(COMM_POST_BOOKMARK)
                                        .where(COMM_POST_BOOKMARK.POST_ULID.eq(COMM_POST.ULID))
                                        .and(COMM_POST_BOOKMARK.MEMB_UUID.eq(currentMemberUuid))
                        ).as("isBookmarked")
                ).from(COMM_POST)
                .join(COMM_PRI_CATE).on(COMM_POST.PRI_CATE_ID.eq(COMM_PRI_CATE.ID))
                .join(COMM_SECO_CATE).on(COMM_POST.SECO_CATE_ID.eq(COMM_SECO_CATE.ID))
                .join(SITE_MEMBER).on(COMM_POST.AUTH_MEMB_UUID.eq(SITE_MEMBER.UUID))
                .leftJoin(SITE_MEMBER_PROF).on(SITE_MEMBER.UUID.eq(SITE_MEMBER_PROF.UUID))
                .where(COMM_POST.ULID.eq(postId.getValue()))
                .fetchOne()
        ).map(postJooqMapper::toPostDetailReadModel);
    }

    public Optional<PostDetailDataReadModel> findPostDetailDataByPostId(PostId postId) {
        return Optional.ofNullable(dsl
                .select(
                        COMM_POST.ULID,
                        COMM_PRI_CATE.ID.as("primaryCategoryId"),
                        COMM_PRI_CATE.CATEGORY.as("primaryCategory"),
                        COMM_SECO_CATE.ID.as("secondaryCategoryId"),
                        COMM_SECO_CATE.CATEGORY.as("secondaryCategory"),
                        SITE_MEMBER.UUID.as("authorUuid"),
                        SITE_MEMBER.NICKNAME,
                        COMM_POST.TITLE,
                        COMM_POST.CONTENT.convert(JSON_CONVERTER).as("content"),
                        COMM_POST.IS_PUBLISHED,
                        COMM_POST.PUBLISHED_AT,
                        COMM_POST.UPDATED_AT
                ).from(COMM_POST)
                .join(COMM_PRI_CATE).on(COMM_POST.PRI_CATE_ID.eq(COMM_PRI_CATE.ID))
                .join(COMM_SECO_CATE).on(COMM_POST.SECO_CATE_ID.eq(COMM_SECO_CATE.ID))
                .join(SITE_MEMBER).on(COMM_POST.AUTH_MEMB_UUID.eq(SITE_MEMBER.UUID))
                .where(COMM_POST.ULID.eq(postId.getValue()))
                .fetchOne()
        ).map(postJooqMapper::toPostDetailDataReadModel);
    }

    private Condition buildCategoryConditions(Integer primaryCategoryId, List<Integer> secondaryCategoryIds) {
        if (primaryCategoryId == null && secondaryCategoryIds != null && !secondaryCategoryIds.isEmpty()) {
            throw new EmptyCategoryIdException();
        }
        Condition condition = noCondition();
        if(primaryCategoryId != null) {
            condition = condition.and(COMM_POST.PRI_CATE_ID.eq(primaryCategoryId));
        }
        if (primaryCategoryId != null && secondaryCategoryIds != null && !secondaryCategoryIds.isEmpty()) {
            condition = condition.and(COMM_POST.SECO_CATE_ID.in(secondaryCategoryIds));
        }
        return condition;
    }

    private Condition buildKeywordCondition(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return noCondition();
        }
        String searchKeyword = "%"+escapeWildcards(keyword)+"%";
        Condition titleCondition = COMM_POST.TITLE.likeIgnoreCase(searchKeyword);
        Name alias = name("c");
        Condition contentCondition = exists(
                selectOne()
                        .from(table("jsonb_array_elements({0})", COMM_POST.CONTENT).as(alias))
                        .where(
                                field("{0}->>'type'", String.class, field(alias)).eq(val("text"))
                                        .and(field("{0}->>'data'", String.class, field(alias)).likeIgnoreCase(val(searchKeyword)))
                        )
        );
        return titleCondition.or(contentCondition);
    }

    private String escapeWildcards(String input) {
        return input
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    private Condition buildCursorCondition(String cursorUlid) {
        if (cursorUlid == null) {
            return noCondition();
        }
        LocalDateTime cursorPublishedAt = dsl.select(COMM_POST.PUBLISHED_AT)
                .from(COMM_POST)
                .where(COMM_POST.ULID.eq(cursorUlid))
                .fetchOne(COMM_POST.PUBLISHED_AT);

        if (cursorPublishedAt == null) {
            cursorPublishedAt = dsl.select(COMM_POST_ARCHIVE.PUBLISHED_AT)
                    .from(COMM_POST_ARCHIVE)
                    .where(COMM_POST_ARCHIVE.ULID.eq(cursorUlid))
                    .fetchOne(COMM_POST_ARCHIVE.PUBLISHED_AT);
            if(cursorPublishedAt == null) {
                throw new PostNotFoundException();
            }
        }
        return row(COMM_POST.PUBLISHED_AT, COMM_POST.ULID).lessThan(cursorPublishedAt,cursorUlid);
    }
}
