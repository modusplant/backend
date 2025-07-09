package kr.modusplant.domains.communication.domain.service;

import kr.modusplant.domains.communication.error.CommunicationExistsException;
import kr.modusplant.domains.communication.error.CommunicationNotFoundException;
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
        if (commentRepository.existsByPostUlidAndPath(postUlid, path)) {
            throw CommunicationExistsException.ofComment();
        }
    }

    public void validateNotFoundCommCommentEntity(String postUlid, String path) {
        if(!commentRepository.existsByPostUlidAndPath(postUlid, path)) {
            throw CommunicationNotFoundException.ofComment();
        }
    }
}
