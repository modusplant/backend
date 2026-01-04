package kr.modusplant.domains.post.usecase.port.repository;

import kr.modusplant.domains.post.domain.vo.PostId;
import kr.modusplant.domains.post.usecase.record.PostDetailDataReadModel;
import kr.modusplant.domains.post.usecase.record.PostDetailReadModel;
import kr.modusplant.domains.post.usecase.record.PostSummaryReadModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostQueryRepository {

    List<PostSummaryReadModel> findByCategoryWithCursor(Integer primaryCategoryId, List<Integer> secondaryCategoryIds, UUID currentMemberUuid, String cursorUlid, int size);

    List<PostSummaryReadModel> findByKeywordWithCursor(String keyword, UUID currentMemberUuid, String cursorUlid, int size);

    Optional<PostDetailReadModel> findPostDetailByPostId(PostId postId, UUID currentMemberUuid);

    Optional<PostDetailDataReadModel> findPostDetailDataByPostId(PostId postId);
}
