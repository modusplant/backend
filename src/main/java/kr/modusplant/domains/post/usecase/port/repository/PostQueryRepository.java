package kr.modusplant.domains.post.usecase.port.repository;

import kr.modusplant.domains.post.domain.vo.PostId;
import kr.modusplant.domains.post.usecase.enums.SearchOption;
import kr.modusplant.domains.post.usecase.record.PostDetailDataReadModel;
import kr.modusplant.domains.post.usecase.record.PostDetailReadModel;
import kr.modusplant.domains.post.usecase.record.PostSummaryReadModel;
import kr.modusplant.domains.post.usecase.record.PostSummaryWithSearchInfoReadModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostQueryRepository {

    List<PostSummaryReadModel> findByCategoryWithCursor(Integer primaryCategoryId, List<Integer> secondaryCategoryIds, UUID currentMemberUuid, String cursorUlid, int size);

    List<PostSummaryWithSearchInfoReadModel> searchByKeywordWithLatest(SearchOption option, String keyword, Integer primaryCategoryId, List<Integer> secondaryCategoryIds, UUID currentMemberUuid, String cursorUlid, LocalDateTime cursorPublishedAt, int size);

    List<PostSummaryWithSearchInfoReadModel> searchByKeywordWithRelevance(SearchOption option, String keyword, Integer primaryCategoryId, List<Integer> secondaryCategoryIds, UUID currentMemberUuid, String cursorUlid, Integer cursorImportance, Double cursorMaxWordSimilarity, LocalDateTime cursorPublishedAt, int size);

    Optional<PostDetailReadModel> findPostDetailByPostId(PostId postId, UUID currentMemberUuid);

    Optional<PostDetailDataReadModel> findPostDetailDataByPostId(PostId postId);
}
