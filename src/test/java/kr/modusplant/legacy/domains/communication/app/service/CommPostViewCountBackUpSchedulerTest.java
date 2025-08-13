package kr.modusplant.legacy.domains.communication.app.service;

import kr.modusplant.framework.outbound.persistence.generator.UlidIdGenerator;
import kr.modusplant.legacy.domains.communication.persistence.repository.CommPostRepository;
import kr.modusplant.legacy.domains.communication.persistence.repository.CommPostViewCountRedisRepository;
import org.hibernate.generator.EventType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommPostViewCountBackUpSchedulerTest {
    @Mock
    private CommPostViewCountRedisRepository viewCountRedisRepository;
    @Mock
    private CommPostRepository commPostRepository;
    @InjectMocks
    private CommPostViewCountBackUpScheduler viewCountBackUpScheduler;

    private static final UlidIdGenerator generator = new UlidIdGenerator();

    @Test
    @DisplayName("Redis 조회 수를 DB와 동기화")
    void syncRedisViewCountToDatabaseTest() {
        // given
        String ulid1 = generator.generate(null,null,null, EventType.INSERT);
        String ulid2 = generator.generate(null,null,null, EventType.INSERT);
        Map<String, Long> viewCountMap = new HashMap<>();
        viewCountMap.put(ulid1,10L);
        viewCountMap.put(ulid2,20L);
        when(viewCountRedisRepository.findAll()).thenReturn(viewCountMap);

        // when
        viewCountBackUpScheduler.syncRedisViewCountToDatabase();

        // then
        verify(viewCountRedisRepository,times(1)).findAll();
        verify(commPostRepository,times(1)).updateViewCount(ulid1,10L);
        verify(commPostRepository,times(1)).updateViewCount(ulid2,20L);
        verifyNoMoreInteractions(commPostRepository);
    }

}