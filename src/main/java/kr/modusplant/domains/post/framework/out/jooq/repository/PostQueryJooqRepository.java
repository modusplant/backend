package kr.modusplant.domains.post.framework.out.jooq.repository;

import kr.modusplant.domains.post.domain.exception.EmptyValueException;
import kr.modusplant.domains.post.domain.exception.PostNotFoundException;
import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
import kr.modusplant.domains.post.domain.vo.PostId;
import kr.modusplant.domains.post.framework.out.jooq.mapper.supers.PostJooqMapper;
import kr.modusplant.domains.post.usecase.enums.SearchOption;
import kr.modusplant.domains.post.usecase.enums.SearchSort;
import kr.modusplant.domains.post.usecase.port.repository.PostQueryRepository;
import kr.modusplant.domains.post.usecase.record.PostDetailDataReadModel;
import kr.modusplant.domains.post.usecase.record.PostDetailReadModel;
import kr.modusplant.domains.post.usecase.record.PostSummaryReadModel;
import kr.modusplant.framework.jooq.converter.JsonbJsonNodeConverter;
import lombok.RequiredArgsConstructor;
import org.jooq.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static kr.modusplant.jooq.Tables.*;
import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class PostQueryJooqRepository implements PostQueryRepository {

    private final DSLContext dsl;
    private final PostJooqMapper postJooqMapper;
    private final JsonbJsonNodeConverter jsonConverter = new JsonbJsonNodeConverter();

    public List<PostSummaryReadModel> findByCategoryWithCursor(Integer primaryCategoryId, List<Integer> secondaryCategoryIds, UUID currentMemberUuid, String cursorUlid, int size) {
        return dsl
                .select(
                        COMM_POST.ULID,
                        COMM_PRI_CATE.CATEGORY.as("primaryCategory"),
                        COMM_SECO_CATE.CATEGORY.as("secondaryCategory"),
                        SITE_MEMBER.NICKNAME,
                        COMM_POST.TITLE,
                        COMM_POST.CONTENT.convert(jsonConverter).as("content"),
                        COMM_POST.THUMBNAIL_PATH,
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

    // TODO: 정확도순은 근사 BM25로 구현. 추후 BM25알고리즘 구현 시 Elasticsearch/OpenSearch 도입 가능
    public List<PostSummaryReadModel> findByKeywordWithCursor(
            SearchOption option, String keyword, SearchSort sort,
            Integer primaryCategoryId, List<Integer> secondaryCategoryIds,
            UUID currentMemberUuid, String cursorUlid, int size) {

        boolean isAccuracy = sort == SearchSort.RELEVANCE
                && keyword != null
                && !keyword.trim().isEmpty();

        return dsl
                .select(
                        COMM_POST.ULID,
                        COMM_PRI_CATE.CATEGORY.as("primaryCategory"),
                        COMM_SECO_CATE.CATEGORY.as("secondaryCategory"),
                        SITE_MEMBER.NICKNAME,
                        COMM_POST.TITLE,
                        COMM_POST.CONTENT.convert(jsonConverter).as("content"),
                        COMM_POST.THUMBNAIL_PATH,
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
                .and(buildKeywordCondition(keyword,option,isAccuracy))
                .and(buildCategoryConditions(primaryCategoryId, secondaryCategoryIds))
                .and(isAccuracy
                        ? buildAccuracyCursorCondition(cursorUlid, keyword, option)
                        : buildCursorCondition(cursorUlid))
                .orderBy(buildOrderBy(keyword,option,sort))
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
                        COMM_POST.CONTENT.convert(jsonConverter).as("content"),
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
                .and(COMM_POST.IS_PUBLISHED.isTrue())
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
                        COMM_POST.CONTENT.convert(jsonConverter).as("content"),
                        COMM_POST.THUMBNAIL_PATH,
                        COMM_POST.IS_PUBLISHED,
                        COMM_POST.PUBLISHED_AT,
                        COMM_POST.UPDATED_AT
                ).from(COMM_POST)
                .leftJoin(COMM_PRI_CATE).on(COMM_POST.PRI_CATE_ID.eq(COMM_PRI_CATE.ID))
                .leftJoin(COMM_SECO_CATE).on(COMM_POST.SECO_CATE_ID.eq(COMM_SECO_CATE.ID))
                .join(SITE_MEMBER).on(COMM_POST.AUTH_MEMB_UUID.eq(SITE_MEMBER.UUID))
                .where(COMM_POST.ULID.eq(postId.getValue()))
                .fetchOne()
        ).map(postJooqMapper::toPostDetailDataReadModel);
    }

    private String toTsQueryFormat(String keyword) {
        return Arrays.stream(keyword.trim().split("\\s+"))
                .map(word -> word.replaceAll("[&|!():*]", ""))
                .filter(word -> !word.isEmpty())
                .collect(Collectors.joining(" & "));
    }

    private Condition buildCategoryConditions(Integer primaryCategoryId, List<Integer> secondaryCategoryIds) {
        if (primaryCategoryId == null && secondaryCategoryIds != null && !secondaryCategoryIds.isEmpty()) {
            throw new EmptyValueException(PostErrorCode.EMPTY_CATEGORY_ID);
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

    private Condition buildKeywordCondition(String keyword, SearchOption option, boolean isAccuracy) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return noCondition();
        }

        String searchKeyword = "%" + escapeWildcards(keyword) + "%";
        // comment 조건은 정확도순/최신순 모두 동일한 LIKE 조건
        Condition commentCondition = exists(
                selectOne().from(COMM_COMMENT)
                        .where(COMM_COMMENT.POST_ULID.eq(COMM_POST.ULID))
                        .and(COMM_COMMENT.IS_DELETED.isFalse())
                        .and(COMM_COMMENT.CONTENT.likeIgnoreCase(searchKeyword))
        );

        // 정확도순 조회
        if (isAccuracy) {
            // ftsCondition : search_vector에는 title(가중치A) + content(가중치B)가 포함되어 있음
            Condition ftsCondition = field(
                    "{0} @@ to_tsquery('simple', {1})",
                    Boolean.class,
                    COMM_POST.field("search_vector"),
                    val(toTsQueryFormat(keyword))
            ).isTrue();
            // titleSimilarityCondition : similarity는 한국어 형태소 미처리 케이스 보완용 (title 대상)
            Condition titleSimilarityCondition = field(
                    "similarity({0}, {1})", Double.class,
                    COMM_POST.TITLE, val(keyword)
            ).gt(0.1);

            return switch (option) {
                case TITLE -> ftsCondition.or(titleSimilarityCondition);    // title search_vector 매칭 + trgm 유사도 보완
                case CONTENT -> ftsCondition;                               // content는 search_vector에 포함되므로 fts만
                case TITLE_CONTENT ->  ftsCondition.or(titleSimilarityCondition);
                case TITLE_CONTENT_COMMENT -> ftsCondition.or(titleSimilarityCondition).or(commentCondition);   // title + content(search_vector) + comment content(LIKE)
            };
        }

        // 최신순: 기존 LIKE 유지
        Name alias = name("c");
        Condition titleCondition = COMM_POST.TITLE.likeIgnoreCase(searchKeyword);
        Condition contentCondition = exists(
                selectOne()
                        .from(table("jsonb_array_elements({0})", COMM_POST.CONTENT).as(alias))
                        .where(
                                field("{0}->>'type'", String.class, field(alias)).eq(val("text"))
                                        .and(field("{0}->>'data'", String.class, field(alias))
                                                .likeIgnoreCase(val(searchKeyword)))
                        )
        );

        return switch (option) {
            case TITLE -> titleCondition;
            case CONTENT -> contentCondition;
            case TITLE_CONTENT -> titleCondition.or(contentCondition);
            case TITLE_CONTENT_COMMENT -> titleCondition.or(contentCondition).or(commentCondition);
        };
    }

    private String escapeWildcards(String input) {
        return input
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    private List<SortField<?>> buildOrderBy(String keyword, SearchOption option, SearchSort sort) {
        if (sort == SearchSort.RELEVANCE && keyword != null && !keyword.trim().isEmpty()) {
            return List.of(buildScoreField(keyword,option).desc(), COMM_POST.ULID.desc());
        }
        return List.of(COMM_POST.PUBLISHED_AT.desc(), COMM_POST.ULID.desc());
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

    private Condition buildAccuracyCursorCondition(String cursorUlid, String keyword, SearchOption option) {
        if (cursorUlid == null) {
            return noCondition();
        }

        Field<Double> scoreField = buildScoreField(keyword,option);
        Double cursorScore = dsl.select(scoreField)
                .from(COMM_POST)
                .where(COMM_POST.ULID.eq(cursorUlid))
                .fetchOne(scoreField);

        if (cursorScore == null) {
            throw new PostNotFoundException();
        }

        Field<Double> score = buildScoreField(keyword, option);

        // (score < cursorScore) OR (score = cursorScore AND ulid < cursorUlid)
        // score가 같으면 ulid 내림차순으로 보조 정렬하므로 ulid lt로 처리
        return score.lt(val(cursorScore))
                .or(score.eq(val(cursorScore))
                        .and(COMM_POST.ULID.lt(cursorUlid)));
    }

    private Field<Double> buildScoreField(String keyword, SearchOption option) {
        return switch (option) {
            // CONTENT
            case CONTENT ->
                    field(
                            "ts_rank({0}, to_tsquery('simple', {1}))",
                            Double.class,
                            field("search_vector"), val(toTsQueryFormat(keyword))
                    );
            // TITLE, TITLE_CONTENT, TITLE_CONTENT_COMMENT
            default ->
                    field(
                            "ts_rank({0}, to_tsquery('simple', {1})) + similarity({2}, {3}) * 0.3",
                            Double.class,
                            field("search_vector"), val(toTsQueryFormat(keyword)),
                            COMM_POST.TITLE, val(keyword)
                    );
        };
    }
}
