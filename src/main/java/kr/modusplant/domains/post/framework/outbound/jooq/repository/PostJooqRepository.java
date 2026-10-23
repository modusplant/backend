package kr.modusplant.domains.post.framework.outbound.jooq.repository;

import kr.modusplant.domains.post.framework.outbound.jooq.record.PostIsPublishedAndContentRecord;
import kr.modusplant.shared.framework.jooq.converter.JsonbJsonNodeConverter;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static kr.modusplant.jooq.Tables.COMM_POST;

@Repository
@RequiredArgsConstructor
public class PostJooqRepository {
    private final DSLContext dsl;
    private final JsonbJsonNodeConverter jsonbJsonNodeConverter = new JsonbJsonNodeConverter();

    public List<PostIsPublishedAndContentRecord> getPostIsPublishedAndContentsByMemberId(UUID memberId) {
        return dsl.select(COMM_POST.ULID, COMM_POST.IS_PUBLISHED, COMM_POST.CONTENT)
                .from(COMM_POST)
                .where(COMM_POST.AUTH_MEMB_UUID.eq(memberId))
                .fetch(postRecord -> new PostIsPublishedAndContentRecord(
                        postRecord.get(COMM_POST.ULID),
                        postRecord.get(COMM_POST.IS_PUBLISHED),
                        jsonbJsonNodeConverter.from(postRecord.get(COMM_POST.CONTENT))));
    }
}
