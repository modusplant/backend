package kr.modusplant.domains.comment.usecase.port.outbound;

public interface CommentPostValidator {

    boolean isPostPublished(String postId);
}
