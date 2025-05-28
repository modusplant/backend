package kr.modusplant.domains.communication.tip.domain.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCategoryEntity;
import kr.modusplant.domains.communication.tip.persistence.repository.TipCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.modusplant.global.enums.ExceptionMessage.EXISTED_ENTITY;
import static kr.modusplant.global.enums.ExceptionMessage.NOT_FOUND_ENTITY;
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
        if (tipCategoryRepository.findByOrder(order).isPresent()) {
            throw new EntityExistsException(getFormattedExceptionMessage(EXISTED_ENTITY.getValue(), ORDER, order, TipCategoryEntity.class));
        }
    }

    public void validateExistedCategory(String category) {
        if (tipCategoryRepository.findByCategory(category).isPresent()) {
            throw new EntityExistsException(getFormattedExceptionMessage(EXISTED_ENTITY.getValue(), CATEGORY, category, TipCategoryEntity.class));
        }
    }

    public void validateNotFoundOrder(Integer order) {
        if (order == null || tipCategoryRepository.findByOrder(order).isEmpty()) {
            throw new EntityNotFoundException(getFormattedExceptionMessage(NOT_FOUND_ENTITY.getValue(), ORDER, order, TipCategoryEntity.class));
        }
    }
}