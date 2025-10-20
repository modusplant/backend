package kr.modusplant.legacy.domains.communication.app.service;

import kr.modusplant.framework.out.jpa.repository.CommPostJpaRepository;
import kr.modusplant.legacy.domains.communication.persistence.repository.CommPostViewCountRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CommPostViewCountBackUpScheduler {
    private final CommPostViewCountRedisRepository viewCountRedisRepository;
    private final CommPostJpaRepository commPostRepository;

    @Transactional
    @Scheduled(fixedRateString = "${scheduler.sync-interval-ms}")
    public void syncRedisViewCountToDatabase() {
        Map<String,Long> viewCounts = viewCountRedisRepository.findAll();

        for (Map.Entry<String, Long> entry : viewCounts.entrySet()) {
            String ulid = entry.getKey();
            Long count = entry.getValue();
            commPostRepository.updateViewCount(ulid,count);
        }
    }
}
