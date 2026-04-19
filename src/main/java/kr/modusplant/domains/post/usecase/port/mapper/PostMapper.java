package kr.modusplant.domains.post.usecase.port.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.domains.post.usecase.record.*;
import kr.modusplant.domains.post.usecase.response.*;

public interface PostMapper {
    PostDetailResponse toPostDetailResponse(PostDetailReadModel postDetailReadModel, String profileImageUrl, JsonNode content, Long viewCount);

    PostDetailDataResponse toPostDetailDataResponse(PostDetailDataReadModel postDetailDataReadModel, JsonNode content, String thumbnailFilename);

    PostSummaryResponse toPostSummaryResponse(PostSummaryReadModel postSummaryReadModel, JsonNode content);

    PostSummaryWithSearchInfoResponse toPostSummaryWithSearchInfoResponse(PostSummaryWithSearchInfoReadModel postSummaryWithSearchInfoReadModel, JsonNode content);

    DraftPostResponse toDraftPostResponse(DraftPostReadModel draftPostReadModel, JsonNode content);
}
