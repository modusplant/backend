package kr.modusplant.domains.tip.app.service;

import kr.modusplant.domains.tip.app.http.request.TipCommentInsertRequest;
import kr.modusplant.domains.tip.app.http.response.TipCommentResponse;
import kr.modusplant.domains.tip.domain.service.TipCommentValidationService;
import kr.modusplant.domains.tip.mapper.TipCommentAppInfraMapper;
import kr.modusplant.domains.tip.persistence.entity.TipCommentEntity;
import kr.modusplant.domains.tip.persistence.entity.compositekey.TipCommentCompositeKey;
import kr.modusplant.domains.tip.persistence.repository.TipCommentRepository;
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
public class TipCommentApplicationService {

    private final TipCommentValidationService tipCommentValidationService;
    private final TipCommentRepository tipCommentRepository;
    private final TipCommentAppInfraMapper tipCommentAppInfraMapper;

    public List<TipCommentResponse> getAll() {
        return tipCommentRepository.findAll()
                .stream().map(tipCommentAppInfraMapper::toTipCommentResponse).toList();
    }

    public List<TipCommentResponse> getByPostUlid(String postUlid) {
        return tipCommentRepository.findBypostUlid(postUlid)
                .stream().map(tipCommentAppInfraMapper::toTipCommentResponse).toList();
    }

    public List<TipCommentResponse> getByAuthMemberUuid(UUID uuid) {
        return tipCommentRepository.findByAuthMemberUuid(uuid)
                .stream().map(tipCommentAppInfraMapper::toTipCommentResponse).toList();
    }

    public List<TipCommentResponse> getByCreateMemberUuid(UUID uuid) {
        return tipCommentRepository.findByAuthMemberUuid(uuid)
                .stream().map(tipCommentAppInfraMapper::toTipCommentResponse).toList();
    }

    public List<TipCommentResponse> getByContent(String content) {
        return tipCommentRepository.findByContent(content)
                .stream().map(tipCommentAppInfraMapper::toTipCommentResponse).toList();
    }

    public Optional<TipCommentResponse> getById(String postUlid, String materializedPath) {
        return Optional.of(
                tipCommentAppInfraMapper.toTipCommentResponse(
                        tipCommentRepository.findById(new TipCommentCompositeKey(postUlid, materializedPath)).orElseThrow()
                ));
    }

    @Transactional
    public TipCommentResponse insert(TipCommentInsertRequest commentInsertRequest) {
        String postUlid = commentInsertRequest.postUlid();
        String materializedPath = commentInsertRequest.materializedPath();
        tipCommentValidationService.validateExistence(postUlid, materializedPath);
        tipCommentValidationService.validateNotFoundEntity(postUlid, materializedPath);

        TipCommentEntity commentEntity = tipCommentAppInfraMapper.toTipCommentEntity(commentInsertRequest);

        return tipCommentAppInfraMapper.toTipCommentResponse(
                tipCommentRepository.save(commentEntity));
    }

    @Transactional
    public void removeById(String postUlid, String materializedPath) {
        tipCommentValidationService.validateExistence(postUlid, materializedPath);
        tipCommentValidationService.validateNotFoundEntity(postUlid, materializedPath);
        tipCommentRepository.deleteById(new TipCommentCompositeKey(postUlid, materializedPath));
    }
}
