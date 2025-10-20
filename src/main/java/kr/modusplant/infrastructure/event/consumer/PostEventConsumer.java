package kr.modusplant.infrastructure.event.consumer;

import kr.modusplant.framework.out.jpa.entity.CommPostLikeEntity;
import kr.modusplant.framework.out.jpa.repository.CommPostJpaRepository;
import kr.modusplant.framework.out.jpa.repository.CommPostLikeJpaRepository;
import kr.modusplant.infrastructure.event.bus.EventBus;
import kr.modusplant.shared.event.PostLikeEvent;
import kr.modusplant.shared.event.PostUnlikeEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PostEventConsumer {
    private final CommPostLikeJpaRepository commPostLikeRepository;
    private final CommPostJpaRepository commPostRepository;

    public PostEventConsumer(EventBus eventBus, CommPostLikeJpaRepository commPostLikeRepository, CommPostJpaRepository commPostRepository) {
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
        this.commPostLikeRepository = commPostLikeRepository;
        this.commPostRepository = commPostRepository;
    }

    private void putCommPostLike(UUID memberId, String postId) {
        commPostLikeRepository.save(CommPostLikeEntity.of(postId, memberId));
        commPostRepository.findByUlid(postId).orElseThrow().increaseLikeCount();
    }

    private void deleteCommPostLike(UUID memberId, String postId) {
        commPostLikeRepository.delete(CommPostLikeEntity.of(postId, memberId));
        commPostRepository.findByUlid(postId).orElseThrow().decreaseLikeCount();
    }
}
