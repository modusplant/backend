package kr.modusplant.domains.tip.domain.service;

import kr.modusplant.domains.conversation.persistence.entity.compositekey.ConvCommentCompositeKey;
import kr.modusplant.domains.tip.persistence.entity.compositekey.TipCommentCompositeKey;
import kr.modusplant.domains.tip.persistence.repository.TipCommentRepository;
import kr.modusplant.global.error.EntityExistsWithPostUlidAndMatePathException;
import kr.modusplant.global.error.EntityNotFoundWithPostUlidAndMatePathException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TipCommentValidationService {
    private final TipCommentRepository TipCommentRepository;

    public void validateExistence(String postUlid, String matePath) {
        TipCommentCompositeKey compositeKey = new TipCommentCompositeKey(postUlid, matePath);

        if (TipCommentRepository.findById(compositeKey).isPresent()) {
            throw new EntityExistsWithPostUlidAndMatePathException();
        }
    }

    public void validateNotFoundEntity(String postUlid, String matePath) {
        TipCommentCompositeKey compositeKey = new TipCommentCompositeKey(postUlid, matePath);

        if (postUlid == null || matePath == null ||
            TipCommentRepository.findById(compositeKey).isEmpty()) {
            throw new EntityNotFoundWithPostUlidAndMatePathException();
        }
    }
}
