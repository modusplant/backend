package kr.modusplant.domains.member.framework.outbound.jooq.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static kr.modusplant.jooq.Tables.*;
import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
public class ActivitySubjectPostJooqRepository {
    private final DSLContext dsl;

    public List<String> getPostUlidsLikedByMemberId(UUID memberId) {
        return dsl.select(COMM_POST_LIKE.POST_ULID)
                .from(COMM_POST_LIKE)
                .where(COMM_POST_LIKE.MEMB_UUID.eq(memberId))
                .fetchInto(String.class);
    }

    public void archiveAllByPostUlids(String[] publishedPostUlids) {
        dsl.insertInto(COMM_POST_ARCHIVE,
                        COMM_POST_ARCHIVE.ULID,
                        COMM_POST_ARCHIVE.PRI_CATE_ID,
                        COMM_POST_ARCHIVE.SECO_CATE_ID,
                        COMM_POST_ARCHIVE.AUTH_MEMB_UUID,
                        COMM_POST_ARCHIVE.TITLE,
                        COMM_POST_ARCHIVE.CONTENT_TEXT,
                        COMM_POST_ARCHIVE.CREATED_AT,
                        COMM_POST_ARCHIVE.ARCHIVED_AT,
                        COMM_POST_ARCHIVE.UPDATED_AT,
                        COMM_POST_ARCHIVE.PUBLISHED_AT
                )
                .select(
                        select(
                                COMM_POST.ULID,
                                COMM_POST.PRI_CATE_ID,
                                COMM_POST.SECO_CATE_ID,
                                COMM_POST.AUTH_MEMB_UUID,
                                COMM_POST.TITLE,
                                COMM_POST.CONTENT_TEXT,
                                COMM_POST.CREATED_AT,
                                val(LocalDateTime.now()),
                                COMM_POST.UPDATED_AT,
                                COMM_POST.PUBLISHED_AT
                        )
                                .from(COMM_POST)
                                .where(COMM_POST.ULID.in(publishedPostUlids))
                ).execute();
    }

    public void deleteAllAndRelatedRecordsByMemberIdAndPostUlids(UUID memberId, String[] publishedPostUlids) {
        dsl.batch(
                dsl.deleteFrom(COMM_POST_LIKE)
                        .where(COMM_POST_LIKE.POST_ULID.in(publishedPostUlids)),

                dsl.deleteFrom(COMM_POST_BOOKMARK)
                        .where(COMM_POST_BOOKMARK.POST_ULID.in(publishedPostUlids)),

                dsl.deleteFrom(COMM_COMMENT_LIKE)
                        .where(COMM_COMMENT_LIKE.POST_ULID.in(publishedPostUlids)),

                dsl.deleteFrom(COMM_POST)
                        .where(COMM_POST.AUTH_MEMB_UUID.eq(memberId))
        ).execute();
    }

    public void decreaseLikeCountByPostUlids(List<String> activitySubjectPostUlids) {
        dsl.update(COMM_POST)
                .set(COMM_POST.LIKE_COUNT, COMM_POST.LIKE_COUNT.minus(1))
                .set(COMM_POST.UPDATED_AT, LocalDateTime.now())
                .set(COMM_POST.VER, coalesce(COMM_POST.VER, 0).plus(1))
                .where(
                        COMM_POST.ULID.in(activitySubjectPostUlids)
                                .and(COMM_POST.LIKE_COUNT.gt(0)))   // 좋아요 수가 1 이상일 때만 좋아요 수 감소
                .execute();
    }
}
