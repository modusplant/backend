package kr.modusplant.domains.group.domain.service;

import jakarta.persistence.EntityNotFoundException;
import kr.modusplant.domains.group.persistence.entity.PlantGroupEntity;
import kr.modusplant.domains.group.persistence.repository.PlantGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.modusplant.global.enums.ExceptionMessage.NOT_FOUND_ENTITY;
import static kr.modusplant.global.util.ExceptionUtils.getFormattedExceptionMessage;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlantGroupValidationService {

    private final PlantGroupRepository plantGroupRepository;

    public void validateNotFoundOrder(Integer order) {
        if (order == null || !plantGroupRepository.existsByOrder(order)) {
            throw new EntityNotFoundException(getFormattedExceptionMessage(NOT_FOUND_ENTITY.getValue(),"order",order,PlantGroupEntity.class));
        }
    }
}
