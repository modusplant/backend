package kr.modusplant.domains.communication.qna.domain.service;

import kr.modusplant.domains.communication.common.domain.service.supers.AbstractPostValidationService;
import kr.modusplant.domains.communication.common.error.PostAccessDeniedException;
import kr.modusplant.domains.communication.common.error.PostNotFoundException;
import kr.modusplant.domains.communication.qna.app.http.request.QnaPostInsertRequest;
import kr.modusplant.domains.communication.qna.app.http.request.QnaPostUpdateRequest;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaCategoryRepository;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QnaPostValidationService extends AbstractPostValidationService {

    private final QnaPostRepository qnaPostRepository;
    private final QnaCategoryRepository qnaCategoryRepository;

    public void validateQnaPostInsertRequest(QnaPostInsertRequest request) {
        validateNotFoundCategoryUuid(request.categoryUuid(), qnaCategoryRepository);
        validateTitle(request.title());
        validateContentAndOrderInfo(request.content(),request.orderInfo());
    }

    public void validateQnaPostUpdateRequest(QnaPostUpdateRequest request) {
        validateNotFoundCategoryUuid(request.categoryUuid(), qnaCategoryRepository);
        validateTitle(request.title());
        validateContentAndOrderInfo(request.content(),request.orderInfo());
    }

    public void validateAccessibleQnaPost(String ulid, UUID memberUuid) {
        QnaPostEntity qnaPost = findIfExistsByUlid(ulid);
        validateMemberHasPostAccess(qnaPost,memberUuid);
    }

    public void validateNotFoundUlid(String ulid) {
        if (ulid == null || !qnaPostRepository.existsByUlid(ulid)) {
            throw new PostNotFoundException();
        }
    }

    private QnaPostEntity findIfExistsByUlid(String ulid) {
        if (ulid == null) {
            throw new PostNotFoundException();
        }
        return qnaPostRepository.findByUlidAndIsDeletedFalse(ulid)
                .orElseThrow(PostNotFoundException::new);
    }

    // TODO : Spring Security 적용 후 PreAuthorize 고려
    private void validateMemberHasPostAccess(QnaPostEntity qnaPost, UUID memberUuid) {
        if(!qnaPost.getAuthMember().getUuid().equals(memberUuid)) {
            throw new PostAccessDeniedException();
        }
    }
}
