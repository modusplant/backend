package kr.modusplant.domains.post.usecase.port.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.domains.post.usecase.model.PostDetailReadModel;
import kr.modusplant.domains.post.usecase.model.PostSummaryReadModel;
import kr.modusplant.domains.post.usecase.response.PostDetailResponse;
import kr.modusplant.domains.post.usecase.response.PostSummaryResponse;

public interface PostMapper {
    PostDetailResponse toPostDetailResponse(PostDetailReadModel postDetailReadModel, JsonNode content, Long viewCount);

    PostSummaryResponse toPostSummaryResponse(PostSummaryReadModel postSummaryReadModel, JsonNode content);
}
