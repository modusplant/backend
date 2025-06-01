package kr.modusplant.domains.communication.tip.app.service;

import kr.modusplant.domains.communication.tip.persistence.repository.TipPostRepository;
import kr.modusplant.domains.communication.tip.persistence.repository.TipPostViewCountRedisRepository;
import kr.modusplant.global.persistence.generator.UlidIdGenerator;
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
class TipPostViewCountBackUpSchedulerTest {
    @Mock
    private TipPostViewCountRedisRepository viewCountRedisRepository;
    @Mock
    private TipPostRepository tipPostRepository;
    @InjectMocks
    private TipPostViewCountBackUpScheduler viewCountBackUpScheduler;

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
        verify(tipPostRepository,times(1)).updateViewCount(ulid1,10L);
        verify(tipPostRepository,times(1)).updateViewCount(ulid2,20L);
        verifyNoMoreInteractions(tipPostRepository);
    }

}