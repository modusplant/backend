package kr.modusplant.infrastructure.event.listener;

import kr.modusplant.framework.jpa.entity.CommentLikeEntity;
import kr.modusplant.framework.jpa.repository.CommentJpaRepository;
import kr.modusplant.framework.jpa.repository.CommentLikeJpaRepository;
import kr.modusplant.shared.event.CommentLikeEvent;
import kr.modusplant.shared.event.CommentUnlikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CommentEventListener {
    private final CommentLikeJpaRepository commentLikeRepository;
    private final CommentJpaRepository commentRepository;

    @EventListener
    public void handleCommentLikeEvent(CommentLikeEvent commentLikeEvent) {
        UUID memberId = commentLikeEvent.getMemberId();
        String postId = commentLikeEvent.getPostId();
        String path = commentLikeEvent.getPath();

        commentLikeRepository.save(CommentLikeEntity.of(postId, path, memberId));
        commentRepository.findByPostUlidAndPath(postId, path).orElseThrow().increaseLikeCount();
    }

    @EventListener
    public void handleCommentUnlikeEvent(CommentUnlikeEvent commentUnlikeEvent) {
        UUID memberId = commentUnlikeEvent.getMemberId();
        String postId = commentUnlikeEvent.getPostId();
        String path = commentUnlikeEvent.getPath();

        commentLikeRepository.delete(CommentLikeEntity.of(postId, path, memberId));
        commentRepository.findByPostUlidAndPath(postId, path).orElseThrow().decreaseLikeCount();
    }
}
