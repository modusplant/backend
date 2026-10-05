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
    private LoadingCache<String, List<String>> transliteratedPlantNamesCache;
    private final CacheManager cacheManager;
    private final String PLANT_NAMES_CACHE_NAME = "PLANT_NAMES";

    public SearchPlantCaffeineCache(@Qualifier("plantNameCaffeineCacheManager") CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @SuppressWarnings({"unchecked"})
    @PostConstruct
    public void initializeLoadingCache() {
        Cache transliteratedCache = cacheManager.getCache("transliteratedPlantNamesCache");
        if (transliteratedCache == null) {
            throw new NotFoundValueException(CACHE_NOT_INITIALIZED, "transliteratedPlantNamesCache");
        }
        transliteratedPlantNamesCache = (LoadingCache<String, List<String>>) transliteratedCache.getNativeCache();
        transliteratedPlantNamesCache.get(PLANT_NAMES_CACHE_NAME);
    }

    @Override
    public List<String> getTransliteratedPlantNames() {
        return transliteratedPlantNamesCache.get(PLANT_NAMES_CACHE_NAME);
    }
}
