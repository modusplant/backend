package kr.modusplant.domains.tip.app.service;

import kr.modusplant.domains.tip.persistence.repository.TipPostRepository;
import kr.modusplant.domains.tip.persistence.repository.TipPostViewCountRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class TipPostViewCountBackUpScheduler {
    private final TipPostViewCountRedisRepository viewCountRedisRepository;
    private final TipPostRepository tipPostRepository;

    @Transactional
    @Scheduled(fixedRateString = "${scheduler.sync-interval-ms}")
    public void syncRedisViewCountToDatabase() {
        Map<String,Long> viewCounts = viewCountRedisRepository.findAll();

        for (Map.Entry<String, Long> entry : viewCounts.entrySet()) {
            String ulid = entry.getKey();
            Long count = entry.getValue();
            tipPostRepository.updateViewCount(ulid,count);
        }
    }
}
