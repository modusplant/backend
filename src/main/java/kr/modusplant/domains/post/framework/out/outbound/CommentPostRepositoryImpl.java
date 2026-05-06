package kr.modusplant.domains.post.framework.out.outbound;

import kr.modusplant.domains.comment.usecase.port.outbound.CommentPostRepository;
import kr.modusplant.jooq.tables.CommPost;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentPostRepositoryImpl implements CommentPostRepository {

    private final DSLContext dsl;
    private final CommPost commPost = CommPost.COMM_POST;

    @Override
    public boolean isPostPublished(String postId) {
        return postId != null && Boolean.TRUE.equals(
                dsl.select(commPost.IS_PUBLISHED)
                        .from(commPost)
                        .where(commPost.ULID.eq(postId))
                        .fetchOne(commPost.IS_PUBLISHED)
        );
    }
}
