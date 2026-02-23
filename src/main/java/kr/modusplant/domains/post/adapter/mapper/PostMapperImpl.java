package kr.modusplant.domains.post.adapter.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.domains.post.usecase.port.mapper.PostMapper;
import kr.modusplant.domains.post.usecase.record.DraftPostReadModel;
import kr.modusplant.domains.post.usecase.record.PostDetailDataReadModel;
import kr.modusplant.domains.post.usecase.record.PostDetailReadModel;
import kr.modusplant.domains.post.usecase.record.PostSummaryReadModel;
import kr.modusplant.domains.post.usecase.response.DraftPostResponse;
import kr.modusplant.domains.post.usecase.response.PostDetailResponse;
import kr.modusplant.domains.post.usecase.response.PostSummaryResponse;
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
    public PostDetailResponse toPostDetailResponse(PostDetailDataReadModel postDetailDataReadModel, JsonNode content) {
        return new PostDetailResponse(
                postDetailDataReadModel.ulid(),
                postDetailDataReadModel.primaryCategoryId(),
                postDetailDataReadModel.primaryCategory(),
                postDetailDataReadModel.secondaryCategoryId(),
                postDetailDataReadModel.secondaryCategory(),
                postDetailDataReadModel.authorUuid(),
                postDetailDataReadModel.nickname(),
                null,
                null,
                null,
                postDetailDataReadModel.title(),
                content,
                postDetailDataReadModel.isPublished(),
                postDetailDataReadModel.publishedAt(),
                postDetailDataReadModel.updatedAt(),
                null,
                null
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
