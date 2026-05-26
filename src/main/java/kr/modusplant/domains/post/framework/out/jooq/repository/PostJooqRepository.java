package kr.modusplant.domains.post.framework.out.jooq.repository;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.shared.framework.jooq.converter.JsonbJsonNodeConverter;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static kr.modusplant.jooq.Tables.COMM_POST;

@Repository
@RequiredArgsConstructor
public class PostJooqRepository {
    private final DSLContext dsl;
    private final JsonbJsonNodeConverter jsonbJsonNodeConverter = new JsonbJsonNodeConverter();

    public String[] getPublishedPostUlidsByMemberId(UUID memberId) {
        return dsl.select(COMM_POST.ULID)
                .from(COMM_POST)
                .where(COMM_POST.AUTH_MEMB_UUID.eq(memberId))
                .and(COMM_POST.IS_PUBLISHED.isTrue())
                .fetchInto(String.class).toArray(new String[0]);
    }

    public List<JsonNode> getPostContentsFromPublishedPostUlids(String[] publishedPostUlids) {
        return dsl.select(COMM_POST.CONTENT)
                .from(COMM_POST)
                .where(COMM_POST.ULID.in(publishedPostUlids))
                .fetchInto(JSONB.class)
                .stream()
                .map(jsonbJsonNodeConverter::from)
                .toList();
    }
}
