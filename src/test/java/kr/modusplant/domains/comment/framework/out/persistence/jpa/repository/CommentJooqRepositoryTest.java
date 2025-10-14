package kr.modusplant.domains.comment.framework.out.persistence.jpa.repository;

import kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.supers.CommentJooqRepository;
import kr.modusplant.jooq.tables.CommComment;
import kr.modusplant.jooq.tables.CommPost;
import kr.modusplant.jooq.tables.SiteMember;
import org.jooq.DSLContext;
import org.mockito.Mockito;

public class CommentJooqRepositoryTest {
    private final DSLContext context = Mockito.mock(DSLContext.class);
    private final CommPost commPost = CommPost.COMM_POST;
    private final CommComment commComment = CommComment.COMM_COMMENT;
    private final SiteMember siteMember = SiteMember.SITE_MEMBER;
//    private final CommentJooqRepository repository = new CommentJooqRepository(context, commPost, commComment, siteMember);


}
