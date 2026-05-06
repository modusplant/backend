package kr.modusplant.domains.comment.usecase.port.outbound;

public interface CommentPostRepository {

    boolean isPostPublished(String postId);
}
