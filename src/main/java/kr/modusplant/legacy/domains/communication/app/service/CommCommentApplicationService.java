package kr.modusplant.legacy.domains.communication.app.service;

import kr.modusplant.framework.out.persistence.jpa.entity.CommCommentEntity;
import kr.modusplant.framework.out.persistence.jpa.entity.CommPostEntity;
import kr.modusplant.framework.out.persistence.jpa.entity.SiteMemberEntity;
import kr.modusplant.framework.out.persistence.jpa.repository.CommCommentRepository;
import kr.modusplant.framework.out.persistence.jpa.repository.CommPostRepository;
import kr.modusplant.framework.out.persistence.jpa.repository.SiteMemberRepository;
import kr.modusplant.legacy.domains.communication.app.http.request.CommCommentInsertRequest;
import kr.modusplant.legacy.domains.communication.app.http.response.CommCommentResponse;
import kr.modusplant.legacy.domains.communication.domain.service.CommCommentValidationService;
import kr.modusplant.legacy.domains.communication.domain.service.CommPostValidationService;
import kr.modusplant.legacy.domains.communication.mapper.CommCommentAppInfraMapper;
import kr.modusplant.legacy.domains.communication.mapper.CommCommentAppInfraMapperImpl;
import kr.modusplant.legacy.domains.member.domain.service.SiteMemberValidationService;
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
public class CommCommentApplicationService {

    private final CommCommentValidationService commCommentValidationService;
    private final CommPostValidationService commPostValidationService;
    private final SiteMemberValidationService memberValidationService;
    private final CommCommentAppInfraMapper commCommentAppInfraMapper = new CommCommentAppInfraMapperImpl();
    private final CommCommentRepository commCommentRepository;
    private final CommPostRepository commPostRepository;
    private final SiteMemberRepository memberRepository;

    public List<CommCommentResponse> getAll() {
        return commCommentRepository.findAll()
                .stream().map(commCommentAppInfraMapper::toCommCommentResponse).toList();
    }

    public List<CommCommentResponse> getByPostEntity(CommPostEntity requestPostEntity) {
        String ulid = requestPostEntity.getUlid();
        commPostValidationService.validateNotFoundUlid(ulid);
        CommPostEntity postEntity = commPostRepository.findByUlid(requestPostEntity.getUlid()).orElseThrow();

        return commCommentRepository.findByPostEntity(postEntity)
                .stream().map(commCommentAppInfraMapper::toCommCommentResponse).toList();
    }

    public List<CommCommentResponse> getByAuthMember(SiteMemberEntity authMember) {
        memberValidationService.validateNotFoundUuid(authMember.getUuid());
        SiteMemberEntity memberEntity = memberRepository.findByUuid(authMember.getUuid()).orElseThrow();

        return commCommentRepository.findByAuthMember(memberEntity)
                .stream().map(commCommentAppInfraMapper::toCommCommentResponse).toList();
    }

    public List<CommCommentResponse> getByCreateMember(SiteMemberEntity createMember) {
        memberValidationService.validateNotFoundUuid(createMember.getUuid());
        SiteMemberEntity memberEntity = memberRepository.findByUuid(createMember.getUuid()).orElseThrow();

        return commCommentRepository.findByCreateMember(memberEntity)
                .stream().map(commCommentAppInfraMapper::toCommCommentResponse).toList();
    }

    public Optional<CommCommentResponse> getByPostUlidAndPath(String postUlid, String path) {
        commPostValidationService.validateNotFoundUlid(postUlid);
        return Optional.of(
                commCommentAppInfraMapper.toCommCommentResponse(
                        commCommentRepository.findByPostUlidAndPath(postUlid, path).orElseThrow()
                ));
    }

    @Transactional
    public CommCommentResponse insert(CommCommentInsertRequest commentInsertRequest, UUID memberUuid) {
        String postUlid = commentInsertRequest.postUlid();
        String path = commentInsertRequest.path();
        commCommentValidationService.validateExistedCommCommentEntity(postUlid, path);
        memberValidationService.validateNotFoundUuid(memberUuid);

        SiteMemberEntity memberEntity = memberRepository.findByUuid(memberUuid).orElseThrow();
        CommCommentEntity commentEntity = CommCommentEntity.builder()
                .postEntity(commPostRepository.findByUlid(postUlid).orElseThrow())
                .path(path)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .content(commentInsertRequest.content())
                .build();

        return commCommentAppInfraMapper.toCommCommentResponse(commCommentRepository.save(commentEntity));
    }

    @Transactional
    public void removeByPostUlidAndPath(String postUlid, String path) {
        commCommentValidationService.validateNotFoundCommCommentEntity(postUlid, path);
        commCommentRepository.deleteByPostUlidAndPath(postUlid, path);
    }
}
