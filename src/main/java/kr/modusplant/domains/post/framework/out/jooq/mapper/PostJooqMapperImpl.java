package kr.modusplant.domains.post.framework.out.jooq.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.domains.post.framework.out.jooq.mapper.supers.PostJooqMapper;
import kr.modusplant.domains.post.usecase.record.DraftPostReadModel;
import kr.modusplant.domains.post.usecase.record.PostDetailDataReadModel;
import kr.modusplant.domains.post.usecase.record.PostDetailReadModel;
import kr.modusplant.domains.post.usecase.record.PostSummaryReadModel;
import org.jooq.Record;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static kr.modusplant.jooq.Tables.*;
import static kr.modusplant.jooq.Tables.COMM_POST;

@Component
public class PostJooqMapperImpl implements PostJooqMapper {

    @Override
    public PostSummaryReadModel toPostSummaryReadModel(Record record) {
        return new PostSummaryReadModel(
                record.get(COMM_POST.ULID),
                record.get("primaryCategory", String.class),
                record.get("secondaryCategory", String.class),
                record.get(SITE_MEMBER.NICKNAME),
                record.get(COMM_POST.TITLE),
                record.get("content", JsonNode.class),
                record.get(COMM_POST.LIKE_COUNT),
                record.get(COMM_POST.PUBLISHED_AT),
                record.get("commentCount", Integer.class),
                record.get("isLiked", Boolean.class),
                record.get("isBookmarked", Boolean.class)
        );
    }

    @Override
    public PostDetailReadModel toPostDetailReadModel(Record record) {
        return new PostDetailReadModel(
                record.get(COMM_POST.ULID),
                record.get("primaryCategoryUuid", UUID.class),
                record.get("primaryCategory", String.class),
                record.get("secondaryCategoryUuid", UUID.class),
                record.get("secondaryCategory", String.class),
                record.get("authorUuid", UUID.class),
                record.get(SITE_MEMBER.NICKNAME),
                record.get(COMM_POST.TITLE),
                record.get("content", JsonNode.class),
                record.get(COMM_POST.LIKE_COUNT),
                record.get(COMM_POST.IS_PUBLISHED),
                record.get(COMM_POST.PUBLISHED_AT),
                record.get(COMM_POST.UPDATED_AT),
                record.get("isLiked", Boolean.class),
                record.get("isBookmarked", Boolean.class)
        );
    }

    @Override
    public PostDetailDataReadModel toPostDetailDataReadModel(Record record) {
        return new PostDetailDataReadModel(
                record.get(COMM_POST.ULID),
                record.get("primaryCategoryUuid", UUID.class),
                record.get("primaryCategory", String.class),
                record.get("secondaryCategoryUuid", UUID.class),
                record.get("secondaryCategory", String.class),
                record.get("authorUuid", UUID.class),
                record.get(SITE_MEMBER.NICKNAME),
                record.get(COMM_POST.TITLE),
                record.get("content", JsonNode.class),
                record.get(COMM_POST.IS_PUBLISHED),
                record.get(COMM_POST.PUBLISHED_AT),
                record.get(COMM_POST.UPDATED_AT)
        );
    }

    @Override
    public DraftPostReadModel toDraftPostReadModel(Record record) {
        return new DraftPostReadModel(
                record.get(COMM_POST.ULID),
                record.get("primaryCategory", String.class),
                record.get("secondaryCategory", String.class),
                record.get(COMM_POST.TITLE),
                record.get("content", JsonNode.class),
                record.get(COMM_POST.UPDATED_AT)
        );
    }
}
