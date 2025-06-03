package kr.modusplant.domains.communication.conversation.domain.service;

import kr.modusplant.domains.communication.conversation.persistence.repository.ConvCommentRepository;
import kr.modusplant.global.error.EntityExistsWithPostUlidAndMatePathException;
import kr.modusplant.global.error.EntityNotFoundWithPostUlidAndMatePathException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConvCommentValidationService {
    private final ConvCommentRepository commentRepository;

    public void validateFoundConvCommentEntity(String postUlid, String path) {
        Optional.ofNullable(postUlid).orElseThrow(() -> new IllegalArgumentException("postUlid is null"));
        Optional.ofNullable(path).orElseThrow(() -> new IllegalArgumentException("path is null"));

        if (commentRepository.findByPostUlidAndPath(postUlid, path).isPresent()) {
            throw new EntityExistsWithPostUlidAndMatePathException("conv comment entity already exists");
        }
    }

    public void validateNotFoundConvCommentEntity(String postUlid, String path) {
        Optional.ofNullable(postUlid).orElseThrow(() -> new IllegalArgumentException("postUlid is null"));
        Optional.ofNullable(path).orElseThrow(() -> new IllegalArgumentException("path is null"));

        if(commentRepository.findByPostUlidAndPath(postUlid, path).isEmpty()) {
            throw new EntityNotFoundWithPostUlidAndMatePathException("conv comment entity not found");
        }
    }
}
