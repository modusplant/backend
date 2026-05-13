package kr.modusplant.infrastructure.event.listener;

import kr.modusplant.framework.jpa.entity.CommCommentLikeEntity;
import kr.modusplant.framework.jpa.repository.CommCommentJpaRepository;
import kr.modusplant.framework.jpa.repository.CommCommentLikeJpaRepository;
import kr.modusplant.shared.event.CommentLikeEvent;
import kr.modusplant.shared.event.CommentUnlikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CommentEventListener {
    private final CommCommentLikeJpaRepository commCommentLikeRepository;
    private final CommCommentJpaRepository commCommentRepository;

    @EventListener
    public void handleCommentLikeEvent(CommentLikeEvent commentLikeEvent) {
        UUID memberId = commentLikeEvent.getMemberId();
        String postId = commentLikeEvent.getPostId();
        String path = commentLikeEvent.getPath();

        commCommentLikeRepository.save(CommCommentLikeEntity.of(postId, path, memberId));
        commCommentRepository.findByPostUlidAndPath(postId, path).orElseThrow().increaseLikeCount();
    }

    @EventListener
    public void handleCommentUnlikeEvent(CommentUnlikeEvent commentUnlikeEvent) {
        UUID memberId = commentUnlikeEvent.getMemberId();
        String postId = commentUnlikeEvent.getPostId();
        String path = commentUnlikeEvent.getPath();

        commCommentLikeRepository.delete(CommCommentLikeEntity.of(postId, path, memberId));
        commCommentRepository.findByPostUlidAndPath(postId, path).orElseThrow().decreaseLikeCount();
    }
}
