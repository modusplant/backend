package kr.modusplant.domains.communication.conversation.app.service;

import kr.modusplant.domains.communication.conversation.app.http.request.ConvCategoryInsertRequest;
import kr.modusplant.domains.communication.conversation.app.http.response.ConvCategoryResponse;
import kr.modusplant.domains.communication.conversation.domain.service.ConvCategoryValidationService;
import kr.modusplant.domains.communication.conversation.mapper.ConvCategoryAppInfraMapper;
import kr.modusplant.domains.communication.conversation.mapper.ConvCategoryAppInfraMapperImpl;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCategoryEntity;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Primary
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConvCategoryApplicationService {

    private final ConvCategoryValidationService validationService;
    private final ConvCategoryRepository convCategoryRepository;
    private final ConvCategoryAppInfraMapper convCategoryAppInfraMapper = new ConvCategoryAppInfraMapperImpl();

    public List<ConvCategoryResponse> getAll() {
        return convCategoryRepository.findAll().stream().map(convCategoryAppInfraMapper::toConvCategoryResponse).toList();
    }
    
    public Optional<ConvCategoryResponse> getByOrder(Integer order) {
        Optional<ConvCategoryEntity> convCategoryOrEmpty = convCategoryRepository.findByOrder(order);
        return convCategoryOrEmpty.isEmpty() ? Optional.empty() : Optional.of(convCategoryAppInfraMapper.toConvCategoryResponse(convCategoryOrEmpty.orElseThrow()));
    }

    public Optional<ConvCategoryResponse> getByCategory(String category) {
        Optional<ConvCategoryEntity> convCategoryOrEmpty = convCategoryRepository.findByCategory(category);
        return convCategoryOrEmpty.isEmpty() ? Optional.empty() : Optional.of(convCategoryAppInfraMapper.toConvCategoryResponse(convCategoryOrEmpty.orElseThrow()));
    }

    @Transactional
    public ConvCategoryResponse insert(ConvCategoryInsertRequest convCategoryInsertRequest) {
        validationService.validateExistedCategory(convCategoryInsertRequest.category());
        return convCategoryAppInfraMapper.toConvCategoryResponse(convCategoryRepository.save(convCategoryAppInfraMapper.toConvCategoryEntity(convCategoryInsertRequest)));
    }

    @Transactional
    public void removeByOrder(Integer order) {
        validationService.validateNotFoundOrder(order);
        convCategoryRepository.deleteByOrder(order);
    }
}
