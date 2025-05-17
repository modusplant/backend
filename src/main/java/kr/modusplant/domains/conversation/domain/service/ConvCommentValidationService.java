package kr.modusplant.domains.conversation.domain.service;

import kr.modusplant.domains.conversation.persistence.entity.compositekey.ConvCommentCompositeKey;
import kr.modusplant.domains.conversation.persistence.repository.ConvCommentRepository;
import kr.modusplant.domains.member.persistence.entity.SiteMemberTermEntity;
import kr.modusplant.global.error.EntityExistsWithPostUlidAndMatePathException;
import kr.modusplant.global.error.EntityNotFoundWithPostUlidAndMatePathException;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConvCommentValidationService {
    private final ConvCommentRepository convCommentRepository;

    public void validateExistence(String postUlid, String matePath) {
        ConvCommentCompositeKey compositeKey = new ConvCommentCompositeKey(postUlid, matePath);

        if (convCommentRepository.findById(compositeKey).isPresent()) {
            throw new EntityExistsWithPostUlidAndMatePathException();
        }
    }

    public void validateNotFoundEntity(String postUlid, String matePath) {
        ConvCommentCompositeKey compositeKey = new ConvCommentCompositeKey(postUlid, matePath);

        if (postUlid == null || matePath == null ||
            convCommentRepository.findById(compositeKey).isEmpty()) {
            throw new EntityNotFoundWithPostUlidAndMatePathException();
        }
    }
}
