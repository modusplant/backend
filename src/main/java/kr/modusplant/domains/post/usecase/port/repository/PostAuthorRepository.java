package kr.modusplant.domains.post.usecase.port.repository;

import kr.modusplant.domains.post.domain.vo.PostAuthorId;
import kr.modusplant.shared.enums.Role;

public interface PostAuthorRepository {
    Role getRole(PostAuthorId postAuthorId);
}
