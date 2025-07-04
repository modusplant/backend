package kr.modusplant.domains.communication.tip.app.service;

import kr.modusplant.domains.common.app.service.MultipartDataProcessor;
import kr.modusplant.domains.common.enums.PostType;
import kr.modusplant.domains.communication.common.error.CommunicationNotFoundException;
import kr.modusplant.domains.communication.tip.app.http.request.TipPostInsertRequest;
import kr.modusplant.domains.communication.tip.app.http.request.TipPostUpdateRequest;
import kr.modusplant.domains.communication.tip.app.http.response.TipPostResponse;
import kr.modusplant.domains.communication.tip.domain.service.TipCategoryValidationService;
import kr.modusplant.domains.communication.tip.domain.service.TipPostValidationService;
import kr.modusplant.domains.communication.tip.mapper.TipPostAppInfraMapper;
import kr.modusplant.domains.communication.tip.mapper.TipPostAppInfraMapperImpl;
import kr.modusplant.domains.communication.tip.persistence.entity.TipCategoryEntity;
import kr.modusplant.domains.communication.tip.persistence.entity.TipPostEntity;
import kr.modusplant.domains.communication.tip.persistence.repository.TipCategoryRepository;
import kr.modusplant.domains.communication.tip.persistence.repository.TipPostRepository;
import kr.modusplant.domains.communication.tip.persistence.repository.TipPostViewCountRedisRepository;
import kr.modusplant.domains.communication.tip.persistence.repository.TipPostViewLockRedisRepository;
import kr.modusplant.domains.member.domain.service.SiteMemberValidationService;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
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
    private final TipCategoryValidationService tipCategoryValidationService;
    private final SiteMemberValidationService siteMemberValidationService;
    private final MultipartDataProcessor multipartDataProcessor;
    private final TipPostRepository tipPostRepository;
    private final SiteMemberRepository siteMemberRepository;
    private final TipCategoryRepository tipCategoryRepository;
    private final TipPostViewCountRedisRepository tipPostViewCountRedisRepository;
    private final TipPostViewLockRedisRepository tipPostViewLockRedisRepository;
    private final TipPostAppInfraMapper tipPostAppInfraMapper = new TipPostAppInfraMapperImpl();

    @Value("${redis.ttl.view_count}")
    private long ttlMinutes;

    public Page<TipPostResponse> getAll(Pageable pageable) {
        return tipPostRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable).map(entity -> {
            try {
                entity.updateContent(multipartDataProcessor.convertFileSrcToBinaryData(entity.getContent()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return tipPostAppInfraMapper.toTipPostResponse(entity);
        });
    }

    public Page<TipPostResponse> getByMemberUuid(UUID memberUuid, Pageable pageable) {
        SiteMemberEntity siteMember = siteMemberRepository.findByUuid(memberUuid).orElseThrow();
        return tipPostRepository.findByAuthMemberAndIsDeletedFalseOrderByCreatedAtDesc(siteMember, pageable).map(entity -> {
            try {
                entity.updateContent(multipartDataProcessor.convertFileSrcToBinaryData(entity.getContent()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return tipPostAppInfraMapper.toTipPostResponse(entity);
        });
    }

    public Page<TipPostResponse> getByCategoryUuid(UUID categoryUuid, Pageable pageable) {
        TipCategoryEntity tipCategory = tipCategoryRepository.findByUuid(categoryUuid).orElseThrow();
        return tipPostRepository.findByCategoryAndIsDeletedFalseOrderByCreatedAtDesc(tipCategory, pageable).map(entity -> {
            try {
                entity.updateContent(multipartDataProcessor.convertFileSrcToBinaryData(entity.getContent()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return tipPostAppInfraMapper.toTipPostResponse(entity);
        });
    }

    public Page<TipPostResponse> searchByKeyword(String keyword, Pageable pageable) {
        return tipPostRepository.searchByTitleOrContent(keyword, pageable).map(entity -> {
            try {
                entity.updateContent(multipartDataProcessor.convertFileSrcToBinaryData(entity.getContent()));
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
                        tipPost.updateContent(multipartDataProcessor.convertFileSrcToBinaryData(tipPost.getContent()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Optional.ofNullable(tipPostViewCountRedisRepository.read(ulid))
                            .ifPresent(tipPost::updateViewCount);
                    return tipPostAppInfraMapper.toTipPostResponse(tipPost);
                });
    }

    @Transactional
    public void insert(TipPostInsertRequest tipPostInsertRequest, UUID memberUuid) throws IOException {
        tipPostValidationService.validateTipPostInsertRequest(tipPostInsertRequest);
        tipCategoryValidationService.validateNotFoundUuid(tipPostInsertRequest.categoryUuid());
        siteMemberValidationService.validateNotFoundUuid(memberUuid);
        SiteMemberEntity siteMember = siteMemberRepository.findByUuid(memberUuid).orElseThrow();
        TipPostEntity tipPostEntity = TipPostEntity.builder()
                .category(tipCategoryRepository.findByUuid(tipPostInsertRequest.categoryUuid()).orElseThrow())
                .authMember(siteMember)
                .createMember(siteMember)
                .title(tipPostInsertRequest.title())
                .content(multipartDataProcessor.saveFilesAndGenerateContentJson(PostType.TIP_POST,tipPostInsertRequest.content()))
                .build();
        tipPostRepository.save(tipPostEntity);
    }

    @Transactional
    public void update(TipPostUpdateRequest tipPostUpdateRequest, UUID memberUuid) throws IOException {
        tipPostValidationService.validateTipPostUpdateRequest(tipPostUpdateRequest);
        tipPostValidationService.validateAccessibleTipPost(tipPostUpdateRequest.ulid(), memberUuid);
        tipCategoryValidationService.validateNotFoundUuid(tipPostUpdateRequest.categoryUuid());
        TipPostEntity tipPostEntity = tipPostRepository.findByUlid(tipPostUpdateRequest.ulid()).orElseThrow();
        multipartDataProcessor.deleteFiles(tipPostEntity.getContent());
        tipPostEntity.updateCategory(tipCategoryRepository.findByUuid(tipPostUpdateRequest.categoryUuid()).orElseThrow());
        tipPostEntity.updateTitle(tipPostUpdateRequest.title());
        tipPostEntity.updateContent(multipartDataProcessor.saveFilesAndGenerateContentJson(PostType.TIP_POST,tipPostUpdateRequest.content()));
        tipPostRepository.save(tipPostEntity);
    }

    @Transactional
    public void removeByUlid(String ulid, UUID memberUuid) throws IOException {
        tipPostValidationService.validateAccessibleTipPost(ulid,memberUuid);
        TipPostEntity tipPostEntity = tipPostRepository.findByUlid(ulid).orElseThrow();
        multipartDataProcessor.deleteFiles(tipPostEntity.getContent());
        tipPostEntity.updateIsDeleted(true);
        tipPostRepository.save(tipPostEntity);
    }

    public Long readViewCount(String ulid) {
        Long redisViewCount = tipPostViewCountRedisRepository.read(ulid);
        if (redisViewCount != null) {
            return redisViewCount;
        }
        Long dbViewCount = tipPostRepository.findByUlid(ulid)
                .map(tipPostEntity -> Optional.ofNullable(tipPostEntity.getViewCount()).orElseThrow())
                .orElseThrow(CommunicationNotFoundException::ofPost);
        tipPostViewCountRedisRepository.write(ulid, dbViewCount);
        return dbViewCount;
    }

    @Transactional
    public Long increaseViewCount(String ulid, UUID memberUuid) {
        // 조회수 어뷰징 정책 - 사용자는 게시글 1개당 ttl에 1번 조회수 증가
        if (!tipPostViewLockRedisRepository.lock(ulid, memberUuid, ttlMinutes)) {
            return tipPostViewCountRedisRepository.read(ulid);
        }
        // 조회수 증가
        return tipPostViewCountRedisRepository.increase(ulid);
    }
}
