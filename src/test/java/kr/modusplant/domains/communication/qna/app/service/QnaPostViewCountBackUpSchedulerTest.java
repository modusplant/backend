package kr.modusplant.domains.communication.qna.app.service;

import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostRepository;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostViewCountRedisRepository;
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
class QnaPostViewCountBackUpSchedulerTest {
    @Mock
    private QnaPostViewCountRedisRepository viewCountRedisRepository;
    @Mock
    private QnaPostRepository qnaPostRepository;
    @InjectMocks
    private QnaPostViewCountBackUpScheduler viewCountBackUpScheduler;

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
        verify(qnaPostRepository,times(1)).updateViewCount(ulid1,10L);
        verify(qnaPostRepository,times(1)).updateViewCount(ulid2,20L);
        verifyNoMoreInteractions(qnaPostRepository);
    }

}