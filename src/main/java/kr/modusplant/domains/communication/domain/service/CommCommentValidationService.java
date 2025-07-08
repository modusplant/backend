package kr.modusplant.domains.communication.domain.service;

import kr.modusplant.domains.communication.error.CommentExistsException;
import kr.modusplant.domains.communication.error.CommentNotFoundException;
import kr.modusplant.domains.communication.persistence.repository.CommCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommCommentValidationService {
    private final CommCommentRepository commentRepository;

    public void validateExistedCommCommentEntity(String postUlid, String path) {
        Optional.ofNullable(postUlid).orElseThrow(() -> new IllegalArgumentException("게시글 값이 비어 있습니다."));
        Optional.ofNullable(path).orElseThrow(() -> new IllegalArgumentException("경로 값이 비어 있습니다."));

        if (commentRepository.existsByPostUlidAndPath(postUlid, path)) {
            throw new CommentExistsException();
        }
    }

    public void validateNotFoundCommCommentEntity(String postUlid, String path) {
        Optional.ofNullable(postUlid).orElseThrow(() -> new IllegalArgumentException("게시글 값이 비어 있습니다."));
        Optional.ofNullable(path).orElseThrow(() -> new IllegalArgumentException("경로 값이 비어 있습니다."));

        if(!commentRepository.existsByPostUlidAndPath(postUlid, path)) {
            throw new CommentNotFoundException();
        }
    }
}
