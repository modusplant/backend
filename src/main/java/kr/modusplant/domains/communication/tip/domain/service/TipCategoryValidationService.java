package kr.modusplant.domains.communication.tip.domain.service;

import kr.modusplant.domains.communication.common.error.CategoryExistsException;
import kr.modusplant.domains.communication.common.error.CategoryNotFoundException;
import kr.modusplant.domains.communication.tip.persistence.repository.TipCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Primary
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TipCategoryValidationService {

    private final TipCategoryRepository tipCategoryRepository;

    public void validateExistedOrder(Integer order) {
        if (order == null) {
            return;
        }
        if (tipCategoryRepository.existsByOrder(order)) {
            throw new CategoryExistsException();
        }
    }

    public void validateExistedCategory(String category) {
        if (tipCategoryRepository.existsByCategory(category)) {
            throw new CategoryExistsException();
        }
    }

    public void validateNotFoundUuid(UUID uuid) {
        if (uuid == null || !tipCategoryRepository.existsByUuid(uuid)) {
            throw new CategoryNotFoundException();
        }
    }
}