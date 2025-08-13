package kr.modusplant.legacy.domains.communication.domain.service;

import kr.modusplant.global.vo.EntityName;
import kr.modusplant.legacy.domains.communication.persistence.repository.CommSecondaryCategoryRepository;
import kr.modusplant.shared.exception.EntityExistsException;
import kr.modusplant.shared.exception.EntityNotFoundException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Primary
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommCategoryValidationService {

    private final CommSecondaryCategoryRepository commCategoryRepository;

    public void validateExistedOrder(Integer order) {
        if (order == null) {
            return;
        }
        if (commCategoryRepository.existsByOrder(order)) {
            throw new EntityExistsException(ErrorCode.CATEGORY_EXISTS, EntityName.CATEGORY);
        }
    }

    public void validateExistedCategory(String category) {
        if (commCategoryRepository.existsByCategory(category)) {
            throw new EntityExistsException(ErrorCode.CATEGORY_EXISTS, EntityName.CATEGORY);
        }
    }

    public void validateNotFoundUuid(UUID uuid) {
        if (uuid == null || !commCategoryRepository.existsByUuid(uuid)) {
            throw new EntityNotFoundException(ErrorCode.CATEGORY_NOT_FOUND, EntityName.CATEGORY);
        }
    }
}