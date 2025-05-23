package kr.modusplant.domains.tip.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import kr.modusplant.domains.group.domain.service.PlantGroupValidationService;
import kr.modusplant.domains.group.persistence.entity.PlantGroupEntity;
import kr.modusplant.domains.group.persistence.repository.PlantGroupRepository;
import kr.modusplant.domains.member.domain.service.SiteMemberValidationService;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.domains.tip.app.http.request.TipPostRequest;
import kr.modusplant.domains.tip.app.http.response.TipPostResponse;
import kr.modusplant.domains.tip.domain.service.TipPostValidationService;
import kr.modusplant.domains.tip.mapper.TipPostAppInfraMapper;
import kr.modusplant.domains.tip.mapper.TipPostAppInfraMapperImpl;
import kr.modusplant.domains.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.tip.persistence.repository.TipPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TipPostApplicationService {

    private final TipPostValidationService tipPostValidationService;
    private final PlantGroupValidationService plantGroupValidationService;
    private final SiteMemberValidationService siteMemberValidationService;
    private final MediaContentService mediaContentService;
    private final TipPostRepository tipPostRepository;
    private final SiteMemberRepository siteMemberRepository;
    private final PlantGroupRepository plantGroupRepository;
    private final TipPostAppInfraMapper tipPostAppInfraMapper = new TipPostAppInfraMapperImpl();


    public Page<TipPostResponse> getAll(Pageable pageable) {
        return tipPostRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc(pageable).map(entity -> {
            try {
                entity.updateContent(mediaContentService.convertFileSrcToBinaryData(entity.getContent()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return tipPostAppInfraMapper.toTipPostResponse(entity);
        });
    }

    public Page<TipPostResponse> getByMemberUuid(UUID memberUuid, Pageable pageable) {
        SiteMemberEntity siteMember = siteMemberRepository.findByUuid(memberUuid).orElseThrow();
        return tipPostRepository.findByAuthMemberAndIsDeletedFalseOrderByCreatedAtDesc(siteMember,pageable).map(entity -> {
            try {
                entity.updateContent(mediaContentService.convertFileSrcToBinaryData(entity.getContent()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return tipPostAppInfraMapper.toTipPostResponse(entity);
        });
    }

    public Page<TipPostResponse> getByGroupOrder(Integer groupOrder, Pageable pageable) {
        PlantGroupEntity plantGroup = plantGroupRepository.findByOrder(groupOrder).orElseThrow();
        return tipPostRepository.findByGroupAndIsDeletedFalseOrderByCreatedAtDesc(plantGroup,pageable).map(entity -> {
            try {
                entity.updateContent(mediaContentService.convertFileSrcToBinaryData(entity.getContent()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return tipPostAppInfraMapper.toTipPostResponse(entity);
        });
    }

    public Page<TipPostResponse> searchByKeyword(String keyword, Pageable pageable) {
        return tipPostRepository.searchByTitleOrContent(keyword,pageable).map(entity -> {
            try {
                entity.updateContent(mediaContentService.convertFileSrcToBinaryData(entity.getContent()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return tipPostAppInfraMapper.toTipPostResponse(entity);
        });
    }

    public Optional<TipPostResponse> getByUlid(String ulid) {
        return tipPostRepository.findByUlid(ulid)
                .map(tipPost -> {
                    try {
                        tipPost.updateContent(mediaContentService.convertFileSrcToBinaryData(tipPost.getContent()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return tipPostAppInfraMapper.toTipPostResponse(tipPost);
                });
    }

    @Transactional
    public void insert(TipPostRequest tipPostRequest, UUID memberUuid) throws IOException {
        tipPostValidationService.validateTipPostInsertRequest(tipPostRequest);
        plantGroupValidationService.validateNotFoundOrder(tipPostRequest.groupOrder());
        siteMemberValidationService.validateNotFoundUuid(memberUuid);
        SiteMemberEntity siteMember = siteMemberRepository.findByUuid(memberUuid).orElseThrow();
        JsonNode content = mediaContentService.saveFilesAndGenerateContentJson(tipPostRequest.content());
        TipPostEntity tipPostEntity = TipPostEntity.builder()
                .group(plantGroupRepository.findByOrder(tipPostRequest.groupOrder()).orElseThrow())
                .authMember(siteMember)
                .createMember(siteMember)
                .title(tipPostRequest.title())
                .content(content)
                .build();
        tipPostRepository.save(tipPostEntity);
    }

    @Transactional
    public void update(TipPostRequest tipPostRequest, String ulid, UUID memberUuid) throws IOException {
        tipPostValidationService.validateTipPostUpdateRequest(tipPostRequest);
        tipPostValidationService.validateAccessibleTipPost(ulid, memberUuid);
        TipPostEntity tipPostEntity = tipPostRepository.findByUlid(ulid).orElseThrow();
        if (tipPostRequest.groupOrder() != null) {
            plantGroupValidationService.validateNotFoundOrder(tipPostRequest.groupOrder());
            tipPostEntity.updateGroup(plantGroupRepository.findByOrder(tipPostRequest.groupOrder()).orElseThrow());
        }
        if (tipPostRequest.title() != null) {
            tipPostEntity.updateTitle(tipPostRequest.title());
        }
        if (tipPostRequest.content() != null && !tipPostRequest.content().isEmpty()) {
            JsonNode content = mediaContentService.saveFilesAndGenerateContentJson(tipPostRequest.content());
            tipPostEntity.updateContent(content);
        }
        tipPostRepository.save(tipPostEntity);
    }

    @Transactional
    public void removeByUlid(String ulid, UUID memberUuid) {
        tipPostValidationService.validateAccessibleTipPost(ulid,memberUuid);
        TipPostEntity tipPostEntity = tipPostRepository.findByUlid(ulid).orElseThrow();
        tipPostEntity.updateIsDeleted(true);
        tipPostRepository.save(tipPostEntity);
    }

}
