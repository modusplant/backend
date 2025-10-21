package kr.modusplant.domains.comment.usecase.port.repository;

import kr.modusplant.domains.comment.domain.vo.Author;
import kr.modusplant.domains.comment.usecase.model.MemberReadModel;

public interface CommentAuthorRepository {

    MemberReadModel findByAuthor(Author author);
}
