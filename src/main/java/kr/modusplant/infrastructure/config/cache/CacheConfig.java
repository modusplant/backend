package kr.modusplant.infrastructure.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.text.Normalizer;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static kr.modusplant.jooq.Tables.PLANT;
import static kr.modusplant.jooq.Tables.PLANT_VARIETY;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {
    @Bean
    @Primary
    @Qualifier("redisCacheManager")
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(7))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new Jackson2JsonRedisSerializer<>(Object.class))
                );

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(config)
                .build();
    }

    @Bean
    @Qualifier("plantNameCaffeineCacheManager")
    public CacheManager caffeineCacheManager(DSLContext dslContext) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(List.of("transliteratedPlantNamesCache"));
        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .maximumWeight(40000)
                        .weigher((Object key, Object value) -> switch (value) {
                            case List<?> list -> list.size();
                            case Map<?, ?> map -> map.size();
                            case Set<?> set -> set.size();
                            default -> 1;
                        })
                        .softValues()
                        .recordStats());
        cacheManager.setCacheLoader(
                key -> loadAllTransliteratedPlantNamesFromDb(dslContext));

        return cacheManager;
    }

    private List<String> loadAllTransliteratedPlantNamesFromDb(DSLContext dslContext) {
        log.info("[PostgreSQL] Loading all the plant names from the plant and plant_variety tables");
        return dslContext.select(PLANT.KOREAN_NAME)
                .from(PLANT)
                .where(PLANT.KOREAN_NAME.isNotNull())
                .union(dslContext.select(PLANT_VARIETY.VARIETY_NAME).from(PLANT_VARIETY))
                .fetchInto(String.class)
                .stream()
                .map(plantName -> Normalizer.normalize(plantName, Normalizer.Form.NFD))
                .toList();
    }
}