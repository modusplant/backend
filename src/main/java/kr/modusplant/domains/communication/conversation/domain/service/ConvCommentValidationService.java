package kr.modusplant.domains.communication.conversation.domain.service;

import kr.modusplant.domains.communication.common.error.CommentExistsException;
import kr.modusplant.domains.communication.common.error.CommentNotFoundException;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConvCommentValidationService {
    private final ConvCommentRepository commentRepository;

    public void validateExistedConvCommentEntity(String postUlid, String path) {
        Optional.ofNullable(postUlid).orElseThrow(() -> new IllegalArgumentException("게시글 값이 비어 있습니다."));
        Optional.ofNullable(path).orElseThrow(() -> new IllegalArgumentException("경로 값이 비어 있습니다."));

        if (commentRepository.existsByPostUlidAndPath(postUlid, path)) {
            throw new CommentExistsException();
        }
    }

    public void validateNotFoundConvCommentEntity(String postUlid, String path) {
        Optional.ofNullable(postUlid).orElseThrow(() -> new IllegalArgumentException("게시글 값이 비어 있습니다."));
        Optional.ofNullable(path).orElseThrow(() -> new IllegalArgumentException("경로 값이 비어 있습니다."));

        if (!commentRepository.existsByPostUlidAndPath(postUlid, path)) {
            throw new CommentNotFoundException();
        }
    }
}
