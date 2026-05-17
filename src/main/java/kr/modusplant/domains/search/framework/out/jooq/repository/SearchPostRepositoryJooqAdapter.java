package kr.modusplant.domains.search.framework.out.jooq.repository;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Nonnull;
import kr.modusplant.domains.search.domain.enums.SearchPostTarget;
import kr.modusplant.domains.search.domain.vo.*;
import kr.modusplant.domains.search.framework.out.jooq.mapper.supers.SearchJooqMapper;
import kr.modusplant.domains.search.usecase.model.read.SearchPostReadModel;
import kr.modusplant.domains.search.usecase.port.repository.SearchPostRepository;
import kr.modusplant.framework.jooq.converter.JsonbJsonNodeConverter;
import lombok.RequiredArgsConstructor;
import org.jooq.*;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.utils.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static kr.modusplant.jooq.Tables.*;
import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class SearchPostRepositoryJooqAdapter implements SearchPostRepository {
    private final DSLContext dsl;
    private final SearchJooqMapper searchJooqMapper;
    private final JsonbJsonNodeConverter jsonConverter = new JsonbJsonNodeConverter();
    private final char escapeChar = '$'; // 이스케이프 문자를 상수로 지정

    @Override
    public List<SearchPostReadModel> searchByKeywordWithLatest(
            SearchKeyword searchKeyword, SearchPostTarget target, Integer primaryCategoryId, List<Integer> secondaryCategoryIds,
            SearchPostId searchPostId, SearchPostPublishedAt searchPostPublishedAt, int size, UUID memberId) {
        String keyword = searchKeyword.getValue();
        String cursorUlid = searchPostId.getValue();
        LocalDateTime cursorPublishedAt = searchPostPublishedAt.getValue();

        // 1. 키워드의 길이에 따른 ILIKE 전용 매칭 설정
        Field<String> partialMatchToKeywordWithThreeOrMoreLetters = val("%" + escape(keyword, escapeChar) + "%");
        Field<String> prefixMatchToOneOrTwoLetterKeyword = val(escape(keyword, escapeChar) + "%");
        Field<String> partialMatchToTwoLetterKeyword = val("% " + escape(keyword, escapeChar) + "%");
        Field<String> partialMatchToOneLetterKeyword = val("% " + escape(keyword, escapeChar) + " %");
        Field<String> suffixMatchToOneLetterKeyword = val("%" + escape(keyword, escapeChar));

        // 2. matched_comments CTE 생성 및 추가 (댓글 옵션이 있을 때만)
        List<CommonTableExpression<?>> ctes = new ArrayList<>(); // 실행할 CTE를 동적으로 담을 리스트
        CommonTableExpression<?> matchedCommentsCte = null;
        Field<String> matchedCommentsPostUlid = field(name("matched_comments", "post_ulid"), String.class);

        if (target.containsComment()) {
            Condition ilikeConditionForComment = getIlikeCondition(
                    COMM_COMMENT.CONTENT,
                    keyword,
                    partialMatchToKeywordWithThreeOrMoreLetters,
                    prefixMatchToOneOrTwoLetterKeyword,
                    partialMatchToTwoLetterKeyword,
                    partialMatchToOneLetterKeyword,
                    suffixMatchToOneLetterKeyword
            );
            matchedCommentsCte = name("matched_comments").as(
                    selectDistinct(COMM_COMMENT.POST_ULID)
                            .from(COMM_COMMENT)
                            .where(COMM_COMMENT.IS_DELETED.isFalse())
                            .and(ilikeConditionForComment)
            );
            ctes.add(matchedCommentsCte);
        }

        // 3. search_hits CTE 조립
        Condition matchCondition = noCondition();

        if (target.containsTitle()) {
            Condition ilikeConditionForTitle = getIlikeCondition(
                    COMM_POST.TITLE,
                    keyword,
                    partialMatchToKeywordWithThreeOrMoreLetters,
                    prefixMatchToOneOrTwoLetterKeyword,
                    partialMatchToTwoLetterKeyword,
                    partialMatchToOneLetterKeyword,
                    suffixMatchToOneLetterKeyword
            );
            matchCondition = matchCondition.or(ilikeConditionForTitle);
        }
        if (target.containsContent()) {
            Condition ilikeConditionForContent = getIlikeCondition(
                    COMM_POST.CONTENT_TEXT,
                    keyword,
                    partialMatchToKeywordWithThreeOrMoreLetters,
                    prefixMatchToOneOrTwoLetterKeyword,
                    partialMatchToTwoLetterKeyword,
                    partialMatchToOneLetterKeyword,
                    suffixMatchToOneLetterKeyword
            );
            matchCondition = matchCondition.or(ilikeConditionForContent);
        }
        if (target.containsComment()) {
            matchCondition = matchCondition.or(matchedCommentsPostUlid.isNotNull());
        }

        SelectJoinStep<?> searchHitsFromStep =
                select(
                        COMM_POST.ULID,
                        COMM_POST.PRI_CATE_ID,
                        COMM_POST.SECO_CATE_ID,
                        COMM_POST.AUTH_MEMB_UUID,
                        COMM_POST.TITLE,
                        COMM_POST.CONTENT.convert(jsonConverter).as("content"),
                        COMM_POST.THUMBNAIL_PATH,
                        COMM_POST.LIKE_COUNT,
                        COMM_POST.PUBLISHED_AT
                ).from(COMM_POST);

        if (target.containsComment()) {
            searchHitsFromStep = searchHitsFromStep.leftJoin(matchedCommentsCte).on(COMM_POST.ULID.eq(matchedCommentsPostUlid));
        }

        CommonTableExpression<?> searchHitsCte = name("search_hits").as(
                searchHitsFromStep
                        .where(COMM_POST.IS_PUBLISHED.isTrue())
                        .and(COMM_POST.PUBLISHED_AT.isNotNull())
                        .and(buildCategoryConditions(primaryCategoryId, secondaryCategoryIds))
                        .and(matchCondition)
        );
        ctes.add(searchHitsCte);

        // 4. evaluated_hits CTE 정의 (커서 조건 구성)
        Field<String> searchHitsUlid = field(name("search_hits", "ulid"), String.class);
        Field<LocalDateTime> searchHitsPublishedAt = field(name("search_hits", "published_at"), LocalDateTime.class);

        Condition cursorCondition = noCondition();
        if (cursorUlid != null && cursorPublishedAt != null) {
            cursorCondition = searchHitsPublishedAt.lt(cursorPublishedAt)
                    .or(searchHitsPublishedAt.eq(cursorPublishedAt).and(searchHitsUlid.lt(cursorUlid)));
        }

        CommonTableExpression<?> evaluatedHitsCte = name("evaluated_hits").as(
                select(asterisk()).from(searchHitsCte).where(cursorCondition)
        );
        ctes.add(evaluatedHitsCte);

        // 5. 최종 쿼리 매핑 및 실행
        Table<?> eHits = evaluatedHitsCte.as("e_hits");
        Field<String> eHitsUlid = eHits.field("ulid", String.class);
        Field<JsonNode> eHitsContent = Objects.requireNonNull(eHits.field("content", JSONB.class)).convert(jsonConverter);
        Field<LocalDateTime> eHitsPublishedAt = eHits.field("published_at", LocalDateTime.class);

        Table<?> commentCountTable = select(COMM_COMMENT.POST_ULID, count().as("comment_count"))
                .from(COMM_COMMENT)
                .where(COMM_COMMENT.IS_DELETED.isFalse())
                .groupBy(COMM_COMMENT.POST_ULID)
                .asTable("comm_cnt");

        // memberId의 null 여부에 따른 쿼리 최적화
        Condition isLiked = getIsLikedCondition(memberId, eHitsUlid);
        Condition isBookmarked = getIsBookmarkedCondition(memberId, eHitsUlid);

        //noinspection DataFlowIssue
        return dsl.with(ctes)
                .select(
                        eHitsUlid.as("ulid"),
                        COMM_PRI_CATE.CATEGORY.as("primaryCategory"),
                        COMM_SECO_CATE.CATEGORY.as("secondaryCategory"),
                        SITE_MEMBER.NICKNAME,
                        eHits.field("title", String.class).as("title"),
                        eHitsContent.as("content"),
                        eHits.field("thumbnail_path", String.class).as("thumbnailPath"),
                        eHits.field("like_count", Integer.class).as("likeCount"),
                        eHitsPublishedAt.as("publishedAt"),
                        coalesce(commentCountTable.field("comment_count", Integer.class), 0).as("commentCount"),
                        isLiked.as("isLiked"),
                        isBookmarked.as("isBookmarked"),
                        val( null, Integer.class).as("importance"),
                        val( null, Double.class).as("maxWordSimilarity")
                )
                .from(eHits)
                .join(COMM_PRI_CATE).on(eHits.field("pri_cate_id", Integer.class).eq(COMM_PRI_CATE.ID))
                .join(COMM_SECO_CATE).on(eHits.field("seco_cate_id", Integer.class).eq(COMM_SECO_CATE.ID))
                .leftJoin(SITE_MEMBER).on(eHits.field("auth_memb_uuid", UUID.class).eq(SITE_MEMBER.UUID))
                .leftJoin(commentCountTable).on(eHitsUlid.eq(commentCountTable.field(COMM_COMMENT.POST_ULID)))
                .orderBy(
                        eHitsPublishedAt.desc(),
                        eHitsUlid.desc()
                )
                .limit(size + 1)
                .fetch()
                .map(searchJooqMapper::toSearchPostReadModel);
    }

    @Override
    public List<SearchPostReadModel> searchByKeywordWithRelevance(
            SearchKeyword searchKeyword, SearchPostTarget target, Integer primaryCategoryId, List<Integer> secondaryCategoryIds,
            SearchPostId searchPostId, SearchPostPublishedAt searchPostPublishedAt,
            SearchPostImportance searchPostImportance, SearchKeywordSimilarity searchKeywordSimilarity,
            int size, UUID memberId) {
        String keyword = searchKeyword.getValue();
        String cursorUlid = searchPostId.getValue();
        LocalDateTime cursorPublishedAt = searchPostPublishedAt.getValue();
        Field<String> keywordParam = val(keyword);

        // 1. 키워드의 길이에 따른 ILIKE 전용 매칭 설정
        Field<String> partialMatchToKeywordWithThreeOrMoreLetters = val("%" + escape(keyword, escapeChar) + "%");
        Field<String> prefixMatchToOneOrTwoLetterKeyword = val(escape(keyword, escapeChar) + "%");
        Field<String> partialMatchToTwoLetterKeyword = val("% " + escape(keyword, escapeChar) + "%");
        Field<String> partialMatchToOneLetterKeyword = val("% " + escape(keyword, escapeChar) + " %");
        Field<String> suffixMatchToOneLetterKeyword = val("%" + escape(keyword, escapeChar));

        // 2. matched_comments CTE 생성 및 추가 (댓글 옵션이 있을 때만)
        List<CommonTableExpression<?>> ctes = new ArrayList<>(); // 실행할 CTE를 동적으로 담을 리스트
        boolean passWithLowWordSimilarity = keyword.length() >= 3; // 키워드 길이에 따른 플래그

        CommonTableExpression<?> matchedCommentsCte = null;
        Field<String> mcPostUlid = field(name("matched_comments", "post_ulid"), String.class);
        Field<Double> mcCommentWordSimilarity = field(name("matched_comments", "comment_wsim"), Double.class);

        if (target.containsComment()) {
            Condition ilikeConditionForComment = getIlikeCondition(
                    COMM_COMMENT.CONTENT,
                    keyword,
                    partialMatchToKeywordWithThreeOrMoreLetters,
                    prefixMatchToOneOrTwoLetterKeyword,
                    partialMatchToTwoLetterKeyword,
                    partialMatchToOneLetterKeyword,
                    suffixMatchToOneLetterKeyword
            );
            Condition wordSimilarityCondition = condition("{0} %> {1}", COMM_COMMENT.CONTENT, keywordParam);
            if (passWithLowWordSimilarity) {
                wordSimilarityCondition = wordSimilarityCondition.or(ilikeConditionForComment);
            }

            matchedCommentsCte = name("matched_comments").as(
                    select(
                            COMM_COMMENT.POST_ULID,
                            max(field("word_similarity({0}, {1})", Double.class, keywordParam, COMM_COMMENT.CONTENT))
                                    .as("comment_wsim"),
                            boolOr(ilikeConditionForComment)
                                    .as("has_matched_comment")
                    )
                            .from(COMM_COMMENT)
                            .where(COMM_COMMENT.IS_DELETED.isFalse())
                            .and(wordSimilarityCondition)
                            .groupBy(COMM_COMMENT.POST_ULID)
            );
            ctes.add(matchedCommentsCte);
        }

        // 3. search_hits CTE impo 및 max_wsim 필드 구성
        Field<Double> titleWordSimilarity =
                field("word_similarity({0}, {1})", Double.class, keywordParam, COMM_POST.TITLE);
        Field<Double> contentWordSimilarity =
                field("word_similarity({0}, {1})", Double.class, keywordParam, COMM_POST.CONTENT_TEXT);
        Field<Boolean> matchedCommentsHasMatchedComment =
                field(name("matched_comments", "has_matched_comment"), Boolean.class);

        // 중요도(impo) 동적 계산 (해당 옵션만 CASE 조건에 추가)
        Condition ilikeConditionForTitle = getIlikeCondition(
                COMM_POST.TITLE,
                keyword,
                partialMatchToKeywordWithThreeOrMoreLetters,
                prefixMatchToOneOrTwoLetterKeyword,
                partialMatchToTwoLetterKeyword,
                partialMatchToOneLetterKeyword,
                suffixMatchToOneLetterKeyword
        );
        Condition ilikeConditionForContent = getIlikeCondition(
                COMM_POST.CONTENT_TEXT,
                keyword,
                partialMatchToKeywordWithThreeOrMoreLetters,
                prefixMatchToOneOrTwoLetterKeyword,
                partialMatchToTwoLetterKeyword,
                partialMatchToOneLetterKeyword,
                suffixMatchToOneLetterKeyword
        );
        CaseConditionStep<Integer> impoCase = null;
        if (target.containsTitle()) {
            impoCase = case_().when(ilikeConditionForTitle, 4);
        }
        if (target.containsContent()) {
            impoCase = (impoCase == null) ?
                    case_().when(ilikeConditionForContent, 3) :
                    impoCase.when(ilikeConditionForContent, 3);
        }
        if (target.containsComment()) {
            impoCase = Objects.requireNonNull(impoCase).when(matchedCommentsHasMatchedComment.isTrue(), 2);
        }
        Field<Integer> impoField = impoCase != null ? impoCase.otherwise(1).as("impo") : val(1).as("impo");

        // 최대 정확도(max_wsim) 동적 계산 (해당 옵션만 GREATEST 함수에 추가)
        List<Field<?>> scoreFields = new ArrayList<>();
        if (target.containsTitle()) {
            scoreFields.add(coalesce(titleWordSimilarity, 0));
        }
        if (target.containsContent()) {
            scoreFields.add(coalesce(contentWordSimilarity, 0));
        }
        if (target.containsComment()) {
            scoreFields.add(coalesce(mcCommentWordSimilarity, 0));
        }

        Field<Double> maxWordSimilarityField;
        if (scoreFields.size() > 1) {
            //noinspection unchecked
            maxWordSimilarityField =
                    (Field<Double>) greatest(
                            scoreFields.getFirst(),
                            scoreFields.subList(1, scoreFields.size()).toArray(new Field[0]))
                            .as("max_wsim");
        } else {
            maxWordSimilarityField = scoreFields.getFirst().cast(Double.class).as("max_wsim");
        }

        // 인덱스 매칭용 동적 WHERE 조건 구성
        Condition indexMatchCondition = noCondition();
        if (target.containsTitle()) {
            indexMatchCondition = indexMatchCondition.or(condition("{0} %> {1}", COMM_POST.TITLE, keywordParam));
            if (passWithLowWordSimilarity) {
                indexMatchCondition = indexMatchCondition.or(ilikeConditionForTitle);
            }
        }
        if (target.containsContent()) {
            indexMatchCondition = indexMatchCondition.or(condition("{0} %> {1}", COMM_POST.CONTENT_TEXT, keywordParam));
            if (passWithLowWordSimilarity) {
                indexMatchCondition = indexMatchCondition.or(ilikeConditionForContent);
            }
        }
        if (target.containsComment()) {
            indexMatchCondition = indexMatchCondition.or(mcPostUlid.isNotNull()); // JOIN 성공 여부로 판단
        }

        // 4. search_hits CTE 조립 (동적 조인)
        SelectJoinStep<?> searchHitsFromStep =
                select(
                        COMM_POST.ULID,
                        COMM_POST.PRI_CATE_ID,
                        COMM_POST.SECO_CATE_ID,
                        COMM_POST.AUTH_MEMB_UUID,
                        COMM_POST.TITLE,
                        COMM_POST.CONTENT.convert(jsonConverter).as("content"),
                        COMM_POST.THUMBNAIL_PATH,
                        COMM_POST.LIKE_COUNT,
                        COMM_POST.PUBLISHED_AT,
                        impoField,
                        maxWordSimilarityField
                ).from(COMM_POST);

        // 댓글 조회 옵션이 켜져 있을 때만 LEFT JOIN 수행
        if (target.containsComment()) {
            searchHitsFromStep = searchHitsFromStep.leftJoin(matchedCommentsCte).on(COMM_POST.ULID.eq(mcPostUlid));
        }

        CommonTableExpression<?> searchHitsCte = name("search_hits").as(
                searchHitsFromStep
                        .where(COMM_POST.IS_PUBLISHED.isTrue())
                        .and(COMM_POST.PUBLISHED_AT.isNotNull())
                        .and(buildCategoryConditions(primaryCategoryId, secondaryCategoryIds))
                        .and(indexMatchCondition)
        );
        ctes.add(searchHitsCte);

        // 5. evaluated_hits CTE 정의 (커서 조건 구성)
        Field<String> searchHitsUlid = field(name("search_hits", "ulid"), String.class);
        Field<Integer> searchHitsImportance = field(name("search_hits", "impo"), Integer.class);
        Field<LocalDateTime> searchHitsPublishedAt = field(name("search_hits", "published_at"), LocalDateTime.class);
        Field<Double> searchHitsMaxWordSimilarity = field(name("search_hits", "max_wsim"), Double.class);

        Condition cursorCondition = noCondition();
        if (!searchPostImportance.isEmpty() && !searchKeywordSimilarity.isEmpty()) {
            int cursorImportance = searchPostImportance.getValueIfNotEmpty();
            double cursorMaxWordSimilarity = searchKeywordSimilarity.getValueIfNotEmpty();
            cursorCondition =
                    searchHitsImportance.lt(cursorImportance)
                            .or(searchHitsImportance.eq(cursorImportance).and(searchHitsImportance.in(2, 3, 4))
                                    .and(searchHitsPublishedAt.lt(cursorPublishedAt)
                                            .or(searchHitsPublishedAt.eq(cursorPublishedAt)
                                                    .and(searchHitsUlid.lt(cursorUlid)))))
                            .or(searchHitsImportance.eq(cursorImportance).and(searchHitsImportance.eq(1))
                                    .and(searchHitsMaxWordSimilarity.lt(cursorMaxWordSimilarity)
                                            .or(searchHitsMaxWordSimilarity.eq(cursorMaxWordSimilarity)
                                                    .and(searchHitsUlid.lt(cursorUlid)))));
        }

        CommonTableExpression<?> evaluatedHitsCte = name("evaluated_hits").as(
                select(asterisk()).from(searchHitsCte).where(cursorCondition)
        );
        ctes.add(evaluatedHitsCte);

        // 6. 최종 쿼리 매핑 및 실행
        Table<?> eHits = evaluatedHitsCte.as("e_hits");
        Field<String> eHitsUlid = eHits.field("ulid", String.class);
        Field<JsonNode> eHitsContent = Objects.requireNonNull(eHits.field("content", JSONB.class)).convert(jsonConverter);
        Field<LocalDateTime> eHitsPublishedAt = eHits.field("published_at", LocalDateTime.class);
        Field<Integer> eHitsImportance = eHits.field("impo", Integer.class);
        Field<Double> eHitsMaxWordSimilarity = eHits.field("max_wsim", Double.class);

        Table<?> commentCountTable = select(COMM_COMMENT.POST_ULID, count().as("comment_count"))
                .from(COMM_COMMENT)
                .where(COMM_COMMENT.IS_DELETED.isFalse())
                .groupBy(COMM_COMMENT.POST_ULID)
                .asTable("comm_cnt");

        // memberId의 null 여부에 따른 쿼리 최적화
        Condition isLiked = getIsLikedCondition(memberId, eHitsUlid);
        Condition isBookmarked = getIsBookmarkedCondition(memberId, eHitsUlid);

        //noinspection DataFlowIssue
        return dsl.with(ctes)  // 💡 리스트로 모아둔 CTE들을 한 번에 등록
                .select(
                        eHitsUlid.as("ulid"),
                        COMM_PRI_CATE.CATEGORY.as("primaryCategory"),
                        COMM_SECO_CATE.CATEGORY.as("secondaryCategory"),
                        SITE_MEMBER.NICKNAME,
                        eHits.field("title", String.class).as("title"),
                        eHitsContent.as("content"),
                        eHits.field("thumbnail_path", String.class).as("thumbnailPath"),
                        eHits.field("like_count", Integer.class).as("likeCount"),
                        eHitsPublishedAt.as("publishedAt"),
                        coalesce(commentCountTable.field("comment_count", Integer.class), 0).as("commentCount"),
                        isLiked.as("isLiked"),
                        isBookmarked.as("isBookmarked"),
                        eHitsImportance.as("importance"),
                        eHitsMaxWordSimilarity.as("maxWordSimilarity")
                )
                .from(eHits)
                .join(COMM_PRI_CATE).on(eHits.field("pri_cate_id", Integer.class).eq(COMM_PRI_CATE.ID))
                .join(COMM_SECO_CATE).on(eHits.field("seco_cate_id", Integer.class).eq(COMM_SECO_CATE.ID))
                .leftJoin(SITE_MEMBER).on(eHits.field("auth_memb_uuid", UUID.class).eq(SITE_MEMBER.UUID))
                .leftJoin(commentCountTable).on(eHitsUlid.eq(commentCountTable.field(COMM_COMMENT.POST_ULID)))
                .orderBy(
                        eHitsImportance.desc(),
                        case_().when(eHitsImportance.in(2, 3, 4), eHitsPublishedAt)
                                .otherwise((LocalDateTime) null).desc().nullsLast(),
                        case_().when(eHitsImportance.eq(1), eHitsMaxWordSimilarity)
                                .otherwise((Double) null).desc().nullsLast(),
                        eHitsUlid.desc()
                )
                .limit(size + 1)
                .fetch()
                .map(searchJooqMapper::toSearchPostReadModel);
    }

    private @Nonnull Condition getIlikeCondition(TableField<?, String> tableField,
                                                 String keyword,
                                                 Field<String> partialMatchToKeywordWithThreeOrMoreLetters,
                                                 Field<String> prefixMatchToOneOrTwoLetterKeyword,
                                                 Field<String> partialMatchToTwoLetterKeyword,
                                                 Field<String> partialMatchToOneLetterKeyword,
                                                 Field<String> suffixMatchToOneLetterKeyword) {
        Condition ilikeCondition;
        if (keyword.length() >= 3) {
            ilikeCondition = tableField
                    .likeIgnoreCase(partialMatchToKeywordWithThreeOrMoreLetters)
                    .escape(escapeChar);

        } else if (keyword.length() == 2) {
            ilikeCondition = tableField
                    .likeIgnoreCase(prefixMatchToOneOrTwoLetterKeyword).escape(escapeChar)
                    .or(tableField.likeIgnoreCase(partialMatchToTwoLetterKeyword).escape(escapeChar));
        } else {
            ilikeCondition = tableField
                    .likeIgnoreCase(prefixMatchToOneOrTwoLetterKeyword).escape(escapeChar)
                    .or(tableField.likeIgnoreCase(partialMatchToOneLetterKeyword).escape(escapeChar))
                    .or(tableField.likeIgnoreCase(suffixMatchToOneLetterKeyword).escape(escapeChar));
        }
        return ilikeCondition;
    }

    private static @Nonnull Condition getIsLikedCondition(UUID memberId, Field<String> eHitsUlid) {
        return memberId != null ?
                exists(
                        selectOne().from(COMM_POST_LIKE)
                                .where(COMM_POST_LIKE.POST_ULID.eq(eHitsUlid))
                                .and(COMM_POST_LIKE.MEMB_UUID.eq(memberId))) :
                falseCondition();
    }

    private static @Nonnull Condition getIsBookmarkedCondition(UUID memberId, Field<String> eHitsUlid) {
        return memberId != null ?
                exists(
                        selectOne().from(COMM_POST_BOOKMARK)
                                .where(COMM_POST_BOOKMARK.POST_ULID.eq(eHitsUlid))
                                .and(COMM_POST_BOOKMARK.MEMB_UUID.eq(memberId))) :
                falseCondition();
    }

    private Condition buildCategoryConditions(Integer primaryCategoryId, List<Integer> secondaryCategoryIds) {
        Condition condition = noCondition();
        if (primaryCategoryId != null) {
            condition = condition.and(COMM_POST.PRI_CATE_ID.eq(primaryCategoryId));
        }
        if (!CollectionUtils.isNullOrEmpty(secondaryCategoryIds)) {
            condition = condition.and(COMM_POST.SECO_CATE_ID.in(secondaryCategoryIds));
        }
        return condition;
    }
}
