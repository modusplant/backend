package kr.modusplant.domains.post.adapter.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.domains.post.usecase.model.PostDetailReadModel;
import kr.modusplant.domains.post.usecase.model.PostSummaryReadModel;
import kr.modusplant.domains.post.usecase.port.mapper.PostMapper;
import kr.modusplant.domains.post.usecase.response.PostDetailResponse;
import kr.modusplant.domains.post.usecase.response.PostSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostMapperImpl implements PostMapper {

    @Override
    public PostDetailResponse toPostDetailResponse(PostDetailReadModel postDetailReadModel, JsonNode content, Long viewCount) {
        return new PostDetailResponse(
                postDetailReadModel.ulid(),
                postDetailReadModel.primaryCategoryUuid(),
                postDetailReadModel.primaryCategory(),
                postDetailReadModel.secondaryCategoryUuid(),
                postDetailReadModel.secondaryCategory(),
                postDetailReadModel.authorUuid(),
                postDetailReadModel.nickname(),
                postDetailReadModel.likeCount(),
                viewCount==null ? 0 : viewCount,
                postDetailReadModel.title(),
                content,
                postDetailReadModel.isPublished(),
                postDetailReadModel.publishedAt()
        );
    }

    @Override
    public PostSummaryResponse toPostSummaryResponse(PostSummaryReadModel postSummaryReadModel, JsonNode content) {
        return new PostSummaryResponse(
                postSummaryReadModel.ulid(),
                postSummaryReadModel.primaryCategory(),
                postSummaryReadModel.secondaryCategory(),
                postSummaryReadModel.nickname(),
                postSummaryReadModel.title(),
                content,
                postSummaryReadModel.publishedAt()
        );
    }
}
