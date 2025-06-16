package kr.modusplant.domains.communication.conversation.domain.service;

import jakarta.persistence.EntityExistsException;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCategoryEntity;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvCategoryRepository;
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
public class ConvCategoryValidationService {

    private final ConvCategoryRepository convCategoryRepository;

    public void validateExistedOrder(Integer order) {
        if (order == null) {
            return;
        }
        if (convCategoryRepository.existsByOrder(order)) {
            throw new EntityExistsException(getFormattedExceptionMessage(EXISTED_ENTITY, ORDER, order, ConvCategoryEntity.class));
        }
    }

    public void validateExistedCategory(String category) {
        if (convCategoryRepository.existsByCategory(category)) {
            throw new EntityExistsException(getFormattedExceptionMessage(EXISTED_ENTITY, CATEGORY, category, ConvCategoryEntity.class));
        }
    }

    public void validateNotFoundUuid(UUID uuid) {
        if (uuid == null || !convCategoryRepository.existsByUuid(uuid)) {
            throw new EntityNotFoundWithUuidException(uuid, ConvCategoryEntity.class);
        }
    }
}