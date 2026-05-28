package kr.modusplant.domains.member.framework.outbound.jooq.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.jooq.Tables.*;

@Repository
@RequiredArgsConstructor
public class MemberJooqRepository {
    private final DSLContext dsl;

    public void updateMemberIdToNullByMemberId(UUID memberId) {
        dsl.batch(
                dsl.update(COMM_POST_ARCHIVE)
                        .setNull(COMM_POST_ARCHIVE.AUTH_MEMB_UUID)
                        .set(COMM_POST_ARCHIVE.UPDATED_AT, LocalDateTime.now())
                        .where(COMM_POST_ARCHIVE.AUTH_MEMB_UUID.eq(memberId)),

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
                        .where(FCM_TOKEN.MEMB_UUID.eq(memberId))

        ).execute();
    }
}
