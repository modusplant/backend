package kr.modusplant.domains.communication.tip.persistence.repository;

import kr.modusplant.global.context.RepositoryOnlyContext;
import kr.modusplant.global.persistence.generator.UlidIdGenerator;
import org.hibernate.generator.EventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@RepositoryOnlyContext
class TipPostViewLockRedisRepositoryTest {
    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private TipPostViewLockRedisRepository tipPostViewLockRedisRepository;

    private static final UlidIdGenerator generator = new UlidIdGenerator();
    private final String ulid = generator.generate(null,null,null, EventType.INSERT);
    private final UUID memberUuid = UUID.randomUUID();
    private static final String KEY_FORMAT = "viewCount:tip_post:%s:member:%s:lock";


    @Test
    @DisplayName("Redis에 TTL 동안 락이 존재하면 조회수 증가 없이 true를 반환한다")
    void lockShouldReturnTrueWhenLockExists() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).willReturn(true);

        // when
        boolean result = tipPostViewLockRedisRepository.lock(ulid,memberUuid,10);

        // then
        assertThat(result).isEqualTo(true);
        then(stringRedisTemplate).should().opsForValue();
        then(valueOperations).should().setIfAbsent(String.format(KEY_FORMAT, ulid, memberUuid),"",Duration.ofMinutes(10));
    }

    @Test
    @DisplayName("Redis에 TTL 동안 락이 존재하면 조회수 증가 없이 true를 반환한다")
    void lockShouldReturnFalseWhenLockNotExist() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).willReturn(false);

        // when
        boolean result = tipPostViewLockRedisRepository.lock(ulid,memberUuid,10);

        // then
        assertThat(result).isEqualTo(false);
    }

}