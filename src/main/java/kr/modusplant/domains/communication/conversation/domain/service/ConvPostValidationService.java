package kr.modusplant.domains.communication.conversation.domain.service;

import kr.modusplant.domains.communication.common.domain.service.supers.AbstractPostValidationService;
import kr.modusplant.domains.communication.common.error.CommunicationNotFoundException;
import kr.modusplant.domains.communication.common.error.PostAccessDeniedException;
import kr.modusplant.domains.communication.conversation.app.http.request.ConvPostInsertRequest;
import kr.modusplant.domains.communication.conversation.app.http.request.ConvPostUpdateRequest;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvPostEntity;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvCategoryRepository;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConvPostValidationService extends AbstractPostValidationService {

    private final ConvPostRepository convPostRepository;
    private final ConvCategoryRepository convCategoryRepository;

    public void validateConvPostInsertRequest(ConvPostInsertRequest request) {
        validateNotFoundCategoryUuid(request.categoryUuid(), convCategoryRepository);
        validateContentAndOrderInfo(request.content(),request.orderInfo());
    }

    public void validateConvPostUpdateRequest(ConvPostUpdateRequest request) {
        validateNotFoundCategoryUuid(request.categoryUuid(), convCategoryRepository);
        validateContentAndOrderInfo(request.content(),request.orderInfo());
    }

    public void validateAccessibleConvPost(String ulid, UUID memberUuid) {
        ConvPostEntity convPost = findIfExistsByUlid(ulid);
        validateMemberHasPostAccess(convPost,memberUuid);
    }

    public void validateNotFoundUlid(String ulid) {
        if (ulid == null || !convPostRepository.existsByUlid(ulid)) {
            throw CommunicationNotFoundException.ofPost();
        }
    }

    private ConvPostEntity findIfExistsByUlid(String ulid) {
        if (ulid == null) {
            throw CommunicationNotFoundException.ofPost();
        }
        return convPostRepository.findByUlidAndIsDeletedFalse(ulid)
                .orElseThrow(CommunicationNotFoundException::ofPost);
    }

    // TODO : Spring Security 적용 후 PreAuthorize 고려
    private void validateMemberHasPostAccess(ConvPostEntity convPost, UUID memberUuid) {
        if(!convPost.getAuthMember().getUuid().equals(memberUuid)) {
            throw new PostAccessDeniedException();
        }
    }
}
