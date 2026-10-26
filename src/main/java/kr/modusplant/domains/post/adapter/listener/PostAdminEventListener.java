package kr.modusplant.domains.post.adapter.listener;

import kr.modusplant.domains.member.domain.event.PostAbuseReportApproveEvent;
import kr.modusplant.domains.post.domain.aggregate.Post;
import kr.modusplant.domains.post.domain.exception.PostNotFoundException;
import kr.modusplant.domains.post.domain.vo.PostAuthorId;
import kr.modusplant.domains.post.domain.vo.PostId;
import kr.modusplant.domains.post.usecase.port.processor.ContentDataProcessorPort;
import kr.modusplant.domains.post.usecase.port.repository.PostArchiveRepository;
import kr.modusplant.domains.post.usecase.port.repository.PostAuthorRepository;
import kr.modusplant.domains.post.usecase.port.repository.PostRepository;
import kr.modusplant.shared.enums.Role;
import kr.modusplant.shared.exception.NotAccessibleException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostAdminEventListener {
    private final PostRepository postRepository;
    private final PostArchiveRepository postArchiveRepository;
    private final PostAuthorRepository postAuthorRepository;
    private final ContentDataProcessorPort contentDataProcessorPort;

    @EventListener
    public void deletePost(PostAbuseReportApproveEvent event) {
        if (postAuthorRepository.getRole(PostAuthorId.fromUuid(event.getMemberId())) != Role.ADMIN) {
            throw new NotAccessibleException(
                    GeneralErrorCode.ADMIN_ROLE_REQUIRED_TO_ACCESS, "memberId", String.valueOf(event.getMemberId()));
        }
        PostId postId = PostId.create(event.getPostUlid());
        Post post = postRepository.getPostByUlid(postId).orElseThrow(PostNotFoundException::new);
        if (post.getStatus().isPublished()) {
            postArchiveRepository.save(postId);
            postRepository.deletePostLikeByPostId(post.getPostId());
            postRepository.deletePostBookmarkByPostId(post.getPostId());
            postRepository.deletePostAbuseReportByPostId(post.getPostId());
            postRepository.deletePostAbuseReportDashboardByPostId(post.getPostId());
            postRepository.deletePostRecentlyViewRecordByPostId(post.getPostId());
        }
        contentDataProcessorPort.deleteFiles(post.getPostContent().getContent());
        postRepository.delete(post);
    }
}
