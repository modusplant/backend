package kr.modusplant.infrastructure.event.consumer;

import kr.modusplant.framework.out.persistence.jpa.entity.CommLikeEntity;
import kr.modusplant.framework.out.persistence.jpa.repository.CommLikeRepository;
import kr.modusplant.infrastructure.event.CommPostLikedEvent;
import kr.modusplant.infrastructure.event.bus.EventBus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CommPostEventConsumer {
    private final CommLikeRepository commLikeRepository;

    public CommPostEventConsumer(EventBus eventBus, CommLikeRepository commLikeRepository) {
        eventBus.subscribe(event -> {
            if (event instanceof CommPostLikedEvent commPostLikedEvent) {
                updatePostLike(commPostLikedEvent.memberId(), commPostLikedEvent.postId());
            }
        });
        this.commLikeRepository = commLikeRepository;
    }

    private void updatePostLike(UUID memberId, String postId) {
        commLikeRepository.save(CommLikeEntity.of(postId, memberId));
    }
}
