package kr.modusplant.domains.communication.qna.domain.service;

import kr.modusplant.domains.communication.common.error.CategoryExistsException;
import kr.modusplant.domains.communication.common.error.CategoryNotFoundException;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Primary
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QnaCategoryValidationService {

    private final QnaCategoryRepository qnaCategoryRepository;

    public void validateExistedOrder(Integer order) {
        if (order == null) {
            return;
        }
        if (qnaCategoryRepository.existsByOrder(order)) {
            throw new CategoryExistsException();
        }
    }

    public void validateExistedCategory(String category) {
        if (qnaCategoryRepository.existsByCategory(category)) {
            throw new CategoryExistsException();
        }
    }

    public void validateNotFoundUuid(UUID uuid) {
        if (uuid == null || !qnaCategoryRepository.existsByUuid(uuid)) {
            throw new CategoryNotFoundException();
        }
    }
}