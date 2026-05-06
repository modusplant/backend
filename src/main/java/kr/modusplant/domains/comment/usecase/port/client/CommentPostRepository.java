package kr.modusplant.domains.comment.usecase.port.client;

public interface CommentPostRepository {

    boolean isPostPublished(String postId);
}
