package kr.modusplant.domains.communication.conversation.app.service;

import kr.modusplant.domains.communication.conversation.app.http.request.ConvCommentInsertRequest;
import kr.modusplant.domains.communication.conversation.app.http.response.ConvCommentResponse;
import kr.modusplant.domains.communication.conversation.domain.service.ConvCommentValidationService;
import kr.modusplant.domains.communication.conversation.domain.service.ConvPostValidationService;
import kr.modusplant.domains.communication.conversation.mapper.ConvCommentAppInfraMapper;
import kr.modusplant.domains.communication.conversation.mapper.ConvCommentAppInfraMapperImpl;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCommentEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvPostEntity;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvCommentRepository;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvPostRepository;
import kr.modusplant.domains.member.domain.service.SiteMemberValidationService;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
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
    private final ConvPostValidationService convPostValidationService;
    private final SiteMemberValidationService memberValidationService;
    private final ConvCommentAppInfraMapper convCommentAppInfraMapper = new ConvCommentAppInfraMapperImpl();
    private final ConvCommentRepository convCommentRepository;
    private final ConvPostRepository convPostRepository;
    private final SiteMemberRepository memberRepository;

    public List<ConvCommentResponse> getAll() {
        return convCommentRepository.findAll()
                .stream().map(convCommentAppInfraMapper::toConvCommentResponse).toList();
    }

    public List<ConvCommentResponse> getByPostEntity(ConvPostEntity requestPostEntity) {
        String ulid = requestPostEntity.getUlid();
        convPostValidationService.validateNotFoundUlid(ulid);
        ConvPostEntity postEntity = convPostRepository.findByUlid(ulid).orElseThrow();

        return convCommentRepository.findByPostEntity(postEntity)
                .stream().map(convCommentAppInfraMapper::toConvCommentResponse).toList();
    }

    public List<ConvCommentResponse> getByAuthMember(SiteMemberEntity authMember) {
        memberValidationService.validateNotFoundUuid(authMember.getUuid());
        SiteMemberEntity memberEntity = memberRepository.findByUuid(authMember.getUuid()).orElseThrow();

        return convCommentRepository.findByAuthMember(memberEntity)
                .stream().map(convCommentAppInfraMapper::toConvCommentResponse).toList();
    }

    public List<ConvCommentResponse> getByCreateMember(SiteMemberEntity createMember) {
        memberValidationService.validateNotFoundUuid(createMember.getUuid());
        SiteMemberEntity memberEntity = memberRepository.findByUuid(createMember.getUuid()).orElseThrow();

        return convCommentRepository.findByCreateMember(memberEntity)
                .stream().map(convCommentAppInfraMapper::toConvCommentResponse).toList();
    }

    public List<ConvCommentResponse> getByContent(String content) {
        return convCommentRepository.findByContent(content)
                .stream().map(convCommentAppInfraMapper::toConvCommentResponse).toList();
    }

    public Optional<ConvCommentResponse> getByPostUlidAndPath(String postUlid, String path) {
        convPostValidationService.validateNotFoundUlid(postUlid);
        return Optional.of(
                convCommentAppInfraMapper.toConvCommentResponse(
                        convCommentRepository.findByPostUlidAndPath(postUlid, path).orElseThrow()
                ));
    }

    @Transactional
    public ConvCommentResponse insert(ConvCommentInsertRequest commentInsertRequest, UUID memberUuid) {
        String postUlid = commentInsertRequest.postUlid();
        String path = commentInsertRequest.path();
        convCommentValidationService.validateExistedConvCommentEntity(postUlid, path);
        memberValidationService.validateNotFoundUuid(memberUuid);

        SiteMemberEntity memberEntity = memberRepository.findByUuid(memberUuid).orElseThrow();
        ConvCommentEntity commentEntity = ConvCommentEntity.builder()
                .postEntity(convPostRepository.findByUlid(postUlid).orElseThrow())
                .path(path)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .content(commentInsertRequest.content())
                .build();

        return convCommentAppInfraMapper.toConvCommentResponse(convCommentRepository.save(commentEntity));
    }

    @Transactional
    public void removeByPostUlidAndPath(String postUlid, String path) {
        convCommentValidationService.validateNotFoundConvCommentEntity(postUlid, path);
        convCommentRepository.deleteByPostUlidAndPath(postUlid, path);
    }
}
