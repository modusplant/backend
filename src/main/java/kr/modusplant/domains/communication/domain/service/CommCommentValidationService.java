package kr.modusplant.domains.communication.domain.service;

import kr.modusplant.domains.communication.persistence.repository.CommCommentRepository;
import kr.modusplant.global.enums.ErrorCode;
import kr.modusplant.global.error.EntityExistsException;
import kr.modusplant.global.error.EntityNotFoundException;
import kr.modusplant.global.vo.EntityName;
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
