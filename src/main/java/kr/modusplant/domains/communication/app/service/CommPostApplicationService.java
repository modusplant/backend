package kr.modusplant.domains.communication.app.service;

import kr.modusplant.domains.common.app.service.MultipartDataProcessor;
import kr.modusplant.domains.communication.app.http.request.CommPostInsertRequest;
import kr.modusplant.domains.communication.app.http.request.CommPostUpdateRequest;
import kr.modusplant.domains.communication.app.http.response.CommPostResponse;
import kr.modusplant.domains.communication.domain.service.CommCategoryValidationService;
import kr.modusplant.domains.communication.domain.service.CommPostValidationService;
import kr.modusplant.domains.communication.error.PostNotFoundException;
import kr.modusplant.domains.communication.mapper.CommPostAppInfraMapper;
import kr.modusplant.domains.communication.mapper.CommPostAppInfraMapperImpl;
import kr.modusplant.domains.communication.persistence.entity.CommPostEntity;
import kr.modusplant.domains.communication.persistence.entity.CommSecondaryCategoryEntity;
import kr.modusplant.domains.communication.persistence.repository.CommPostRepository;
import kr.modusplant.domains.communication.persistence.repository.CommPostViewCountRedisRepository;
import kr.modusplant.domains.communication.persistence.repository.CommPostViewLockRedisRepository;
import kr.modusplant.domains.communication.persistence.repository.CommSecondaryCategoryRepository;
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
public class CommPostApplicationService {

    private final CommPostValidationService commPostValidationService;
    private final CommCategoryValidationService commCategoryValidationService;
    private final SiteMemberValidationService siteMemberValidationService;
    private final MultipartDataProcessor multipartDataProcessor;
    private final CommPostRepository commPostRepository;
    private final SiteMemberRepository siteMemberRepository;
    private final CommSecondaryCategoryRepository commCategoryRepository;
    private final CommPostViewCountRedisRepository commPostViewCountRedisRepository;
    private final CommPostViewLockRedisRepository commPostViewLockRedisRepository;
    private final CommPostAppInfraMapper commPostAppInfraMapper = new CommPostAppInfraMapperImpl();

    @Value("${redis.ttl.view_count}")
    private long ttlMinutes;

    public Page<CommPostResponse> getAll(Pageable pageable) {
        return commPostRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable).map(entity -> {
            try {
                entity.updateContent(multipartDataProcessor.convertFileSrcToBinaryData(entity.getContent()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return commPostAppInfraMapper.toCommPostResponse(entity);
        });
    }

    public Page<CommPostResponse> getByMemberUuid(UUID memberUuid, Pageable pageable) {
        SiteMemberEntity siteMember = siteMemberRepository.findByUuid(memberUuid).orElseThrow();
        return commPostRepository.findByAuthMemberAndIsDeletedFalseOrderByCreatedAtDesc(siteMember, pageable).map(entity -> {
            try {
                entity.updateContent(multipartDataProcessor.convertFileSrcToBinaryData(entity.getContent()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return commPostAppInfraMapper.toCommPostResponse(entity);
        });
    }

    public Page<CommPostResponse> getByCategoryUuid(UUID categoryUuid, Pageable pageable) {
        CommSecondaryCategoryEntity commCategory = commCategoryRepository.findByUuid(categoryUuid).orElseThrow();
        return commPostRepository.findByCategoryAndIsDeletedFalseOrderByCreatedAtDesc(commCategory, pageable).map(entity -> {
            try {
                entity.updateContent(multipartDataProcessor.convertFileSrcToBinaryData(entity.getContent()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return commPostAppInfraMapper.toCommPostResponse(entity);
        });
    }

    public Page<CommPostResponse> searchByKeyword(String keyword, Pageable pageable) {
        return commPostRepository.searchByTitleOrContent(keyword, pageable).map(entity -> {
            try {
                entity.updateContent(multipartDataProcessor.convertFileSrcToBinaryData(entity.getContent()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return commPostAppInfraMapper.toCommPostResponse(entity);
        });
    }

    public Optional<CommPostResponse> getByUlid(String ulid) {
        return commPostRepository.findByUlid(ulid)
                .map(commPost -> {
                    try {
                        commPost.updateContent(multipartDataProcessor.convertFileSrcToBinaryData(commPost.getContent()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Optional.ofNullable(commPostViewCountRedisRepository.read(ulid))
                            .ifPresent(commPost::updateViewCount);
                    return commPostAppInfraMapper.toCommPostResponse(commPost);
                });
    }

    @Transactional
    public void insert(CommPostInsertRequest commPostInsertRequest, UUID memberUuid) throws IOException {
        commPostValidationService.validateCommPostInsertRequest(commPostInsertRequest);
        commCategoryValidationService.validateNotFoundUuid(commPostInsertRequest.categoryUuid());
        siteMemberValidationService.validateNotFoundUuid(memberUuid);
        SiteMemberEntity siteMember = siteMemberRepository.findByUuid(memberUuid).orElseThrow();
        CommPostEntity commPostEntity = CommPostEntity.builder()
                .category(commCategoryRepository.findByUuid(commPostInsertRequest.categoryUuid()).orElseThrow())
                .authMember(siteMember)
                .createMember(siteMember)
                .title(commPostInsertRequest.title())
                .content(multipartDataProcessor.saveFilesAndGenerateContentJson(commPostInsertRequest.content()))
                .build();
        commPostRepository.save(commPostEntity);
    }

    @Transactional
    public void update(CommPostUpdateRequest commPostUpdateRequest, UUID memberUuid) throws IOException {
        commPostValidationService.validateCommPostUpdateRequest(commPostUpdateRequest);
        commPostValidationService.validateAccessibleCommPost(commPostUpdateRequest.ulid(), memberUuid);
        commCategoryValidationService.validateNotFoundUuid(commPostUpdateRequest.categoryUuid());
        CommPostEntity commPostEntity = commPostRepository.findByUlid(commPostUpdateRequest.ulid()).orElseThrow();
        multipartDataProcessor.deleteFiles(commPostEntity.getContent());
        commPostEntity.updateCategory(commCategoryRepository.findByUuid(commPostUpdateRequest.categoryUuid()).orElseThrow());
        commPostEntity.updateTitle(commPostUpdateRequest.title());
        commPostEntity.updateContent(multipartDataProcessor.saveFilesAndGenerateContentJson(commPostUpdateRequest.content()));
        commPostRepository.save(commPostEntity);
    }

    @Transactional
    public void removeByUlid(String ulid, UUID memberUuid) {
        commPostValidationService.validateAccessibleCommPost(ulid,memberUuid);
        CommPostEntity commPostEntity = commPostRepository.findByUlid(ulid).orElseThrow();
        multipartDataProcessor.deleteFiles(commPostEntity.getContent());
        commPostEntity.updateIsDeleted(true);
        commPostRepository.save(commPostEntity);
    }

    public Long readViewCount(String ulid) {
        Long redisViewCount = commPostViewCountRedisRepository.read(ulid);
        if (redisViewCount != null) {
            return redisViewCount;
        }
        Long dbViewCount = commPostRepository.findByUlid(ulid)
                .map(commPostEntity -> Optional.ofNullable(commPostEntity.getViewCount()).orElseThrow())
                .orElseThrow(PostNotFoundException::new);
        commPostViewCountRedisRepository.write(ulid, dbViewCount);
        return dbViewCount;
    }

    @Transactional
    public Long increaseViewCount(String ulid, UUID memberUuid) {
        // 조회수 어뷰징 정책 - 사용자는 게시글 1개당 ttl에 1번 조회수 증가
        if (!commPostViewLockRedisRepository.lock(ulid, memberUuid, ttlMinutes)) {
            return commPostViewCountRedisRepository.read(ulid);
        }
        // 조회수 증가
        return commPostViewCountRedisRepository.increase(ulid);
    }
}
