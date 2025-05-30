package kr.modusplant.domains.communication.conversation.app.service;

import kr.modusplant.domains.communication.conversation.persistence.repository.ConvPostRepository;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvPostViewCountRedisRepository;
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
class ConvPostViewCountBackUpSchedulerTest {
    @Mock
    private ConvPostViewCountRedisRepository viewCountRedisRepository;
    @Mock
    private ConvPostRepository convPostRepository;
    @InjectMocks
    private ConvPostViewCountBackUpScheduler viewCountBackUpScheduler;

    private static final UlidIdGenerator generator = new UlidIdGenerator();

    @Test
    @DisplayName("")
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
        verify(convPostRepository,times(1)).updateViewCount(ulid1,10L);
        verify(convPostRepository,times(1)).updateViewCount(ulid2,20L);
        verifyNoMoreInteractions(convPostRepository);
    }

}