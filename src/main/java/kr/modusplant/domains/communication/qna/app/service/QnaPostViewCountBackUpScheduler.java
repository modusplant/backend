package kr.modusplant.domains.communication.qna.app.service;

import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostRepository;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostViewCountRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class QnaPostViewCountBackUpScheduler {
    private final QnaPostViewCountRedisRepository viewCountRedisRepository;
    private final QnaPostRepository qnaPostRepository;

    @Transactional
    @Scheduled(fixedRateString = "${scheduler.sync-interval-ms}")
    public void syncRedisViewCountToDatabase() {
        Map<String,Long> viewCounts = viewCountRedisRepository.findAll();

        for (Map.Entry<String, Long> entry : viewCounts.entrySet()) {
            String ulid = entry.getKey();
            Long count = entry.getValue();
            qnaPostRepository.updateViewCount(ulid,count);
        }
    }
}
