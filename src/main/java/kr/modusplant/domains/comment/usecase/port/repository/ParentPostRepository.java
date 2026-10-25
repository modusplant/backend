package kr.modusplant.domains.comment.usecase.port.repository;

import kr.modusplant.domains.comment.domain.vo.ParentPostId;

public interface ParentPostRepository {

    boolean isParentPostExists(ParentPostId parentPostId);

    boolean isParentPostPublished(ParentPostId parentPostId);
}
