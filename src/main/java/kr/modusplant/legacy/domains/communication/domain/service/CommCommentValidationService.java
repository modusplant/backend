package kr.modusplant.legacy.domains.communication.domain.service;

import kr.modusplant.framework.outbound.persistence.vo.EntityName;
import kr.modusplant.legacy.domains.communication.persistence.repository.CommCommentRepository;
import kr.modusplant.shared.exception.EntityExistsException;
import kr.modusplant.shared.exception.EntityNotFoundException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommCommentValidationService {
    private final CommCommentRepository commentRepository;

    public void validateExistedCommCommentEntity(String postUlid, String path) {
        if (commentRepository.existsByPostUlidAndPath(postUlid, path)) {
            throw new EntityExistsException(ErrorCode.COMMENT_EXISTS, EntityName.COMMENT);
        }
    }

    public void validateNotFoundCommCommentEntity(String postUlid, String path) {
        if(!commentRepository.existsByPostUlidAndPath(postUlid, path)) {
            throw new EntityNotFoundException(ErrorCode.COMMENT_NOT_FOUND, EntityName.COMMENT);
        }
    }
}
