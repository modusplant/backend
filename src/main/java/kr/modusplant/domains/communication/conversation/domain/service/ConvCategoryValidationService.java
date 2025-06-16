package kr.modusplant.domains.communication.conversation.domain.service;

import kr.modusplant.domains.communication.common.error.CategoryExistsException;
import kr.modusplant.domains.communication.common.error.CategoryNotFoundException;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Primary
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConvCategoryValidationService {

    private final ConvCategoryRepository convCategoryRepository;

    public void validateExistedOrder(Integer order) {
        if (order == null) {
            return;
        }
        if (convCategoryRepository.existsByOrder(order)) {
            throw new CategoryExistsException();
        }
    }

    public void validateExistedCategory(String category) {
        if (convCategoryRepository.existsByCategory(category)) {
            throw new CategoryExistsException();
        }
    }

    public void validateNotFoundUuid(UUID uuid) {
        if (uuid == null || !convCategoryRepository.existsByUuid(uuid)) {
            throw new CategoryNotFoundException();
        }
    }
}