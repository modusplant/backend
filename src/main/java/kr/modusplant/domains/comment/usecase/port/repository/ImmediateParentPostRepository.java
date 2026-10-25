package kr.modusplant.domains.comment.usecase.port.repository;

import kr.modusplant.domains.comment.domain.vo.ImmediateParentPostId;

public interface ImmediateParentPostRepository {

    boolean isImmediateParentPostExists(ImmediateParentPostId immediateParentPostId);

    boolean isImmediateParentPostPublished(ImmediateParentPostId immediateParentPostId);
}
