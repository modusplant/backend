package kr.modusplant.legacy.domains.communication.persistence.repository;

import kr.modusplant.infrastructure.context.RepositoryOnlyContext;
import kr.modusplant.infrastructure.persistence.generator.UlidIdGenerator;
import org.hibernate.generator.EventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;


@RepositoryOnlyContext
class CommPostViewCountRedisRepositoryTest {
    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private CommPostViewCountRedisRepository commPostViewCountRedisRepository;

    private static final UlidIdGenerator generator = new UlidIdGenerator();
    private final String ulid = generator.generate(null,null,null, EventType.INSERT);
    private static final String KEY_FORMAT = "viewCount:comm_post:%s:view_count";

    @Test
    @DisplayName("Redis에 값이 있으면 Long으로 변환하여 반환")
    void readShoudReturnLongWhenValueExistsTest() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(KEY_FORMAT.formatted(ulid))).willReturn("20");

        // when
        Long result = commPostViewCountRedisRepository.read(ulid);

        // then
        assertThat(result).isEqualTo(20L);
    }

    @Test
    @DisplayName("Redis에 값이 없으면 null 반환")
    void readShoudReturnNullWhenValueNotExistTest() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(KEY_FORMAT.formatted(ulid))).willReturn(null);

        // when
        Long result = commPostViewCountRedisRepository.read(ulid);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Redis 조회수 값을 증가시키고 결과 반환")
    void increaseTest() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.increment(KEY_FORMAT.formatted(ulid))).willReturn(10L);

        // when
        Long result = commPostViewCountRedisRepository.increase(ulid);

        // then
        assertThat(result).isEqualTo(10L);
    }

    @Test
    @DisplayName("Redis 조회수 값을 저장하고 결과 반환")
    void writeTest() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);

        // when
        commPostViewCountRedisRepository.write(ulid,20L);

        // then
        then(valueOperations).should().set(KEY_FORMAT.formatted(ulid),"20");
    }

    @Test
    @DisplayName("Redis에서 모든 값을 찾기")
    void findAllTest() {
        // given
        String ulid2 = generator.generate(null,null,null, EventType.INSERT);
        Set<String> keys = Set.of(
                KEY_FORMAT.formatted(ulid),
                KEY_FORMAT.formatted(ulid2)
        );
        when(stringRedisTemplate.keys("viewCount:comm_post:*:view_count")).thenReturn(keys);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(KEY_FORMAT.formatted(ulid))).thenReturn("10");
        when(valueOperations.get(KEY_FORMAT.formatted(ulid2))).thenReturn("20");

        // when
        Map<String, Long> result = commPostViewCountRedisRepository.findAll();

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(ulid)).isEqualTo(10L);
        assertThat(result.get(ulid2)).isEqualTo(20L);
    }
}