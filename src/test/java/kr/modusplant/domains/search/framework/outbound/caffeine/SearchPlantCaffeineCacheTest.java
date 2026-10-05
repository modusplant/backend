package kr.modusplant.domains.search.framework.outbound.caffeine;

import com.github.benmanes.caffeine.cache.LoadingCache;
import kr.modusplant.shared.exception.NotFoundValueException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.List;

import static kr.modusplant.domains.search.common.constant.SearchStringConstant.TEST_SEARCH_PLANT_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

class SearchPlantCaffeineCacheTest {

    private final CacheManager cacheManager = Mockito.mock(CacheManager.class);
    private final Cache springCacheWrapper = Mockito.mock(Cache.class);
    @SuppressWarnings("unchecked")
    private final LoadingCache<String, List<String>> loadingCache = Mockito.mock(LoadingCache.class);
    private final SearchPlantCaffeineCache searchPlantCaffeineCache = new SearchPlantCaffeineCache(cacheManager);

    @Test
    @DisplayName("캐시가 존재할 때 initializeLoadingCache 호출 시 캐시 초기화 및 워밍업 처리")
    void testInitializeLoadingCache_givenExistingCache_willProcessAction() {
        // given
        given(cacheManager.getCache("transliteratedPlantNamesCache")).willReturn(springCacheWrapper);
        given(springCacheWrapper.getNativeCache()).willReturn(loadingCache);

        // when
        searchPlantCaffeineCache.initializeLoadingCache();

        // then
        verify(loadingCache).get("PLANT_NAMES");
    }

    @Test
    @DisplayName("캐시가 null일 때 initializeLoadingCache 호출 시 예외 발생")
    void testInitializeLoadingCache_givenNullCache_willThrowException() {
        // given
        given(cacheManager.getCache("transliteratedPlantNamesCache")).willReturn(null);

        // when
        NotFoundValueException exception = assertThrows(NotFoundValueException.class,
                searchPlantCaffeineCache::initializeLoadingCache);

        // then
        assertThat(exception.getErrorCode()).isEqualTo(GeneralErrorCode.CACHE_NOT_INITIALIZED);
    }

    @Test
    @DisplayName("캐시 초기화 후 getTransliteratedPlantNames 호출 시 식물명 목록 반환")
    void testGetTransliteratedPlantNames_givenInitializedCache_willReturnPlantNameList() {
        // given
        given(cacheManager.getCache("transliteratedPlantNamesCache")).willReturn(springCacheWrapper);
        given(springCacheWrapper.getNativeCache()).willReturn(loadingCache);
        given(loadingCache.get("PLANT_NAMES")).willReturn(List.of(TEST_SEARCH_PLANT_NAME));
        searchPlantCaffeineCache.initializeLoadingCache();

        // when
        List<String> result = searchPlantCaffeineCache.getTransliteratedPlantNames();

        // then
        assertThat(result).containsExactly(TEST_SEARCH_PLANT_NAME);
    }
}
