package kr.modusplant.domains.post.adapter.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.domains.post.usecase.port.mapper.PostMapper;
import kr.modusplant.domains.post.usecase.record.*;
import kr.modusplant.domains.post.usecase.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostMapperImpl implements PostMapper {

    @Override
    public PostDetailResponse toPostDetailResponse(PostDetailReadModel postDetailReadModel, String profileImageUrl, JsonNode content, Long viewCount) {
        return new PostDetailResponse(
                postDetailReadModel.ulid(),
                postDetailReadModel.primaryCategoryId(),
                postDetailReadModel.primaryCategory(),
                postDetailReadModel.secondaryCategoryId(),
                postDetailReadModel.secondaryCategory(),
                postDetailReadModel.authorUuid(),
                postDetailReadModel.nickname(),
                profileImageUrl,
                postDetailReadModel.likeCount(),
                viewCount==null ? 0 : viewCount,
                postDetailReadModel.title(),
                content,
                postDetailReadModel.isPublished(),
                postDetailReadModel.publishedAt(),
                postDetailReadModel.updatedAt(),
                postDetailReadModel.isLiked(),
                postDetailReadModel.isBookmarked()
        );
    }

    @Override
    public PostDetailDataResponse toPostDetailDataResponse(PostDetailDataReadModel postDetailDataReadModel, JsonNode content, String thumbnailFilename) {
        return new PostDetailDataResponse(
                postDetailDataReadModel.ulid(),
                postDetailDataReadModel.primaryCategoryId(),
                postDetailDataReadModel.primaryCategory(),
                postDetailDataReadModel.secondaryCategoryId(),
                postDetailDataReadModel.secondaryCategory(),
                postDetailDataReadModel.authorUuid(),
                postDetailDataReadModel.nickname(),
                postDetailDataReadModel.title(),
                content,
                thumbnailFilename,
                postDetailDataReadModel.isPublished(),
                postDetailDataReadModel.publishedAt(),
                postDetailDataReadModel.updatedAt()
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
                postSummaryReadModel.likeCount(),
                postSummaryReadModel.publishedAt(),
                postSummaryReadModel.commentCount(),
                postSummaryReadModel.isLiked(),
                postSummaryReadModel.isBookmarked()
        );
    }

    @Override
    public PostSummaryWithSearchInfoResponse toPostSummaryWithSearchInfoResponse(
            PostSummaryWithSearchInfoReadModel readModel, JsonNode content) {
        return new PostSummaryWithSearchInfoResponse(
                readModel.ulid(),
                readModel.primaryCategory(),
                readModel.secondaryCategory(),
                readModel.nickname(),
                readModel.title(),
                content,
                readModel.likeCount(),
                readModel.publishedAt(),
                readModel.commentCount(),
                readModel.isLiked(),
                readModel.isBookmarked(),
                readModel.importance(),
                readModel.maxWordSimilarity()
        );
    }

    @Override
    public DraftPostResponse toDraftPostResponse(DraftPostReadModel draftPostReadModel, JsonNode content) {
        return new DraftPostResponse(
                draftPostReadModel.ulid(),
                draftPostReadModel.primaryCategory(),
                draftPostReadModel.secondaryCategory(),
                draftPostReadModel.title(),
                content,
                draftPostReadModel.updatedAt()
        );
    }
}
