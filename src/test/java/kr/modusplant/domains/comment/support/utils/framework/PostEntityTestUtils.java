package kr.modusplant.domains.comment.support.utils.framework;

import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.PostEntity;
import kr.modusplant.domains.comment.support.utils.domain.PostIdTestUtils;

public interface PostEntityTestUtils extends PostIdTestUtils {
    PostEntity testPostEntity = PostEntity.create(testPostId.getId());
}
