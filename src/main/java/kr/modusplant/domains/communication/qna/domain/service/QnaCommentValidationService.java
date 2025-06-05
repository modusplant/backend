package kr.modusplant.domains.communication.qna.domain.service;

import kr.modusplant.domains.communication.qna.persistence.repository.QnaCommentRepository;
import kr.modusplant.domains.communication.common.error.EntityExistsWithPostUlidAndMatePathException;
import kr.modusplant.domains.communication.common.error.EntityNotFoundWithPostUlidAndMatePathException;
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

        if (commentRepository.findByPostUlidAndPath(postUlid, path).isPresent()) {
            throw new EntityExistsWithPostUlidAndMatePathException("qna comment entity already exists");
        }
    }

    public void validateNotFoundQnaCommentEntity(String postUlid, String path) {
        Optional.ofNullable(postUlid).orElseThrow(() -> new IllegalArgumentException("postUlid is null"));
        Optional.ofNullable(path).orElseThrow(() -> new IllegalArgumentException("path is null"));

        if(commentRepository.findByPostUlidAndPath(postUlid, path).isEmpty()) {
            throw new EntityNotFoundWithPostUlidAndMatePathException("qna comment entity not found");
        }
    }
}
