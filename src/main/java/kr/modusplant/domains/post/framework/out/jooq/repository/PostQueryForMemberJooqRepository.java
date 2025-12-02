package kr.modusplant.domains.post.framework.out.jooq.repository;

import kr.modusplant.domains.post.domain.vo.AuthorId;
import kr.modusplant.domains.post.framework.out.jooq.mapper.supers.PostJooqMapper;
import kr.modusplant.domains.post.usecase.port.repository.PostQueryForMemberRepository;
import kr.modusplant.domains.post.usecase.record.DraftPostReadModel;
import kr.modusplant.domains.post.usecase.record.PostSummaryReadModel;
import kr.modusplant.framework.jooq.converter.JsonbJsonNodeConverter;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static kr.modusplant.jooq.Tables.*;
import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class PostQueryForMemberJooqRepository implements PostQueryForMemberRepository {
    private final DSLContext dsl;
    private final PostJooqMapper postJooqMapper;
    private static final JsonbJsonNodeConverter JSON_CONVERTER = new JsonbJsonNodeConverter();

    public Page<PostSummaryReadModel> findPublishedByAuthMemberWithOffset(AuthorId authorId, int page, int size) {
        long offset = (long) page * size;

        long totalElements = dsl.selectCount()
                .from(COMM_POST)
                .where(COMM_POST.IS_PUBLISHED.isTrue())
                .and(COMM_POST.AUTH_MEMB_UUID.eq(authorId.getValue()))
                .fetchOne(0, Long.class);

        // 데이터 조회
        List<PostSummaryReadModel> posts = dsl.select(
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
                                .and(COMM_POST_LIKE.MEMB_UUID.eq(authorId.getValue()))
                ).as("isLiked"),
                exists(
                        selectOne().from(COMM_POST_BOOKMARK)
                                .where(COMM_POST_BOOKMARK.POST_ULID.eq(COMM_POST.ULID))
                                .and(COMM_POST_BOOKMARK.MEMB_UUID.eq(authorId.getValue()))
                ).as("isBookmarked")
        ).from(COMM_POST)
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
                .and(COMM_POST.AUTH_MEMB_UUID.eq(authorId.getValue()))
                .orderBy(COMM_POST.PUBLISHED_AT.desc(), COMM_POST.ULID.desc())
                .limit(size)
                .offset(offset)
                .fetch()
                .map(postJooqMapper::toPostSummaryReadModel);

        return new PageImpl<>(posts, PageRequest.of(page, size), totalElements);
    }

    public Page<DraftPostReadModel> findDraftByAuthMemberWithOffset(AuthorId authorId, int page, int size) {
        long offset = (long) page * size;

        long totalElements = dsl.selectCount()
                .from(COMM_POST)
                .where(COMM_POST.IS_PUBLISHED.isFalse())
                .and(COMM_POST.AUTH_MEMB_UUID.eq(authorId.getValue()))
                .fetchOne(0, Long.class);

        // 데이터 조회
        List<DraftPostReadModel> posts = dsl.select(
                        COMM_POST.ULID,
                        COMM_PRI_CATE.CATEGORY.as("primaryCategory"),
                        COMM_SECO_CATE.CATEGORY.as("secondaryCategory"),
                        COMM_POST.TITLE,
                        COMM_POST.CONTENT.convert(JSON_CONVERTER).as("content"),
                        COMM_POST.UPDATED_AT
                ).from(COMM_POST)
                .join(COMM_PRI_CATE).on(COMM_POST.PRI_CATE_UUID.eq(COMM_PRI_CATE.UUID))
                .join(COMM_SECO_CATE).on(COMM_POST.SECO_CATE_UUID.eq(COMM_SECO_CATE.UUID))
                .where(COMM_POST.IS_PUBLISHED.isFalse())
                .and(COMM_POST.AUTH_MEMB_UUID.eq(authorId.getValue()))
                .orderBy(COMM_POST.UPDATED_AT.desc(), COMM_POST.ULID.desc())
                .limit(size)
                .offset(offset)
                .fetch()
                .map(postJooqMapper::toDraftPostReadModel);

        return new PageImpl<>(posts, PageRequest.of(page, size), totalElements);
    }

    public Page<PostSummaryReadModel> findLikedByMemberWithOffset(UUID currentMemberUuid, int page, int size) {
        long offset = (long) page * size;

        long totalElements = dsl.selectCount()
                .from(COMM_POST_LIKE)
                .join(COMM_POST).on(COMM_POST_LIKE.POST_ULID.eq(COMM_POST.ULID))
                .where(COMM_POST.IS_PUBLISHED.isTrue())
                .and(COMM_POST_LIKE.MEMB_UUID.eq(currentMemberUuid))
                .fetchOne(0,Long.class);

        List<PostSummaryReadModel> posts = dsl.select(
                        COMM_POST.ULID,
                        COMM_PRI_CATE.CATEGORY.as("primaryCategory"),
                        COMM_SECO_CATE.CATEGORY.as("secondaryCategory"),
                        SITE_MEMBER.NICKNAME,
                        COMM_POST.TITLE,
                        COMM_POST.CONTENT.convert(JSON_CONVERTER).as("content"),
                        COMM_POST.LIKE_COUNT,
                        COMM_POST.PUBLISHED_AT,
                        coalesce(field("cc.comment_count",Integer.class), 0).as("commentCount"),
                        val(true).as("isLiked"), // 이미 좋아요한 글이므로 true
                        exists(
                                selectOne()
                                        .from(COMM_POST_BOOKMARK)
                                        .where(COMM_POST_BOOKMARK.POST_ULID.eq(COMM_POST.ULID))
                                        .and(COMM_POST_BOOKMARK.MEMB_UUID.eq(currentMemberUuid))
                        ).as("isBookmarked")
                )
                .from(COMM_POST_LIKE)
                .join(COMM_POST).on(COMM_POST_LIKE.POST_ULID.eq(COMM_POST.ULID))
                .join(COMM_PRI_CATE).on(COMM_POST.PRI_CATE_UUID.eq(COMM_PRI_CATE.UUID))
                .join(COMM_SECO_CATE).on(COMM_POST.SECO_CATE_UUID.eq(COMM_SECO_CATE.UUID))
                .join(SITE_MEMBER).on(COMM_POST.AUTH_MEMB_UUID.eq(SITE_MEMBER.UUID))
                .leftJoin(
                        select(COMM_COMMENT.POST_ULID, count().as("comment_count"))
                                .from(COMM_COMMENT)
                                .where(COMM_COMMENT.IS_DELETED.isFalse())
                                .groupBy(COMM_COMMENT.POST_ULID)
                                .asTable("cc")
                ).on(COMM_POST.ULID.eq(field("cc.post_ulid", String.class)))
                .where(COMM_POST.IS_PUBLISHED.isTrue())
                .and(COMM_POST_LIKE.MEMB_UUID.eq(currentMemberUuid))
                .orderBy(COMM_POST_LIKE.CREATED_AT.desc(), COMM_POST.ULID.desc())
                .limit(size)
                .offset(offset)
                .fetch()
                .map(postJooqMapper::toPostSummaryReadModel);

        return new PageImpl<>(posts, PageRequest.of(page, size), totalElements);
    }

    public Page<PostSummaryReadModel> findBookmarkedByMemberWithOffset(UUID currentMemberUuid, int page, int size) {
        long offset = (long) page * size;

        long totalElements = dsl.selectCount()
                .from(COMM_POST_BOOKMARK)
                .join(COMM_POST).on(COMM_POST_BOOKMARK.POST_ULID.eq(COMM_POST.ULID))
                .where(COMM_POST.IS_PUBLISHED.isTrue())
                .and(COMM_POST_BOOKMARK.MEMB_UUID.eq(currentMemberUuid))
                .fetchOne(0,Long.class);

        List<PostSummaryReadModel> posts = dsl.select(
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
                                selectOne()
                                        .from(COMM_POST_LIKE)
                                        .where(COMM_POST_LIKE.POST_ULID.eq(COMM_POST.ULID))
                                        .and(COMM_POST_LIKE.MEMB_UUID.eq(currentMemberUuid))
                        ).as("isLiked"),
                        val(true).as("isBookmarked")
                )
                .from(COMM_POST_BOOKMARK)
                .join(COMM_POST).on(COMM_POST_BOOKMARK.POST_ULID.eq(COMM_POST.ULID))
                .join(COMM_PRI_CATE).on(COMM_POST.PRI_CATE_UUID.eq(COMM_PRI_CATE.UUID))
                .join(COMM_SECO_CATE).on(COMM_POST.SECO_CATE_UUID.eq(COMM_SECO_CATE.UUID))
                .join(SITE_MEMBER).on(COMM_POST.AUTH_MEMB_UUID.eq(SITE_MEMBER.UUID))
                .leftJoin(
                        select(COMM_COMMENT.POST_ULID, count().as("comment_count"))
                                .from(COMM_COMMENT)
                                .where(COMM_COMMENT.IS_DELETED.isFalse())
                                .groupBy(COMM_COMMENT.POST_ULID)
                                .asTable("cc")
                ).on(COMM_POST.ULID.eq(field("cc.post_ulid", String.class)))
                .where(COMM_POST.IS_PUBLISHED.isTrue())
                .and(COMM_POST_BOOKMARK.MEMB_UUID.eq(currentMemberUuid))
                .orderBy(COMM_POST_BOOKMARK.CREATED_AT.desc(), COMM_POST.ULID.desc())
                .limit(size)
                .offset(offset)
                .fetch()
                .map(postJooqMapper::toPostSummaryReadModel);

        return new PageImpl<>(posts, PageRequest.of(page, size), totalElements);
    }
}
