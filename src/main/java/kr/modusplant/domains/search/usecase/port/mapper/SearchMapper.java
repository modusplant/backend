package kr.modusplant.domains.search.usecase.port.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.domains.search.usecase.model.read.SearchPostReadModel;
import kr.modusplant.domains.search.usecase.response.SearchPostResponse;

public interface SearchMapper {
    SearchPostResponse toSearchPostResponse(SearchPostReadModel searchPostReadModel, JsonNode content);
}
