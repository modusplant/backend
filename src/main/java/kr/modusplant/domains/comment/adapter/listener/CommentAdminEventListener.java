package kr.modusplant.domains.comment.adapter.listener;

import kr.modusplant.domains.comment.adapter.helper.CommentValidationHelper;
import kr.modusplant.domains.comment.domain.vo.CommentAuthorId;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.domains.comment.domain.vo.ParentPostId;
import kr.modusplant.domains.comment.usecase.port.repository.CommentAuthorRepository;
import kr.modusplant.domains.comment.usecase.port.repository.CommentCommandRepository;
import kr.modusplant.domains.member.domain.event.CommentAbuseReportApproveEvent;
import kr.modusplant.shared.enums.Role;
import kr.modusplant.shared.exception.NotAccessibleException;
import kr.modusplant.shared.exception.enums.GeneralErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentAdminEventListener {
    private final CommentCommandRepository commandRepository;
    private final CommentAuthorRepository authorRepository;
    private final CommentValidationHelper commentValidationHelper;

    @EventListener
    public void deleteComment(CommentAbuseReportApproveEvent event) {
        if (authorRepository.getRole(CommentAuthorId.fromUuid(event.getMemberId())) != Role.ADMIN) {
            throw new NotAccessibleException(
                    GeneralErrorCode.ADMIN_ROLE_REQUIRED_TO_ACCESS, "memberId", String.valueOf(event.getMemberId()));
        }
        ParentPostId parentPostId = ParentPostId.create(event.getPostUlid());
        CommentPath commentPath = CommentPath.create(event.getPath());
        commentValidationHelper.validateIfParentPostExists(parentPostId);
        commentValidationHelper.validateIfParentPostIsPublished(parentPostId);
        commentValidationHelper.validateIfCommentExists(parentPostId, commentPath);

        commandRepository.deleteComment(parentPostId, commentPath);
    }
}
