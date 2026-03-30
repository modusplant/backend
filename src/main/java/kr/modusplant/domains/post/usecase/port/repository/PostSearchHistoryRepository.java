package kr.modusplant.domains.post.usecase.port.repository;

import java.util.List;
import java.util.UUID;

public interface PostSearchHistoryRepository {
    void saveSearchKeyword(UUID memberUuid, String keyword);

    List<String> getSearchHistory(UUID memberUuid, int size);

    void removeSearchKeyword(UUID memberUuid, String keyword);

    void removeAllSearchHistory(UUID memberUuid);
}
