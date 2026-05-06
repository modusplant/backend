package kr.modusplant.domains.search.adapter.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.domains.search.usecase.model.read.SearchPostReadModel;
import kr.modusplant.domains.search.usecase.port.mapper.SearchMapper;
import kr.modusplant.domains.search.usecase.response.SearchPostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchMapperImpl implements SearchMapper {
    @Override
    public SearchPostResponse toSearchPostResponse(
            SearchPostReadModel readModel, JsonNode content) {
        return new SearchPostResponse(
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
}
