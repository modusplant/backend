package kr.modusplant.domains.tip.app.service;

import kr.modusplant.domains.common.domain.service.MediaContentService;
import kr.modusplant.domains.group.domain.service.PlantGroupValidationService;
import kr.modusplant.domains.group.persistence.entity.PlantGroupEntity;
import kr.modusplant.domains.group.persistence.repository.PlantGroupRepository;
import kr.modusplant.domains.member.domain.service.SiteMemberValidationService;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.domains.tip.app.http.request.TipPostInsertRequest;
import kr.modusplant.domains.tip.app.http.request.TipPostUpdateRequest;
import kr.modusplant.domains.tip.app.http.response.TipPostResponse;
import kr.modusplant.domains.tip.domain.service.TipPostValidationService;
import kr.modusplant.domains.tip.mapper.TipPostAppInfraMapper;
import kr.modusplant.domains.tip.mapper.TipPostAppInfraMapperImpl;
import kr.modusplant.domains.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.tip.persistence.repository.TipPostViewCountRedisRepository;
import kr.modusplant.domains.tip.persistence.repository.TipPostViewLockRedisRepository;
import kr.modusplant.domains.tip.persistence.repository.TipPostRepository;
import kr.modusplant.global.error.EntityNotFoundWithUlidException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TipPostApplicationService {

    private final TipPostValidationService tipPostValidationService;
    private final PlantGroupValidationService plantGroupValidationService;
    private final SiteMemberValidationService siteMemberValidationService;
    private final MediaContentService mediaContentService;
    private final TipPostRepository tipPostRepository;
    private final SiteMemberRepository siteMemberRepository;
    private final PlantGroupRepository plantGroupRepository;
    private final TipPostViewCountRedisRepository tipPostViewCountRedisRepository;
    private final TipPostViewLockRedisRepository tipPostViewLockRedisRepository;
    private final TipPostAppInfraMapper tipPostAppInfraMapper = new TipPostAppInfraMapperImpl();

    @Value("${REDIS_TTL.VIEW_COUNT}")
    private long ttlMinutes;

    public Page<TipPostResponse> getAll(Pageable pageable) {
        return tipPostRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable).map(entity -> {
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
    public void insert(TipPostInsertRequest tipPostInsertRequest, UUID memberUuid) throws IOException {
        tipPostValidationService.validateTipPostInsertRequest(tipPostInsertRequest);
        plantGroupValidationService.validateNotFoundOrder(tipPostInsertRequest.groupOrder());
        siteMemberValidationService.validateNotFoundUuid(memberUuid);
        SiteMemberEntity siteMember = siteMemberRepository.findByUuid(memberUuid).orElseThrow();
        TipPostEntity tipPostEntity = TipPostEntity.builder()
                .group(plantGroupRepository.findByOrder(tipPostInsertRequest.groupOrder()).orElseThrow())
                .authMember(siteMember)
                .createMember(siteMember)
                .title(tipPostInsertRequest.title())
                .content(mediaContentService.saveFilesAndGenerateContentJson(tipPostInsertRequest.content()))
                .build();
        tipPostRepository.save(tipPostEntity);
    }

    @Transactional
    public void update(TipPostUpdateRequest tipPostUpdateRequest, UUID memberUuid) throws IOException {
        tipPostValidationService.validateTipPostUpdateRequest(tipPostUpdateRequest);
        tipPostValidationService.validateAccessibleTipPost(tipPostUpdateRequest.ulid(), memberUuid);
        plantGroupValidationService.validateNotFoundOrder(tipPostUpdateRequest.groupOrder());
        TipPostEntity tipPostEntity = tipPostRepository.findByUlid(tipPostUpdateRequest.ulid()).orElseThrow();
        mediaContentService.deleteFiles(tipPostEntity.getContent());
        tipPostEntity.updateGroup(plantGroupRepository.findByOrder(tipPostUpdateRequest.groupOrder()).orElseThrow());
        tipPostEntity.updateTitle(tipPostUpdateRequest.title());
        tipPostEntity.updateContent(mediaContentService.saveFilesAndGenerateContentJson(tipPostUpdateRequest.content()));
        tipPostRepository.save(tipPostEntity);
    }

    @Transactional
    public void removeByUlid(String ulid, UUID memberUuid) throws IOException {
        tipPostValidationService.validateAccessibleTipPost(ulid,memberUuid);
        TipPostEntity tipPostEntity = tipPostRepository.findByUlid(ulid).orElseThrow();
        mediaContentService.deleteFiles(tipPostEntity.getContent());
        tipPostEntity.updateIsDeleted(true);
        tipPostRepository.save(tipPostEntity);
    }

    public Long countViews(String ulid) {
        Long redisViewCount = tipPostViewCountRedisRepository.read(ulid);
        if (redisViewCount != null) {
            return redisViewCount;
        }
        Long dbViewCount = tipPostRepository.findByUlid(ulid)
                .map(tipPostEntity -> Optional.ofNullable(tipPostEntity.getViewCount()).orElse(0L))
                .orElseThrow(() -> new EntityNotFoundWithUlidException(ulid,String.class));
        tipPostViewCountRedisRepository.write(ulid, dbViewCount);
        return dbViewCount;
    }

    @Transactional
    public Long increase(String ulid, UUID memberUuid) {
        // 조회수 어뷰징 정책 - 사용자는 게시글 1개당 ttl에 1번 조회수 증가
        if (!tipPostViewLockRedisRepository.lock(ulid,memberUuid,ttlMinutes)) {
            return tipPostViewCountRedisRepository.read(ulid);
        }
        // 조회수 증가
        return tipPostViewCountRedisRepository.increase(ulid);
    }
}
