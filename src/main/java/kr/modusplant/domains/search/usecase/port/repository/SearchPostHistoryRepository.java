package kr.modusplant.domains.search.usecase.port.repository;

import kr.modusplant.domains.search.domain.vo.SearchKeyword;

import java.util.List;
import java.util.UUID;

public interface SearchPostHistoryRepository {
    void saveSearchKeyword(SearchKeyword keyword, UUID memberId);

    List<String> getSearchHistory(UUID memberId, int size);

    void removeSearchKeyword(SearchKeyword keyword, UUID memberUuid);

    void removeAllSearchHistory(UUID memberId);
}
