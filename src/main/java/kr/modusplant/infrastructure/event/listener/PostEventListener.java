package kr.modusplant.infrastructure.event.listener;

import kr.modusplant.framework.jpa.entity.CommPostBookmarkEntity;
import kr.modusplant.framework.jpa.entity.CommPostLikeEntity;
import kr.modusplant.framework.jpa.repository.CommPostBookmarkJpaRepository;
import kr.modusplant.framework.jpa.repository.CommPostJpaRepository;
import kr.modusplant.framework.jpa.repository.CommPostLikeJpaRepository;
import kr.modusplant.shared.event.PostBookmarkCancelEvent;
import kr.modusplant.shared.event.PostBookmarkEvent;
import kr.modusplant.shared.event.PostLikeEvent;
import kr.modusplant.shared.event.PostUnlikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PostEventListener {
    private final CommPostLikeJpaRepository commPostLikeRepository;
    private final CommPostBookmarkJpaRepository commPostBookmarkRepository;
    private final CommPostJpaRepository commPostRepository;

    @EventListener
    public void handlePostLikeEvent(PostLikeEvent likeEvent) {
        UUID memberId = likeEvent.getMemberId();
        String postId = likeEvent.getPostId();

        commPostLikeRepository.save(CommPostLikeEntity.of(postId, memberId));
        commPostRepository.findByUlid(postId).orElseThrow().increaseLikeCount();
    }

    @EventListener
    public void handlePostUnlikeEvent(PostUnlikeEvent unlikeEvent) {
        UUID memberId = unlikeEvent.getMemberId();
        String postId = unlikeEvent.getPostId();

        commPostLikeRepository.delete(CommPostLikeEntity.of(postId, memberId));
        commPostRepository.findByUlid(postId).orElseThrow().decreaseLikeCount();
    }

    @EventListener
    public void handlePostBookmarkEvent(PostBookmarkEvent bookmarkEvent) {
        UUID memberId = bookmarkEvent.getMemberId();
        String postId = bookmarkEvent.getPostId();

        commPostBookmarkRepository.save(CommPostBookmarkEntity.of(postId, memberId));
    }

    @EventListener
    public void handlePostBookmarkCancelEvent(PostBookmarkCancelEvent bookmarkCancelEvent) {
        UUID memberId = bookmarkCancelEvent.getMemberId();
        String postId = bookmarkCancelEvent.getPostId();

        commPostBookmarkRepository.delete(CommPostBookmarkEntity.of(postId, memberId));
    }
}
