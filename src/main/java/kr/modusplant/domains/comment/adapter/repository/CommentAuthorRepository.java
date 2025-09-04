package kr.modusplant.domains.comment.adapter.repository;

import kr.modusplant.domains.comment.adapter.model.MemberReadModel;
import kr.modusplant.domains.comment.domain.vo.Author;

public interface CommentAuthorRepository {

    MemberReadModel findByAuthor(Author author);
}
