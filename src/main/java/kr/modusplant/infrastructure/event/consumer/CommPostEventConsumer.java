package kr.modusplant.infrastructure.event.consumer;

import kr.modusplant.framework.out.persistence.jpa.entity.CommLikeEntity;
import kr.modusplant.framework.out.persistence.jpa.repository.CommLikeRepository;
import kr.modusplant.infrastructure.event.bus.EventBus;
import kr.modusplant.shared.event.CommPostLikeEvent;
import kr.modusplant.shared.event.CommPostUnlikeEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CommPostEventConsumer {
    private final CommLikeRepository commLikeRepository;

    public CommPostEventConsumer(EventBus eventBus, CommLikeRepository commLikeRepository) {
        eventBus.subscribe(event -> {
            if (event instanceof CommPostLikeEvent commPostLikeEvent) {
                putCommPostLike(commPostLikeEvent.getMemberId(), commPostLikeEvent.getPostId());
            }
        });
        eventBus.subscribe(event -> {
            if (event instanceof CommPostUnlikeEvent commPostUnlikeEvent) {
                deleteCommPostLike(commPostUnlikeEvent.getMemberId(), commPostUnlikeEvent.getPostId());
            }
        });
        this.commLikeRepository = commLikeRepository;
    }

    private void putCommPostLike(UUID memberId, String postId) {
        commLikeRepository.save(CommLikeEntity.of(postId, memberId));
    }

    private void deleteCommPostLike(UUID memberId, String postId) {
        commLikeRepository.delete(CommLikeEntity.of(postId, memberId));
    }
}
