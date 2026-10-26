package kr.modusplant.domains.comment.usecase.port.repository;

import kr.modusplant.domains.comment.domain.vo.CommentAuthorId;
import kr.modusplant.shared.enums.Role;

public interface CommentAuthorRepository {

    Role getRole(CommentAuthorId commentAuthorId);

    boolean isExists(CommentAuthorId commentAuthorId);
}
