package kr.modusplant.infrastructure.event.consumer;

import kr.modusplant.framework.out.jpa.entity.CommPostBookmarkEntity;
import kr.modusplant.framework.out.jpa.entity.CommPostLikeEntity;
import kr.modusplant.framework.out.jpa.repository.CommPostBookmarkJpaRepository;
import kr.modusplant.framework.out.jpa.repository.CommPostJpaRepository;
import kr.modusplant.framework.out.jpa.repository.CommPostLikeJpaRepository;
import kr.modusplant.infrastructure.event.bus.EventBus;
import kr.modusplant.shared.event.PostBookmarkCancelEvent;
import kr.modusplant.shared.event.PostBookmarkEvent;
import kr.modusplant.shared.event.PostLikeEvent;
import kr.modusplant.shared.event.PostUnlikeEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PostEventConsumer {
    private final CommPostLikeJpaRepository commPostLikeRepository;
    private final CommPostBookmarkJpaRepository commPostBookmarkRepository;
    private final CommPostJpaRepository commPostRepository;

    public PostEventConsumer(EventBus eventBus, CommPostLikeJpaRepository commPostLikeRepository, CommPostBookmarkJpaRepository commPostBookmarkRepository, CommPostJpaRepository commPostRepository) {
        eventBus.subscribe(event -> {
            if (event instanceof PostLikeEvent likeEvent) {
                putCommPostLike(likeEvent.getMemberId(), likeEvent.getPostId());
            }
        });
        eventBus.subscribe(event -> {
            if (event instanceof PostUnlikeEvent unlikeEvent) {
                deleteCommPostLike(unlikeEvent.getMemberId(), unlikeEvent.getPostId());
            }
        });
        eventBus.subscribe(event -> {
            if (event instanceof PostBookmarkEvent bookmarkEvent) {
                putCommPostBookmark(bookmarkEvent.getMemberId(), bookmarkEvent.getPostId());
            }
        });
        eventBus.subscribe(event -> {
            if (event instanceof PostBookmarkCancelEvent bookmarkCancelEvent) {
                deleteCommPostBookmark(bookmarkCancelEvent.getMemberId(), bookmarkCancelEvent.getPostId());
            }
        });
        this.commPostLikeRepository = commPostLikeRepository;
        this.commPostBookmarkRepository = commPostBookmarkRepository;
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

    private void putCommPostBookmark(UUID memberId, String postId) {
        commPostBookmarkRepository.save(CommPostBookmarkEntity.of(postId, memberId));
    }

    private void deleteCommPostBookmark(UUID memberId, String postId) {
        commPostBookmarkRepository.delete(CommPostBookmarkEntity.of(postId, memberId));
    }
}
