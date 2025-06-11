package kr.modusplant.domains.communication.tip.domain.service;

import kr.modusplant.domains.communication.common.error.EntityExistsWithPostUlidAndPathException;
import kr.modusplant.domains.communication.common.error.EntityNotFoundWithPostUlidAndPathException;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCommentEntity;
import kr.modusplant.domains.communication.tip.persistence.repository.TipCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TipCommentValidationService {
    private final TipCommentRepository commentRepository;

    public void validateExistedTipCommentEntity(String postUlid, String path) {
        Optional.ofNullable(postUlid).orElseThrow(() -> new IllegalArgumentException("postUlid is null"));
        Optional.ofNullable(path).orElseThrow(() -> new IllegalArgumentException("path is null"));

        if (commentRepository.existsByPostUlidAndPath(postUlid, path)) {
            throw new EntityExistsWithPostUlidAndPathException(postUlid, path, TipCommentEntity.class);
        }
    }

    public void validateNotFoundTipCommentEntity(String postUlid, String path) {
        Optional.ofNullable(postUlid).orElseThrow(() -> new IllegalArgumentException("postUlid is null"));
        Optional.ofNullable(path).orElseThrow(() -> new IllegalArgumentException("path is null"));

        if (!commentRepository.existsByPostUlidAndPath(postUlid, path)) {
            throw new EntityNotFoundWithPostUlidAndPathException(postUlid, path, TipCommentEntity.class);
        }
    }
}
