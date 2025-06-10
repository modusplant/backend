package kr.modusplant.domains.communication.qna.domain.service;

import kr.modusplant.domains.communication.common.error.EntityExistsWithPostUlidAndPathException;
import kr.modusplant.domains.communication.common.error.EntityNotFoundWithPostUlidAndPathException;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCommentEntity;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QnaCommentValidationService {
    private final QnaCommentRepository commentRepository;

    public void validateFoundQnaCommentEntity(String postUlid, String path) {
        Optional.ofNullable(postUlid).orElseThrow(() -> new IllegalArgumentException("postUlid is null"));
        Optional.ofNullable(path).orElseThrow(() -> new IllegalArgumentException("path is null"));

        if (commentRepository.existsByPostUlidAndPath(postUlid, path)) {
            throw new EntityExistsWithPostUlidAndPathException(postUlid, path, QnaCommentEntity.class);
        }
    }

    public void validateNotFoundQnaCommentEntity(String postUlid, String path) {
        Optional.ofNullable(postUlid).orElseThrow(() -> new IllegalArgumentException("postUlid is null"));
        Optional.ofNullable(path).orElseThrow(() -> new IllegalArgumentException("path is null"));

        if(!commentRepository.existsByPostUlidAndPath(postUlid, path)) {
            throw new EntityNotFoundWithPostUlidAndPathException(postUlid, path, QnaCommentEntity.class);
        }
    }
}
