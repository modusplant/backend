package kr.modusplant.domains.comment.adapter.mapper;

import kr.modusplant.domains.comment.domain.aggregate.Comment;
import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.domain.vo.CommentContent;
import kr.modusplant.domains.comment.domain.vo.CommentPath;
import kr.modusplant.domains.comment.domain.vo.PostId;
import kr.modusplant.domains.comment.usecase.port.mapper.CommentMapper;
import kr.modusplant.domains.comment.usecase.request.CommentRegisterRequest;
import org.springframework.stereotype.Component;

@Component
public class CommentMapperImpl implements CommentMapper {

    @Override
    public Comment toComment(CommentRegisterRequest request) {
        return Comment.create(
                PostId.create(request.postId()),
                CommentPath.create(request.path()),
                Author.create(request.memberUuid()),
                CommentContent.create(request.content())
        );
    }

}
