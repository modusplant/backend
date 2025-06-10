package kr.modusplant.domains.communication.conversation.app.service;

import kr.modusplant.domains.communication.conversation.persistence.repository.ConvPostRepository;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvPostViewCountRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ConvPostViewCountBackUpScheduler {
    private final ConvPostViewCountRedisRepository viewCountRedisRepository;
    private final ConvPostRepository convPostRepository;

    @Transactional
    @Scheduled(fixedRateString = "${scheduler.sync-interval-ms}")
    public void syncRedisViewCountToDatabase() {
        Map<String,Long> viewCounts = viewCountRedisRepository.findAll();

        for (Map.Entry<String, Long> entry : viewCounts.entrySet()) {
            String ulid = entry.getKey();
            Long count = entry.getValue();
            convPostRepository.updateViewCount(ulid,count);
        }
    }
}
