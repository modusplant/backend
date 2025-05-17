package kr.modusplant.domains.conversation.app.service;

import kr.modusplant.domains.conversation.app.http.request.ConvCommentInsertRequest;
import kr.modusplant.domains.conversation.app.http.response.ConvCommentResponse;
import kr.modusplant.domains.conversation.domain.model.ConvComment;
import kr.modusplant.domains.conversation.domain.service.ConvCommentValidationService;
import kr.modusplant.domains.conversation.mapper.ConvCommentAppInfraMapper;
import kr.modusplant.domains.conversation.persistence.entity.ConvCommentEntity;
import kr.modusplant.domains.conversation.persistence.entity.ConvPostEntity;
import kr.modusplant.domains.conversation.persistence.entity.compositekey.ConvCommentCompositeKey;
import kr.modusplant.domains.conversation.persistence.repository.ConvCommentRepository;
import kr.modusplant.domains.member.app.http.request.SiteMemberTermInsertRequest;
import kr.modusplant.domains.member.app.http.request.SiteMemberTermUpdateRequest;
import kr.modusplant.domains.member.app.http.response.SiteMemberResponse;
import kr.modusplant.domains.member.app.http.response.SiteMemberTermResponse;
import kr.modusplant.domains.member.app.service.SiteMemberApplicationService;
import kr.modusplant.domains.member.persistence.entity.SiteMemberTermEntity;
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
public class ConvCommentApplicationService {

    private final ConvCommentValidationService convCommentValidationService;
    private final ConvCommentRepository convCommentRepository;
    private final ConvCommentAppInfraMapper convCommentAppInfraMapper;

    public List<ConvCommentResponse> getAll() {
        return convCommentRepository.findAll()
                .stream().map(convCommentAppInfraMapper::toConvCommentResponse).toList();
    }

    public List<ConvCommentResponse> getByPostUlid(String postUlid) {
        return convCommentRepository.findBypostUlid(postUlid)
                .stream().map(convCommentAppInfraMapper::toConvCommentResponse).toList();
    }

    public List<ConvCommentResponse> getByAuthMemberUuid(UUID uuid) {
        return convCommentRepository.findByAuthMemberUuid(uuid)
                .stream().map(convCommentAppInfraMapper::toConvCommentResponse).toList();
    }

    public List<ConvCommentResponse> getByCreateMemberUuid(UUID uuid) {
        return convCommentRepository.findByAuthMemberUuid(uuid)
                .stream().map(convCommentAppInfraMapper::toConvCommentResponse).toList();
    }

    public List<ConvCommentResponse> getByContent(String content) {
        return convCommentRepository.findByContent(content)
                .stream().map(convCommentAppInfraMapper::toConvCommentResponse).toList();
    }

    public Optional<ConvCommentResponse> getById(String postUlid, String materializedPath) {
        return Optional.of(
                convCommentAppInfraMapper.toConvCommentResponse(
                        convCommentRepository.findById(new ConvCommentCompositeKey(postUlid, materializedPath)).orElseThrow()
                ));
    }

    @Transactional
    public ConvCommentResponse insert(ConvCommentInsertRequest commentInsertRequest) {
        String postUlid = commentInsertRequest.postUlid();
        String materializedPath = commentInsertRequest.materializedPath();
        convCommentValidationService.validateExistence(postUlid, materializedPath);
        convCommentValidationService.validateNotFoundEntity(postUlid, materializedPath);

        ConvCommentEntity commentEntity = convCommentAppInfraMapper.toConvCommentEntity(commentInsertRequest);

        return convCommentAppInfraMapper.toConvCommentResponse(
                convCommentRepository.save(commentEntity));
    }

    @Transactional
    public void removeById(String postUlid, String materializedPath) {
        convCommentValidationService.validateExistence(postUlid, materializedPath);
        convCommentValidationService.validateNotFoundEntity(postUlid, materializedPath);
        convCommentRepository.deleteById(new ConvCommentCompositeKey(postUlid, materializedPath));
    }
}
