package kr.modusplant.domains.communication.tip.domain.service;

import kr.modusplant.domains.communication.common.error.EntityExistsWithPostUlidAndMatePathException;
import kr.modusplant.domains.communication.common.error.EntityNotFoundWithPostUlidAndMatePathException;
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

    public void validateFoundTipCommentEntity(String postUlid, String path) {
        Optional.ofNullable(postUlid).orElseThrow(() -> new IllegalArgumentException("postUlid is null"));
        Optional.ofNullable(path).orElseThrow(() -> new IllegalArgumentException("path is null"));

        if (commentRepository.findByPostUlidAndPath(postUlid, path).isPresent()) {
            throw new EntityExistsWithPostUlidAndMatePathException("tip comment entity already exists");
        }
    }

    public void validateNotFoundTipCommentEntity(String postUlid, String path) {
        Optional.ofNullable(postUlid).orElseThrow(() -> new IllegalArgumentException("postUlid is null"));
        Optional.ofNullable(path).orElseThrow(() -> new IllegalArgumentException("path is null"));

        if(commentRepository.findByPostUlidAndPath(postUlid, path).isEmpty()) {
            throw new EntityNotFoundWithPostUlidAndMatePathException("tip comment entity not found");
        }
    }
}
