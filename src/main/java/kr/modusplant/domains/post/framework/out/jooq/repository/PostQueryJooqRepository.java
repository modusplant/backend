package kr.modusplant.domains.post.framework.out.jooq.repository;

import kr.modusplant.domains.post.domain.exception.EmptyCategoryIdException;
import kr.modusplant.domains.post.domain.vo.PostId;
import kr.modusplant.domains.post.framework.out.jooq.mapper.supers.PostJooqMapper;
import kr.modusplant.domains.post.usecase.record.PostDetailReadModel;
import kr.modusplant.domains.post.usecase.record.PostSummaryReadModel;
import kr.modusplant.domains.post.usecase.port.repository.PostQueryRepository;
import kr.modusplant.infrastructure.converter.JsonNodeConverter;
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
    private static final JsonNodeConverter JSON_CONVERTER = new JsonNodeConverter();

    public List<PostSummaryReadModel> findByCategoryWithCursor(UUID primaryCategoryUuid, List<UUID> secondaryCategoryUuids, UUID currentMemberUuid, String cursorUlid, int size) {
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
                .join(COMM_PRI_CATE).on(COMM_POST.PRI_CATE_UUID.eq(COMM_PRI_CATE.UUID))
                .join(COMM_SECO_CATE).on(COMM_POST.SECO_CATE_UUID.eq(COMM_SECO_CATE.UUID))
                .join(SITE_MEMBER).on(COMM_POST.AUTH_MEMB_UUID.eq(SITE_MEMBER.UUID))
                .leftJoin(
                        select(COMM_COMMENT.POST_ULID, count().as("comment_count"))
                                .from(COMM_COMMENT)
                                .where(COMM_COMMENT.IS_DELETED.isFalse())
                                .groupBy(COMM_COMMENT.POST_ULID)
                                .asTable("cc")
                ).on(COMM_POST.ULID.eq(field("cc.post_ulid",String.class)))
                .where(COMM_POST.IS_PUBLISHED.isTrue())
                .and(buildCategoryConditions(primaryCategoryUuid, secondaryCategoryUuids))
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
                .join(COMM_PRI_CATE).on(COMM_POST.PRI_CATE_UUID.eq(COMM_PRI_CATE.UUID))
                .join(COMM_SECO_CATE).on(COMM_POST.SECO_CATE_UUID.eq(COMM_SECO_CATE.UUID))
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
                        COMM_PRI_CATE.UUID.as("primaryCategoryUuid"),
                        COMM_PRI_CATE.CATEGORY.as("primaryCategory"),
                        COMM_SECO_CATE.UUID.as("secondaryCategoryUuid"),
                        COMM_SECO_CATE.CATEGORY.as("secondaryCategory"),
                        SITE_MEMBER.UUID.as("authorUuid"),
                        SITE_MEMBER.NICKNAME,
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
                .join(COMM_PRI_CATE).on(COMM_POST.PRI_CATE_UUID.eq(COMM_PRI_CATE.UUID))
                .join(COMM_SECO_CATE).on(COMM_POST.SECO_CATE_UUID.eq(COMM_SECO_CATE.UUID))
                .join(SITE_MEMBER).on(COMM_POST.AUTH_MEMB_UUID.eq(SITE_MEMBER.UUID))
                .where(COMM_POST.ULID.eq(postId.getValue()))
                .fetchOne()
        ).map(postJooqMapper::toPostDetailReadModel);
    }

    private Condition buildCategoryConditions(UUID primaryCategoryUuid, List<UUID> secondaryCategoryUuids) {
        if (primaryCategoryUuid == null && secondaryCategoryUuids != null && !secondaryCategoryUuids.isEmpty()) {
            throw new EmptyCategoryIdException();
        }
        Condition condition = noCondition();
        if(primaryCategoryUuid != null) {
            condition = condition.and(COMM_POST.PRI_CATE_UUID.eq(primaryCategoryUuid));
        }
        if (primaryCategoryUuid != null && secondaryCategoryUuids != null && !secondaryCategoryUuids.isEmpty()) {
            condition = condition.and(COMM_POST.SECO_CATE_UUID.in(secondaryCategoryUuids));
        }
        return condition;
    }

    private Condition buildKeywordCondition(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return noCondition();
        }
        String searchKeyword = "%"+keyword+"%";
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

    private Condition buildCursorCondition(String cursorUlid) {
        if (cursorUlid == null) {
            return noCondition();
        }
        LocalDateTime cursorPublishedAt = dsl.select(COMM_POST.PUBLISHED_AT)
                .from(COMM_POST)
                .where(COMM_POST.ULID.eq(cursorUlid))
                .fetchOne(COMM_POST.PUBLISHED_AT);

        if (cursorPublishedAt == null) {
            return noCondition();
        }
        return row(COMM_POST.PUBLISHED_AT, COMM_POST.ULID).lessThan(cursorPublishedAt,cursorUlid);
    }
}
