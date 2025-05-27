package kr.modusplant.domains.communication.qna.domain.service;

import kr.modusplant.domains.communication.common.domain.service.supers.AbstractPostValidationService;
import kr.modusplant.domains.communication.qna.app.http.request.QnaPostInsertRequest;
import kr.modusplant.domains.communication.qna.app.http.request.QnaPostUpdateRequest;
import kr.modusplant.domains.communication.common.error.PostAccessDeniedException;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostRepository;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
import kr.modusplant.global.error.EntityNotFoundWithUlidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QnaPostValidationService extends AbstractPostValidationService {

    private final QnaPostRepository qnaPostRepository;

    public void validateQnaPostInsertRequest(QnaPostInsertRequest request) {
        validateGroupOrder(request.groupOrder());
        validateTitle(request.title());
        validateContentAndOrderInfo(request.content(),request.orderInfo());
    }

    public void validateQnaPostUpdateRequest(QnaPostUpdateRequest request) {
        validateGroupOrder(request.groupOrder());
        validateTitle(request.title());
        validateContentAndOrderInfo(request.content(),request.orderInfo());
    }

    public void validateAccessibleQnaPost(String ulid, UUID memberUuid) {
        QnaPostEntity qnaPost = findIfExistsByUlid(ulid);
        validateMemberHasPostAccess(qnaPost,memberUuid);
    }

    public void validateNotFoundUlid(String ulid) {
        if (ulid == null || !qnaPostRepository.existsByUlid(ulid)) {
            throw new EntityNotFoundWithUlidException(ulid, QnaPostEntity.class);
        }
    }

    private QnaPostEntity findIfExistsByUlid(String ulid) {
        if (ulid == null) {
            throw new EntityNotFoundWithUlidException(ulid, QnaPostEntity.class);
        }
        return qnaPostRepository.findByUlidAndIsDeletedFalse(ulid)
                .orElseThrow(() -> new EntityNotFoundWithUlidException(ulid,QnaPostEntity.class));
    }

    // TODO : Spring Security 적용 후 PreAuthorize 고려
    private void validateMemberHasPostAccess(QnaPostEntity qnaPost, UUID memberUuid) {
        if(!qnaPost.getAuthMember().getUuid().equals(memberUuid)) {
            throw new PostAccessDeniedException();
        }
    }
}
