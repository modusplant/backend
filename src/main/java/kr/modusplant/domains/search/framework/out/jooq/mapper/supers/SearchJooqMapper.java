package kr.modusplant.domains.search.framework.out.jooq.mapper.supers;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.domains.search.usecase.model.read.SearchPostReadModel;
import org.jooq.Record;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static kr.modusplant.jooq.Tables.COMM_POST;
import static kr.modusplant.jooq.Tables.SITE_MEMBER;

@Component
public class SearchJooqMapper {
    public SearchPostReadModel toSearchPostReadModel(Record record) {
        return new SearchPostReadModel(
                record.get(COMM_POST.ULID),
                record.get("primaryCategory", String.class),
                record.get("secondaryCategory", String.class),
                record.get(SITE_MEMBER.NICKNAME),
                record.get(COMM_POST.TITLE),
                record.get("content", JsonNode.class),
                record.get("thumbnailPath", String.class),
                record.get("likeCount", Integer.class),
                record.get("publishedAt", LocalDateTime.class),
                record.get("commentCount", Integer.class),
                record.get("isLiked", Boolean.class),
                record.get("isBookmarked", Boolean.class),
                record.get("importance", Integer.class),
                record.get("maxWordSimilarity", Double.class)
        );
    }
}
