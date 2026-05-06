package kr.modusplant.domains.post.framework.out.scheduler;

import kr.modusplant.domains.post.framework.out.redis.PostViewCountRedisRepository;
import kr.modusplant.framework.jpa.repository.CommPostJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PostViewCountBackUpScheduler {
    private final PostViewCountRedisRepository postViewCountRedisRepository;
    private final CommPostJpaRepository postJpaRepository;


    @Transactional
    @Scheduled(fixedRateString = "${scheduler.sync-interval-ms}")
    public void syncRedisViewCountToDatabase() {
        Map<String,Long> viewCounts = postViewCountRedisRepository.findAll();

        for (Map.Entry<String, Long> entry : viewCounts.entrySet()) {
            String ulid = entry.getKey();
            Long count = entry.getValue();
            postJpaRepository.updateViewCount(ulid,count);
        }
    }
}
