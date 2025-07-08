package kr.modusplant.domains.communication.tip.domain.service;

import kr.modusplant.domains.communication.common.error.CommunicationExistsException;
import kr.modusplant.domains.communication.common.error.CommunicationNotFoundException;
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
        if (commentRepository.existsByPostUlidAndPath(postUlid, path)) {
            throw CommunicationExistsException.ofComment();
        }
    }

    public void validateNotFoundTipCommentEntity(String postUlid, String path) {
        if (!commentRepository.existsByPostUlidAndPath(postUlid, path)) {
            throw CommunicationNotFoundException.ofComment();
        }
    }
}
