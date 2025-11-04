package kr.modusplant.domains.post.framework.out.redis;

import kr.modusplant.domains.post.common.util.domain.vo.PostIdTestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static kr.modusplant.domains.post.common.constant.PostUlidConstant.TEST_POST_ULID;
import static kr.modusplant.shared.persistence.common.util.constant.SiteMemberConstant.MEMBER_BASIC_USER_UUID;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PostViewLockRedisRepositoryTest implements PostIdTestUtils {
    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private PostViewLockRedisRepository postViewLockRedisRepository;

    private static final String KEY_FORMAT = "viewCount:comm_post:%s:member:%s:lock";

    @Test
    @DisplayName("Redis에 TTL 동안 락이 존재하면 조회수 증가 없이 true를 반환한다")
    void testLock_givenPostIdAndMemberUuidAndTtlWithExistingLock_willReturnTrue() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).willReturn(true);

        // when
        boolean result = postViewLockRedisRepository.lock(testPostId,MEMBER_BASIC_USER_UUID,10);

        // then
        assertThat(result).isEqualTo(true);
        then(stringRedisTemplate).should().opsForValue();
        then(valueOperations).should().setIfAbsent(String.format(KEY_FORMAT, TEST_POST_ULID, MEMBER_BASIC_USER_UUID),"",Duration.ofMinutes(10));
    }

    @Test
    @DisplayName("Redis에 TTL 동안 락이 존재하지 않으면 false를 반환한다")
    void testLock_givenPostIdAndMemberUuidAndTtlWithoutExistingLock_willReturnFalse() {
        // given
        given(stringRedisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).willReturn(false);

        // when
        boolean result = postViewLockRedisRepository.lock(testPostId,MEMBER_BASIC_USER_UUID,10);

        // then
        assertThat(result).isEqualTo(false);
    }

}