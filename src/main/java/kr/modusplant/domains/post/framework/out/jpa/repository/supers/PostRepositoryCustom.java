package kr.modusplant.domains.post.framework.out.jpa.repository.supers;

import kr.modusplant.domains.post.usecase.model.PostSummaryReadModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PostRepositoryCustom {
    Page<PostSummaryReadModel> findByDynamicConditionsAndIsPublishedTrue(UUID primaryCategoryUuid, List<UUID> secondaryCategoryUuids, String keyword, Pageable pageable);

    Page<PostSummaryReadModel> findByAuthMemberAndDynamicConditionsAndIsPublishedTrue(UUID authMemberUuid, UUID primaryCategoryUuid, List<UUID> secondaryCategoryUuids, String keyword, Pageable pageable);
}
