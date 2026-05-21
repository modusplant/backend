package kr.modusplant.domains.member.framework.out.jooq.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static kr.modusplant.jooq.Tables.COMM_POST_LIKE;

@Repository
@RequiredArgsConstructor
public class ActivitySubjectPostJooqRepository {
    private final DSLContext dsl;

    public List<String> getPostIdsLikedByMemberId(UUID memberId) {
        return dsl.select(COMM_POST_LIKE.POST_ULID)
                .from(COMM_POST_LIKE)
                .where(COMM_POST_LIKE.MEMB_UUID.eq(memberId))
                .fetchInto(String.class);
    }
}
