package kr.modusplant.domains.communication.tip.domain.service;

import jakarta.persistence.EntityExistsException;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCategoryEntity;
import kr.modusplant.domains.communication.tip.persistence.repository.TipCategoryRepository;
import kr.modusplant.global.error.EntityNotFoundWithUuidException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static kr.modusplant.global.enums.ExceptionMessage.EXISTED_ENTITY;
import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;
import static kr.modusplant.global.vo.CamelCaseWord.CATEGORY;
import static kr.modusplant.global.vo.CamelCaseWord.ORDER;

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
            throw new EntityExistsException(getFormattedExceptionMessage(EXISTED_ENTITY.getValue(), ORDER, order, TipCategoryEntity.class));
        }
    }

    public void validateExistedCategory(String category) {
        if (tipCategoryRepository.existsByCategory(category)) {
            throw new EntityExistsException(getFormattedExceptionMessage(EXISTED_ENTITY.getValue(), CATEGORY, category, TipCategoryEntity.class));
        }
    }

    public void validateNotFoundUuid(UUID uuid) {
        if (uuid == null || !tipCategoryRepository.existsByUuid(uuid)) {
            throw new EntityNotFoundWithUuidException(uuid, TipCategoryEntity.class);
        }
    }
}