package kr.modusplant.domains.post.usecase.port.repository;

import kr.modusplant.domains.post.domain.vo.AuthorId;
import kr.modusplant.domains.post.domain.vo.PostId;
import kr.modusplant.domains.post.usecase.record.DraftPostReadModel;
import kr.modusplant.domains.post.usecase.record.PostSummaryReadModel;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface PostQueryForMemberRepository {

    Page<PostSummaryReadModel> findPublishedByAuthMemberWithOffset(AuthorId authorId, int page, int size);

    Page<DraftPostReadModel> findDraftByAuthMemberWithOffset(AuthorId authorId, int page, int size);

    List<PostSummaryReadModel> findByIds(List<PostId> postIds, UUID currentMemberUuid);

    Page<PostSummaryReadModel> findLikedByMemberWithOffset(UUID currentMemberUuid, int page, int size);

    Page<PostSummaryReadModel> findBookmarkedByMemberWithOffset(UUID currentMemberUuid, int page, int size);

}
