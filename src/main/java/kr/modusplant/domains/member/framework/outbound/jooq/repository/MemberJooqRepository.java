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
public class MemberJooqRepository {
    private final DSLContext dsl;

    public String getProfileImagePathByMemberId(UUID memberId) {
        return dsl.select(SITE_MEMBER_PROF.IMAGE_PATH)
                .from(SITE_MEMBER_PROF)
                .where(SITE_MEMBER_PROF.UUID.eq(memberId))
                .fetchOneInto(String.class);
    }

    public List<String> getPlantInfoPhotoPathsByMemberId(UUID memberId) {
        return dsl.select(PLANT_INFO_PHOTO.FILE_PATH)
                .from(PLANT_INFO_PHOTO)
                .join(PLANT_INFO).on(PLANT_INFO_PHOTO.PLANT_INFO_ID.eq(PLANT_INFO.ID))
                .where(PLANT_INFO.MEMBER_UUID.eq(memberId))
                .fetchInto(String.class);
    }

    public void deleteNotificationsByMemberIdAndPostUlids(UUID memberId, String[] publishedPostUlids) {
        dsl.deleteFrom(COMM_NOTIFICATION)
                .where(COMM_NOTIFICATION.ACTOR_ID.eq(memberId)
                        .or(COMM_NOTIFICATION.RECIPIENT_ID.eq(memberId))
                        .or(COMM_NOTIFICATION.POST_ULID.eq(any(publishedPostUlids)))
                        .or(row(COMM_NOTIFICATION.POST_ULID, COMM_NOTIFICATION.COMMENT_PATH).in(
                                select(COMM_COMMENT.POST_ULID, COMM_COMMENT.PATH)
                                        .from(COMM_COMMENT)
                                        .where(COMM_COMMENT.AUTH_MEMB_UUID.eq(memberId)))))
                .execute();
    }

    public void updateMemberIdToNullByMemberId(UUID memberId) {
        dsl.batch(
                dsl.update(COMM_POST_ARCHIVE)
                        .setNull(COMM_POST_ARCHIVE.AUTH_MEMB_UUID)
                        .set(COMM_POST_ARCHIVE.UPDATED_AT, LocalDateTime.now())
                        .where(COMM_POST_ARCHIVE.AUTH_MEMB_UUID.eq(memberId)),

                dsl.update(COMM_COMMENT_ARCHIVE)
                        .setNull(COMM_COMMENT_ARCHIVE.AUTH_MEMB_UUID)
                        .set(COMM_COMMENT_ARCHIVE.UPDATED_AT, LocalDateTime.now())
                        .where(COMM_COMMENT_ARCHIVE.AUTH_MEMB_UUID.eq(memberId)),

                dsl.update(PROP_BUG_REP)
                        .setNull(PROP_BUG_REP.MEMB_UUID)
                        .where(PROP_BUG_REP.MEMB_UUID.eq(memberId)),

                dsl.update(PROP_BUG_REP_ARCHIVE)
                        .setNull(PROP_BUG_REP_ARCHIVE.MEMB_UUID)
                        .where(PROP_BUG_REP_ARCHIVE.MEMB_UUID.eq(memberId))
        ).execute();
    }

    public void deleteMemberRelatedRecordsExceptOfPostAndCommentByMemberId(UUID memberId) {
        dsl.batch(
                dsl.deleteFrom(REFRESH_TOKEN)
                        .where(REFRESH_TOKEN.MEMB_UUID.eq(memberId)),

                dsl.deleteFrom(COMM_POST_LIKE)
                        .where(COMM_POST_LIKE.MEMB_UUID.eq(memberId)),

                dsl.deleteFrom(COMM_COMMENT_LIKE)
                        .where(COMM_COMMENT_LIKE.MEMB_UUID.eq(memberId)),

                dsl.deleteFrom(COMM_POST_BOOKMARK)
                        .where(COMM_POST_BOOKMARK.MEMB_UUID.eq(memberId)),

                dsl.deleteFrom(COMM_POST_ABU_REP)
                        .where(COMM_POST_ABU_REP.MEMB_UUID.eq(memberId)),

                dsl.deleteFrom(COMM_COMMENT_ABU_REP)
                        .where(COMM_COMMENT_ABU_REP.MEMB_UUID.eq(memberId)),

                dsl.deleteFrom(SITE_MEMBER_PROF)
                        .where(SITE_MEMBER_PROF.UUID.eq(memberId)),

                dsl.deleteFrom(SITE_MEMBER_TERM)
                        .where(SITE_MEMBER_TERM.UUID.eq(memberId)),

                dsl.deleteFrom(SITE_MEMBER_AUTH)
                        .where(SITE_MEMBER_AUTH.UUID.eq(memberId)),

                dsl.deleteFrom(SITE_MEMBER)
                        .where(SITE_MEMBER.UUID.eq(memberId)),

                dsl.deleteFrom(FCM_TOKEN)
                        .where(FCM_TOKEN.MEMB_UUID.eq(memberId)),

                dsl.deleteFrom(PLANT_SPACE)
                        .where(PLANT_SPACE.MEMBER_UUID.eq(memberId)),

                // plant_info_photo는 FK의 ON DELETE CASCADE로 삭제
                dsl.deleteFrom(PLANT_INFO)
                        .where(PLANT_INFO.MEMBER_UUID.eq(memberId))

        ).execute();
    }
}
