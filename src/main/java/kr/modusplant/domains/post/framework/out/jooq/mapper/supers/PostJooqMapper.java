package kr.modusplant.domains.post.framework.out.jooq.mapper.supers;

import kr.modusplant.domains.post.usecase.record.*;
import org.jooq.Record;

public interface PostJooqMapper {

    PostSummaryReadModel toPostSummaryReadModel(Record record);

    PostSummaryWithSearchInfoReadModel toPostSummaryWithSearchInfoReadModel(Record record);

    PostDetailReadModel toPostDetailReadModel(Record record);

    PostDetailDataReadModel toPostDetailDataReadModel(Record record);

    DraftPostReadModel toDraftPostReadModel(Record record);

}
