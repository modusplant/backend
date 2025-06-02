package kr.modusplant.domains.communication.tip.app.service;

import kr.modusplant.domains.communication.tip.app.http.request.TipCategoryInsertRequest;
import kr.modusplant.domains.communication.tip.app.http.response.TipCategoryResponse;
import kr.modusplant.domains.communication.tip.domain.service.TipCategoryValidationService;
import kr.modusplant.domains.communication.tip.mapper.TipCategoryAppInfraMapper;
import kr.modusplant.domains.communication.tip.mapper.TipCategoryAppInfraMapperImpl;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCategoryEntity;
import kr.modusplant.domains.communication.tip.persistence.repository.TipCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Primary
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TipCategoryApplicationService {

    private final TipCategoryValidationService validationService;
    private final TipCategoryRepository tipCategoryRepository;
    private final TipCategoryAppInfraMapper tipCategoryAppInfraMapper = new TipCategoryAppInfraMapperImpl();

    public List<TipCategoryResponse> getAll() {
        return tipCategoryRepository.findAll().stream().map(tipCategoryAppInfraMapper::toTipCategoryResponse).toList();
    }

    public Optional<TipCategoryResponse> getByUuid(UUID uuid) {
        Optional<TipCategoryEntity> tipCategoryOrEmpty = tipCategoryRepository.findByUuid(uuid);
        return tipCategoryOrEmpty.isEmpty() ? Optional.empty() : Optional.of(tipCategoryAppInfraMapper.toTipCategoryResponse(tipCategoryOrEmpty.orElseThrow()));
    }

    public Optional<TipCategoryResponse> getByOrder(Integer order) {
        Optional<TipCategoryEntity> tipCategoryOrEmpty = tipCategoryRepository.findByOrder(order);
        return tipCategoryOrEmpty.isEmpty() ? Optional.empty() : Optional.of(tipCategoryAppInfraMapper.toTipCategoryResponse(tipCategoryOrEmpty.orElseThrow()));
    }

    public Optional<TipCategoryResponse> getByCategory(String category) {
        Optional<TipCategoryEntity> tipCategoryOrEmpty = tipCategoryRepository.findByCategory(category);
        return tipCategoryOrEmpty.isEmpty() ? Optional.empty() : Optional.of(tipCategoryAppInfraMapper.toTipCategoryResponse(tipCategoryOrEmpty.orElseThrow()));
    }

    @Transactional
    public TipCategoryResponse insert(TipCategoryInsertRequest tipCategoryInsertRequest) {
        validationService.validateExistedCategory(tipCategoryInsertRequest.category());
        validationService.validateExistedOrder(tipCategoryInsertRequest.order());
        return tipCategoryAppInfraMapper.toTipCategoryResponse(tipCategoryRepository.save(tipCategoryAppInfraMapper.toTipCategoryEntity(tipCategoryInsertRequest)));
    }

    @Transactional
    public void removeByUuid(UUID uuid) {
        validationService.validateNotFoundUuid(uuid);
        tipCategoryRepository.deleteByUuid(uuid);
    }
}
