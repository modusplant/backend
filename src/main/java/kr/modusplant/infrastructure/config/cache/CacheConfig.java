package kr.modusplant.infrastructure.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.jooq.DSLContext;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.List;

import static kr.modusplant.jooq.Tables.PLANT;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean(name = "redisCacheManager")
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

    @Bean(name = "plantKoreanNameCacheManager")
    public CacheManager caffeineCacheManager(DSLContext dslContext) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(List.of("plantKoreanNameCache"));
        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .initialCapacity(15000)
                        .maximumSize(20000)
                        .softValues()
                        .recordStats());
        cacheManager.setCacheLoader(
                key -> loadAllPlantNamesFromDb(dslContext));

        return cacheManager;
    }

    private List<String> loadAllPlantNamesFromDb(DSLContext dslContext) {
        return dslContext.select(PLANT.KOR_NAME)
                .from(PLANT)
                .where(PLANT.KOR_NAME.isNotNull())
                .fetchInto(String.class);
    }
}