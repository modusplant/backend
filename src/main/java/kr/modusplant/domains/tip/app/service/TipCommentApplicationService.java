package kr.modusplant.domains.tip.app.service;

import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.domains.tip.app.http.request.TipCommentInsertRequest;
import kr.modusplant.domains.tip.app.http.response.TipCommentResponse;
import kr.modusplant.domains.tip.domain.service.TipCommentValidationService;
import kr.modusplant.domains.tip.mapper.TipCommentAppInfraMapper;
import kr.modusplant.domains.tip.mapper.TipCommentAppInfraMapperImpl;
import kr.modusplant.domains.tip.persistence.entity.TipCommentEntity;
import kr.modusplant.domains.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.tip.persistence.repository.TipCommentRepository;
import kr.modusplant.domains.tip.persistence.repository.TipPostRepository;
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
public class TipCommentApplicationService {

    private final TipCommentValidationService tipCommentValidationService;
    private final TipCommentAppInfraMapper tipCommentAppInfraMapper = new TipCommentAppInfraMapperImpl();
    private final TipCommentRepository tipCommentRepository;
    private final TipPostRepository tipPostRepository;
    private final SiteMemberRepository memberRepository;

    public List<TipCommentResponse> getAll() {
        return tipCommentRepository.findAll()
                .stream().map(tipCommentAppInfraMapper::toTipCommentResponse).toList();
    }

    public List<TipCommentResponse> getByPostEntity(TipPostEntity requestPostEntity) {
        TipPostEntity postEntity = tipPostRepository.findByUlid(requestPostEntity.getUlid()).orElseThrow();

        return tipCommentRepository.findByPostEntity(postEntity)
                .stream().map(tipCommentAppInfraMapper::toTipCommentResponse).toList();
    }

    public List<TipCommentResponse> getByAuthMember(SiteMemberEntity authMember) {
        SiteMemberEntity memberEntity = memberRepository.findByUuid(authMember.getUuid()).orElseThrow();

        return tipCommentRepository.findByAuthMember(memberEntity)
                .stream().map(tipCommentAppInfraMapper::toTipCommentResponse).toList();
    }

    public List<TipCommentResponse> getByCreateMember(SiteMemberEntity createMember) {
        SiteMemberEntity memberEntity = memberRepository.findByUuid(createMember.getUuid()).orElseThrow();

        return tipCommentRepository.findByCreateMember(memberEntity)
                .stream().map(tipCommentAppInfraMapper::toTipCommentResponse).toList();
    }

    public List<TipCommentResponse> getByContent(String content) {
        return tipCommentRepository.findByContent(content)
                .stream().map(tipCommentAppInfraMapper::toTipCommentResponse).toList();
    }

    public Optional<TipCommentResponse> getByPostUlidAndMaterializedPath(String postUlid, String materializedPath) {
        return Optional.of(
                tipCommentAppInfraMapper.toTipCommentResponse(
                        tipCommentRepository.findByPostUlidAndMaterializedPath(postUlid, materializedPath).orElseThrow()
                ));
    }

    @Transactional
    public TipCommentResponse insert(TipCommentInsertRequest commentInsertRequest) {
        String postUlid = commentInsertRequest.postUlid();
        String materializedPath = commentInsertRequest.materializedPath();
        tipCommentValidationService.validateFoundTipCommentEntity(postUlid, materializedPath);

        TipCommentEntity commentEntity =
                tipCommentAppInfraMapper.toTipCommentEntity(commentInsertRequest, tipPostRepository, memberRepository);

        return tipCommentAppInfraMapper.toTipCommentResponse(
                tipCommentRepository.save(commentEntity));
    }

    @Transactional
    public void removeByPostUlidAndMaterializedPath(String postUlid, String materializedPath) {
        tipCommentValidationService.validateNotFoundTipCommentEntity(postUlid, materializedPath);
        tipCommentRepository.deleteByPostUlidAndMaterializedPath(postUlid, materializedPath);
    }
}
