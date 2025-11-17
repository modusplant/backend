package kr.modusplant.infrastructure.event.consumer;

import kr.modusplant.framework.jpa.entity.CommCommentLikeEntity;
import kr.modusplant.framework.jpa.repository.CommCommentJpaRepository;
import kr.modusplant.framework.jpa.repository.CommCommentLikeJpaRepository;
import kr.modusplant.infrastructure.event.bus.EventBus;
import kr.modusplant.shared.event.CommentLikeEvent;
import kr.modusplant.shared.event.CommentUnlikeEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CommentEventConsumer {
    private final CommCommentLikeJpaRepository commCommentLikeRepository;
    private final CommCommentJpaRepository commCommentRepository;

    public CommentEventConsumer(EventBus eventBus, CommCommentLikeJpaRepository commCommentLikeRepository, CommCommentJpaRepository commCommentRepository) {
        eventBus.subscribe(event -> {
            if (event instanceof CommentLikeEvent commentLikeEvent) {
                putCommCommentLike(commentLikeEvent.getMemberId(), commentLikeEvent.getPostId(), commentLikeEvent.getPath());
            }
        });
        eventBus.subscribe(event -> {
            if (event instanceof CommentUnlikeEvent commentUnlikeEvent) {
                deleteCommCommentLike(commentUnlikeEvent.getMemberId(), commentUnlikeEvent.getPostId(), commentUnlikeEvent.getPath());
            }
        });
        this.commCommentLikeRepository = commCommentLikeRepository;
        this.commCommentRepository = commCommentRepository;
    }

    private void putCommCommentLike(UUID memberId, String postId, String path) {
        commCommentLikeRepository.save(CommCommentLikeEntity.of(postId, path, memberId));
        commCommentRepository.findByPostUlidAndPath(postId, path).orElseThrow().increaseLikeCount();
    }

    private void deleteCommCommentLike(UUID memberId, String postId, String path) {
        commCommentLikeRepository.delete(CommCommentLikeEntity.of(postId, path, memberId));
        commCommentRepository.findByPostUlidAndPath(postId, path).orElseThrow().decreaseLikeCount();
    }
}
