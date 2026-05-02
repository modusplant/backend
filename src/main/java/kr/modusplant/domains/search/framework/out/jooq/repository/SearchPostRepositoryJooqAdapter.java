package kr.modusplant.domains.search.framework.out.jooq.repository;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.domains.post.domain.exception.EmptyValueException;
import kr.modusplant.domains.post.domain.exception.enums.PostErrorCode;
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
    
    @Override
    public List<SearchPostReadModel> searchByKeywordWithLatest(
            SearchKeyword searchKeyword, SearchPostTarget target, Integer primaryCategoryId, List<Integer> secondaryCategoryIds,
            SearchPostId searchPostId, SearchPostPublishedAt searchPostPublishedAt, int size, UUID memberId) {
        String keyword = searchKeyword.getValue();
        String cursorUlid = searchPostId.getValue();
        LocalDateTime cursorPublishedAt = searchPostPublishedAt.getValue();
        Field<String> keywordLongerThanOrEqualToThree = val("%" + escape(keyword, '$') + "%");
        Field<String> keywordLowerThanThree = val(escape(keyword, '$') + "%");

        // 1. SearchOption에 따른 타겟 옵션 불리언 플래그 설정
        boolean isTitle =
                target == SearchPostTarget.TITLE ||
                        target == SearchPostTarget.TITLE_CONTENT ||
                        target == SearchPostTarget.TITLE_CONTENT_COMMENT;
        boolean isContent =
                target == SearchPostTarget.CONTENT ||
                        target == SearchPostTarget.TITLE_CONTENT ||
                        target == SearchPostTarget.TITLE_CONTENT_COMMENT;
        boolean isComment = target == SearchPostTarget.TITLE_CONTENT_COMMENT;

        List<CommonTableExpression<?>> ctes = new ArrayList<>();

        // 2. matched_comments CTE 생성 및 추가 (댓글 옵션이 있을 때만)
        CommonTableExpression<?> matchedCommentsCte = null;
        Field<String> matchedCommentsPostUlid = field(name("matched_comments", "post_ulid"), String.class);

        LikeEscapeStep ilikeConditionForComment = keyword.length() >= 3 ?
                COMM_COMMENT.CONTENT.likeIgnoreCase(keywordLongerThanOrEqualToThree) :
                COMM_COMMENT.CONTENT.likeIgnoreCase(keywordLowerThanThree);

        if (isComment) {
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

        if (isTitle) {
            LikeEscapeStep ilikeConditionForTitle =
                    keyword.length() >= 3 ?
                            COMM_POST.TITLE.likeIgnoreCase(keywordLongerThanOrEqualToThree) :
                            COMM_POST.TITLE.likeIgnoreCase(keywordLowerThanThree);
            matchCondition = matchCondition.or(ilikeConditionForTitle);
        }
        if (isContent) {
            LikeEscapeStep ilikeConditionForContent =
                    keyword.length() >= 3 ?
                            COMM_POST.CONTENT_TEXT.likeIgnoreCase(keywordLongerThanOrEqualToThree) :
                            COMM_POST.CONTENT_TEXT.likeIgnoreCase(keywordLowerThanThree);
            matchCondition = matchCondition.or(ilikeConditionForContent);
        }
        if (isComment) {
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

        if (isComment) {
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
        Condition isLiked =
                memberId != null ?
                        exists(
                                selectOne().from(COMM_POST_LIKE)
                                        .where(COMM_POST_LIKE.POST_ULID.eq(eHitsUlid))
                                        .and(COMM_POST_LIKE.MEMB_UUID.eq(memberId))) :
                        falseCondition();

        Condition isBookmarked =
                memberId != null ?
                        exists(
                                selectOne().from(COMM_POST_BOOKMARK)
                                        .where(COMM_POST_BOOKMARK.POST_ULID.eq(eHitsUlid))
                                        .and(COMM_POST_BOOKMARK.MEMB_UUID.eq(memberId))) :
                        falseCondition();

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
        Field<String> keywordLongerThanOrEqualToThreeParam = val("%" + escape(keyword, '$') + "%");
        Field<String> keywordLowerThanThreeParam = val(escape(keyword, '$') + "%");

        // 1. 불리언 플래그 설정
        // SearchOption에 따른 플래그
        boolean isTitle =
                target == SearchPostTarget.TITLE ||
                        target == SearchPostTarget.TITLE_CONTENT ||
                        target == SearchPostTarget.TITLE_CONTENT_COMMENT;
        boolean isContent =
                target == SearchPostTarget.CONTENT ||
                        target == SearchPostTarget.TITLE_CONTENT ||
                        target == SearchPostTarget.TITLE_CONTENT_COMMENT;
        boolean isComment = target == SearchPostTarget.TITLE_CONTENT_COMMENT;

        List<CommonTableExpression<?>> ctes = new ArrayList<>(); // 실행할 CTE를 동적으로 담을 리스트

        // 키워드 길이에 따른 플래그
        boolean passWithLowWordSimilarity = keyword.length() >= 3;

        // 2. matched_comments CTE 생성 및 추가 (댓글 옵션이 있을 때만)
        CommonTableExpression<?> matchedCommentsCte = null;
        Field<String> mcPostUlid = field(name("matched_comments", "post_ulid"), String.class);
        Field<Double> mcCommentWordSimilarity = field(name("matched_comments", "comment_wsim"), Double.class);

        if (isComment) {
            LikeEscapeStep ilikeConditionForComment = keyword.length() >= 3 ?
                    COMM_COMMENT.CONTENT.likeIgnoreCase(keywordLongerThanOrEqualToThreeParam) :
                    COMM_COMMENT.CONTENT.likeIgnoreCase(keywordLowerThanThreeParam);

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
        LikeEscapeStep ilikeConditionForTitle = keyword.length() >= 3 ?
                COMM_POST.TITLE.likeIgnoreCase(keywordLongerThanOrEqualToThreeParam) :
                COMM_POST.TITLE.likeIgnoreCase(keywordLowerThanThreeParam);
        LikeEscapeStep ilikeConditionForContent = keyword.length() >= 3 ?
                COMM_POST.CONTENT_TEXT.likeIgnoreCase(keywordLongerThanOrEqualToThreeParam) :
                COMM_POST.CONTENT_TEXT.likeIgnoreCase(keywordLowerThanThreeParam);

        CaseConditionStep<Integer> impoCase = null;
        if (isTitle) {
            impoCase = case_().when(ilikeConditionForTitle, 4);
        }
        if (isContent) {
            impoCase = (impoCase == null) ?
                    case_().when(ilikeConditionForContent, 3) :
                    impoCase.when(ilikeConditionForContent, 3);
        }
        if (isComment) {
            impoCase = impoCase.when(matchedCommentsHasMatchedComment.isTrue(), 2);
        }
        Field<Integer> impoField = impoCase != null ? impoCase.otherwise(1).as("impo") : val(1).as("impo");

        // 최대 정확도(max_wsim) 동적 계산 (해당 옵션만 GREATEST 함수에 추가)
        List<Field<?>> scoreFields = new ArrayList<>();
        if (isTitle) {
            scoreFields.add(coalesce(titleWordSimilarity, 0));
        }
        if (isContent) {
            scoreFields.add(coalesce(contentWordSimilarity, 0));
        }
        if (isComment) {
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
        if (isTitle) {
            indexMatchCondition = indexMatchCondition.or(condition("{0} %> {1}", COMM_POST.TITLE, keywordParam));
            if (passWithLowWordSimilarity) {
                indexMatchCondition.or(ilikeConditionForTitle);
            }
        }
        if (isContent) {
            indexMatchCondition = indexMatchCondition.or(condition("{0} %> {1}", COMM_POST.CONTENT_TEXT, keywordParam));
            if (passWithLowWordSimilarity) {
                indexMatchCondition.or(ilikeConditionForContent);
            }
        }
        if (isComment) {
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
        if (isComment) {
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
        Condition isLiked =
                memberId != null ?
                        exists(
                                selectOne().from(COMM_POST_LIKE)
                                        .where(COMM_POST_LIKE.POST_ULID.eq(eHitsUlid))
                                        .and(COMM_POST_LIKE.MEMB_UUID.eq(memberId))) :
                        falseCondition();

        Condition isBookmarked =
                memberId != null ?
                        exists(
                                selectOne().from(COMM_POST_BOOKMARK)
                                        .where(COMM_POST_BOOKMARK.POST_ULID.eq(eHitsUlid))
                                        .and(COMM_POST_BOOKMARK.MEMB_UUID.eq(memberId))) :
                        falseCondition();

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

    private Condition buildCategoryConditions(Integer primaryCategoryId, List<Integer> secondaryCategoryIds) {
        if (primaryCategoryId == null && !CollectionUtils.isNullOrEmpty(secondaryCategoryIds)) {
            throw new EmptyValueException(PostErrorCode.EMPTY_CATEGORY_ID);
        }
        Condition condition = noCondition();
        if (primaryCategoryId != null) {
            condition = condition.and(COMM_POST.PRI_CATE_ID.eq(primaryCategoryId));
        }
        if (primaryCategoryId != null && !CollectionUtils.isNullOrEmpty(secondaryCategoryIds)) {
            condition = condition.and(COMM_POST.SECO_CATE_ID.in(secondaryCategoryIds));
        }
        return condition;
    }
}
