package kr.modusplant.domains.post.framework.out.jooq.mapper.supers;

import kr.modusplant.domains.post.usecase.record.DraftPostReadModel;
import kr.modusplant.domains.post.usecase.record.PostDetailDataReadModel;
import kr.modusplant.domains.post.usecase.record.PostDetailReadModel;
import kr.modusplant.domains.post.usecase.record.PostSummaryReadModel;
import org.jooq.Record;

public interface PostJooqMapper {

    PostSummaryReadModel toPostSummaryReadModel(Record record);

    PostDetailReadModel toPostDetailReadModel(Record record);

    PostDetailDataReadModel toPostDetailDataReadModel(Record record);

    DraftPostReadModel toDraftPostReadModel(Record record);

}
