package kr.modusplant.domains.communication.tip.domain.service;

import kr.modusplant.domains.communication.common.domain.service.supers.AbstractPostValidationService;
import kr.modusplant.domains.communication.common.error.PostAccessDeniedException;
import kr.modusplant.domains.communication.common.error.PostNotFoundException;
import kr.modusplant.domains.communication.tip.app.http.request.TipPostInsertRequest;
import kr.modusplant.domains.communication.tip.app.http.request.TipPostUpdateRequest;
import kr.modusplant.domains.communication.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.communication.tip.persistence.repository.TipCategoryRepository;
import kr.modusplant.domains.communication.tip.persistence.repository.TipPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TipPostValidationService extends AbstractPostValidationService {

    private final TipPostRepository tipPostRepository;
    private final TipCategoryRepository tipCategoryRepository;

    public void validateTipPostInsertRequest(TipPostInsertRequest request) {
        validateNotFoundCategoryUuid(request.categoryUuid(), tipCategoryRepository);
        validateTitle(request.title());
        validateContentAndOrderInfo(request.content(),request.orderInfo());
    }

    public void validateTipPostUpdateRequest(TipPostUpdateRequest request) {
        validateNotFoundCategoryUuid(request.categoryUuid(), tipCategoryRepository);
        validateTitle(request.title());
        validateContentAndOrderInfo(request.content(),request.orderInfo());
    }

    public void validateAccessibleTipPost(String ulid, UUID memberUuid) {
        TipPostEntity tipPost = findIfExistsByUlid(ulid);
        validateMemberHasPostAccess(tipPost,memberUuid);
    }

    public void validateNotFoundUlid(String ulid) {
        if (ulid == null || !tipPostRepository.existsByUlid(ulid)) {
            throw new PostNotFoundException();
        }
    }

    private TipPostEntity findIfExistsByUlid(String ulid) {
        if (ulid == null) {
            throw new PostNotFoundException();
        }
        return tipPostRepository.findByUlidAndIsDeletedFalse(ulid)
                .orElseThrow(PostNotFoundException::new);
    }

    // TODO : Spring Security 적용 후 PreAuthorize 고려
    private void validateMemberHasPostAccess(TipPostEntity tipPost, UUID memberUuid) {
        if(!tipPost.getAuthMember().getUuid().equals(memberUuid)) {
            throw new PostAccessDeniedException();
        }
    }
}
