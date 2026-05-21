package kr.modusplant.domains.member.framework.out.jooq.repository;

import kr.modusplant.domains.member.framework.out.jooq.record.ActivitySubjectCommentIdRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static kr.modusplant.jooq.Tables.COMM_COMMENT_LIKE;

@Repository
@RequiredArgsConstructor
public class ActivitySubjectCommentJooqRepository {
    private final DSLContext dsl;

    public List<ActivitySubjectCommentIdRecord> getCommentIdsThatHaveCommentLikedByMemberId(UUID memberId) {
        return dsl.select(COMM_COMMENT_LIKE.POST_ULID, COMM_COMMENT_LIKE.PATH)
                .from(COMM_COMMENT_LIKE)
                .where(COMM_COMMENT_LIKE.MEMB_UUID.eq(memberId))
                .fetchInto(ActivitySubjectCommentIdRecord.class);
    }
}
