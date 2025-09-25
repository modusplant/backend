package kr.modusplant.domains.comment.adapter.presenter;

import kr.modusplant.domains.comment.adapter.model.CommentReadModel;
import kr.modusplant.domains.comment.adapter.model.MemberReadModel;
import kr.modusplant.domains.comment.adapter.response.CommentResponse;

public class CommentPresenter {

    public static CommentResponse toCommentResponse(CommentReadModel comment, MemberReadModel member) {
        return new CommentResponse(
                comment.postUlid(),
                comment.path(),
                member.nickname(),
                comment.content(),
                comment.isDeleted(),
                comment.createdAt()
        );
    }
}
