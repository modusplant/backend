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
    private LoadingCache<String, List<String>> transliteratedPlantKoreanNamesCache;
    private final CacheManager cacheManager;
    private final String KOREAN_NAMES_CACHE_NAME = "KOREAN_NAMES";

    public SearchPlantCaffeineCache(@Qualifier("plantKoreanNameCaffeineCacheManager") CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"})
    @PostConstruct
    public void initializeLoadingCache() {
        Cache transliteratedCache = cacheManager.getCache("transliteratedPlantKoreanNamesCache");
        if (transliteratedCache == null) {
            throw new NotFoundValueException(CACHE_NOT_INITIALIZED, "transliteratedPlantKoreanNamesCache");
        }
        transliteratedPlantKoreanNamesCache = (LoadingCache<String, List<String>>) transliteratedCache.getNativeCache();
        transliteratedPlantKoreanNamesCache.get(KOREAN_NAMES_CACHE_NAME);
    }

    @Override
    public List<String> getTransliteratedKoreanNames() {
        return transliteratedPlantKoreanNamesCache.get(KOREAN_NAMES_CACHE_NAME);
    }
}
