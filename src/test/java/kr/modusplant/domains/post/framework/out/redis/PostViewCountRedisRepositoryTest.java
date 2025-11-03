package kr.modusplant.domains.post.framework.out.redis;

import kr.modusplant.domains.post.common.util.domain.vo.PostIdTestUtils;
import kr.modusplant.infrastructure.persistence.generator.UlidIdGenerator;
import org.hibernate.generator.EventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Map;
import java.util.Set;

import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PostViewCountRedisRepositoryTest implements PostIdTestUtils {
    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private PostViewCountRedisRepository postViewCountRedisRepository;

    private static final String KEY_FORMAT = "viewCount:comm_post:%s:view_count";

    @Test
    @DisplayName("Redis에 값이 있으면 Long으로 변환하여 반환")
    void testRead_PostIdWithExistingRedisValue_willReturnLongValue() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(KEY_FORMAT.formatted(TEST_POST_ULID))).willReturn("20");

        // when
        Long result = postViewCountRedisRepository.read(testPostId);

        // then
        assertThat(result).isEqualTo(20L);
    }

    @Test
    @DisplayName("Redis에 값이 없으면 null 반환")
    void testRead_givenPostIdWithoutRedisValue_willReturnNull() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(KEY_FORMAT.formatted(TEST_POST_ULID))).willReturn(null);

        // when
        Long result = postViewCountRedisRepository.read(testPostId);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Redis 조회수 값을 증가시키고 결과 반환")
    void testIncrease_givenPostIdAndViewCount_willIncreaseAndReturnNewViewCount() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.increment(KEY_FORMAT.formatted(TEST_POST_ULID))).willReturn(10L);

        // when
        Long result = postViewCountRedisRepository.increase(testPostId);

        // then
        assertThat(result).isEqualTo(10L);
    }

    @Test
    @DisplayName("Redis 조회수 값을 저장하고 결과 반환")
    void testWrite_givenPostIdAndViewCount_willSaveAndReturnViewCount() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);

        // when
        postViewCountRedisRepository.write(testPostId,20L);

        // then
        then(valueOperations).should().set(KEY_FORMAT.formatted(TEST_POST_ULID),"20");
    }

    @Test
    @DisplayName("Redis에서 모든 값을 찾기")
    void testFindAll_givenNothing_willReturnViewCountMap() {
        // given
        UlidIdGenerator generator = new UlidIdGenerator();
        String ulid = generator.generate(null,null,null, EventType.INSERT);
        Set<String> keys = Set.of(
                KEY_FORMAT.formatted(TEST_POST_ULID),
                KEY_FORMAT.formatted(ulid)
        );
        given(stringRedisTemplate.keys("viewCount:comm_post:*:view_count")).willReturn(keys);
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.get(KEY_FORMAT.formatted(TEST_POST_ULID))).willReturn("10");
        given(valueOperations.get(KEY_FORMAT.formatted(ulid))).willReturn("20");

        // when
        Map<String, Long> result = postViewCountRedisRepository.findAll();

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(TEST_POST_ULID)).isEqualTo(10L);
        assertThat(result.get(ulid)).isEqualTo(20L);
    }

}