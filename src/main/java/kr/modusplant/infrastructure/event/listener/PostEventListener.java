package kr.modusplant.infrastructure.event.listener;

import kr.modusplant.domains.member.framework.out.jpa.entity.PostBookmarkEntity;
import kr.modusplant.domains.member.framework.out.jpa.entity.PostLikeEntity;
import kr.modusplant.domains.member.framework.out.jpa.repository.PostBookmarkJpaRepository;
import kr.modusplant.domains.member.framework.out.jpa.repository.PostLikeJpaRepository;
import kr.modusplant.domains.post.framework.out.jpa.repository.PostJpaRepository;
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
    private final PostLikeJpaRepository postLikeRepository;
    private final PostBookmarkJpaRepository postBookmarkRepository;
    private final PostJpaRepository postRepository;

    @EventListener
    public void handlePostLikeEvent(PostLikeEvent likeEvent) {
        UUID memberId = likeEvent.getMemberId();
        String postId = likeEvent.getPostId();

        postLikeRepository.save(PostLikeEntity.of(postId, memberId));
        postRepository.findByUlid(postId).orElseThrow().increaseLikeCount();
    }

    @EventListener
    public void handlePostUnlikeEvent(PostUnlikeEvent unlikeEvent) {
        UUID memberId = unlikeEvent.getMemberId();
        String postId = unlikeEvent.getPostId();

        postLikeRepository.delete(PostLikeEntity.of(postId, memberId));
        postRepository.findByUlid(postId).orElseThrow().decreaseLikeCount();
    }

    @EventListener
    public void handlePostBookmarkEvent(PostBookmarkEvent bookmarkEvent) {
        UUID memberId = bookmarkEvent.getMemberId();
        String postId = bookmarkEvent.getPostId();

        postBookmarkRepository.save(PostBookmarkEntity.of(postId, memberId));
    }

    @EventListener
    public void handlePostBookmarkCancelEvent(PostBookmarkCancelEvent bookmarkCancelEvent) {
        UUID memberId = bookmarkCancelEvent.getMemberId();
        String postId = bookmarkCancelEvent.getPostId();

        postBookmarkRepository.delete(PostBookmarkEntity.of(postId, memberId));
    }
}
