package kr.modusplant.infrastructure.event.consumer;

import kr.modusplant.framework.out.persistence.jpa.entity.CommLikeEntity;
import kr.modusplant.framework.out.persistence.jpa.repository.CommLikeRepository;
import kr.modusplant.infrastructure.event.bus.EventBus;
import kr.modusplant.shared.event.PostLikeEvent;
import kr.modusplant.shared.event.PostUnlikeEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PostEventConsumer {
    private final CommLikeRepository commLikeRepository;

    public PostEventConsumer(EventBus eventBus, CommLikeRepository commLikeRepository) {
        eventBus.subscribe(event -> {
            if (event instanceof PostLikeEvent postLikeEvent) {
                putCommPostLike(postLikeEvent.getMemberId(), postLikeEvent.getPostId());
            }
        });
        eventBus.subscribe(event -> {
            if (event instanceof PostUnlikeEvent postUnlikeEvent) {
                deleteCommPostLike(postUnlikeEvent.getMemberId(), postUnlikeEvent.getPostId());
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
