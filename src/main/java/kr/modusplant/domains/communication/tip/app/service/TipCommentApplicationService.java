package kr.modusplant.domains.communication.tip.app.service;

import kr.modusplant.domains.communication.tip.app.http.request.TipCommentInsertRequest;
import kr.modusplant.domains.communication.tip.app.http.response.TipCommentResponse;
import kr.modusplant.domains.communication.tip.domain.service.TipCommentValidationService;
import kr.modusplant.domains.communication.tip.domain.service.TipPostValidationService;
import kr.modusplant.domains.communication.tip.mapper.TipCommentAppInfraMapper;
import kr.modusplant.domains.communication.tip.mapper.TipCommentAppInfraMapperImpl;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCommentEntity;
import kr.modusplant.domains.communication.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.communication.tip.persistence.repository.TipCommentRepository;
import kr.modusplant.domains.communication.tip.persistence.repository.TipPostRepository;
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
public class TipCommentApplicationService {

    private final TipCommentValidationService tipCommentValidationService;
    private final TipPostValidationService tipPostValidationService;
    private final SiteMemberValidationService memberValidationService;
    private final TipCommentAppInfraMapper tipCommentAppInfraMapper = new TipCommentAppInfraMapperImpl();
    private final TipCommentRepository tipCommentRepository;
    private final TipPostRepository tipPostRepository;
    private final SiteMemberRepository memberRepository;

    public List<TipCommentResponse> getAll() {
        return tipCommentRepository.findAll()
                .stream().map(tipCommentAppInfraMapper::toTipCommentResponse).toList();
    }

    public List<TipCommentResponse> getByPostEntity(TipPostEntity requestPostEntity) {
        String ulid = requestPostEntity.getUlid();
        tipPostValidationService.validateNotFoundUlid(ulid);
        TipPostEntity postEntity = tipPostRepository.findByUlid(requestPostEntity.getUlid()).orElseThrow();

        return tipCommentRepository.findByPostEntity(postEntity)
                .stream().map(tipCommentAppInfraMapper::toTipCommentResponse).toList();
    }

    public List<TipCommentResponse> getByAuthMember(SiteMemberEntity authMember) {
        memberValidationService.validateNotFoundUuid(authMember.getUuid());
        SiteMemberEntity memberEntity = memberRepository.findByUuid(authMember.getUuid()).orElseThrow();

        return tipCommentRepository.findByAuthMember(memberEntity)
                .stream().map(tipCommentAppInfraMapper::toTipCommentResponse).toList();
    }

    public List<TipCommentResponse> getByCreateMember(SiteMemberEntity createMember) {
        memberValidationService.validateNotFoundUuid(createMember.getUuid());
        SiteMemberEntity memberEntity = memberRepository.findByUuid(createMember.getUuid()).orElseThrow();

        return tipCommentRepository.findByCreateMember(memberEntity)
                .stream().map(tipCommentAppInfraMapper::toTipCommentResponse).toList();
    }

    public List<TipCommentResponse> getByContent(String content) {
        return tipCommentRepository.findByContent(content)
                .stream().map(tipCommentAppInfraMapper::toTipCommentResponse).toList();
    }

    public Optional<TipCommentResponse> getByPostUlidAndPath(String postUlid, String path) {
        tipPostValidationService.validateNotFoundUlid(postUlid);
        return Optional.of(
                tipCommentAppInfraMapper.toTipCommentResponse(
                        tipCommentRepository.findByPostUlidAndPath(postUlid, path).orElseThrow()
                ));
    }

    @Transactional
    public TipCommentResponse insert(TipCommentInsertRequest commentInsertRequest, UUID memberUuid) {
        String postUlid = commentInsertRequest.postUlid();
        String path = commentInsertRequest.path();
        tipCommentValidationService.validateExistedTipCommentEntity(postUlid, path);
        memberValidationService.validateNotFoundUuid(memberUuid);

        SiteMemberEntity memberEntity = memberRepository.findByUuid(memberUuid).orElseThrow();
        TipCommentEntity commentEntity = TipCommentEntity.builder()
                .postEntity(tipPostRepository.findByUlid(postUlid).orElseThrow())
                .path(path)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .content(commentInsertRequest.content())
                .build();

        return tipCommentAppInfraMapper.toTipCommentResponse(tipCommentRepository.save(commentEntity));
    }

    @Transactional
    public void removeByPostUlidAndPath(String postUlid, String path) {
        tipCommentValidationService.validateNotFoundTipCommentEntity(postUlid, path);
        tipCommentRepository.deleteByPostUlidAndPath(postUlid, path);
    }
}
