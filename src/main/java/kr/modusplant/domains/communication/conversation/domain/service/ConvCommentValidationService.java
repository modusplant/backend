package kr.modusplant.domains.communication.conversation.domain.service;

import kr.modusplant.domains.communication.common.error.EntityExistsWithPostUlidAndPathException;
import kr.modusplant.domains.communication.common.error.EntityNotFoundWithPostUlidAndPathException;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCommentEntity;
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
        Optional.ofNullable(postUlid).orElseThrow(() -> new IllegalArgumentException("postUlid is null"));
        Optional.ofNullable(path).orElseThrow(() -> new IllegalArgumentException("path is null"));

        if (commentRepository.existsByPostUlidAndPath(postUlid, path)) {
            throw new EntityExistsWithPostUlidAndPathException(postUlid, path, ConvCommentEntity.class);
        }
    }

    public void validateNotFoundConvCommentEntity(String postUlid, String path) {
        Optional.ofNullable(postUlid).orElseThrow(() -> new IllegalArgumentException("postUlid is null"));
        Optional.ofNullable(path).orElseThrow(() -> new IllegalArgumentException("path is null"));

        if (!commentRepository.existsByPostUlidAndPath(postUlid, path)) {
            throw new EntityNotFoundWithPostUlidAndPathException(postUlid, path, ConvCommentEntity.class);
        }
    }
}
