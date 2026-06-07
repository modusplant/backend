package kr.modusplant.domains.search.framework.outbound.caffeine;

import com.github.benmanes.caffeine.cache.LoadingCache;
import jakarta.annotation.PostConstruct;
import kr.modusplant.domains.search.usecase.port.cache.SearchPlantCache;
import kr.modusplant.shared.exception.NotFoundValueException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.List;

import static kr.modusplant.shared.exception.enums.GeneralErrorCode.CACHE_NOT_INITIALIZED;

@Component
public class SearchPlantCaffeineCache implements SearchPlantCache {
    private LoadingCache<String, List<String>> plantKoreanNamesCache;
    private final CacheManager cacheManager;
    private final String KOREAN_NAMES_CACHE_NAME = "KOREAN_NAMES";

    public SearchPlantCaffeineCache(@Qualifier("caffeineCacheManager") CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"})
    @PostConstruct
    public void initializeLoadingCache() {
        Cache springCache = cacheManager.getCache("plantKoreanNameCache");
        if (springCache == null) {
            throw new NotFoundValueException(CACHE_NOT_INITIALIZED, "plantKoreanNameCache");
        }
        plantKoreanNamesCache = (LoadingCache<String, List<String>>) springCache.getNativeCache();
        plantKoreanNamesCache.get(KOREAN_NAMES_CACHE_NAME);
    }

    @Override
    public List<String> getKoreanNames() {
        return plantKoreanNamesCache.get(KOREAN_NAMES_CACHE_NAME);
    }
}
