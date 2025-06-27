package kr.modusplant.domains.communication.conversation.app.service;

import kr.modusplant.domains.common.app.service.MultipartDataProcessor;
import kr.modusplant.domains.common.enums.PostType;
import kr.modusplant.domains.communication.common.error.PostNotFoundException;
import kr.modusplant.domains.communication.conversation.app.http.request.ConvPostInsertRequest;
import kr.modusplant.domains.communication.conversation.app.http.request.ConvPostUpdateRequest;
import kr.modusplant.domains.communication.conversation.app.http.response.ConvPostResponse;
import kr.modusplant.domains.communication.conversation.domain.service.ConvCategoryValidationService;
import kr.modusplant.domains.communication.conversation.domain.service.ConvPostValidationService;
import kr.modusplant.domains.communication.conversation.mapper.ConvPostAppInfraMapper;
import kr.modusplant.domains.communication.conversation.mapper.ConvPostAppInfraMapperImpl;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCategoryEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvPostEntity;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvCategoryRepository;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvPostRepository;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvPostViewCountRedisRepository;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvPostViewLockRedisRepository;
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
public class ConvPostApplicationService {

    private final ConvPostValidationService convPostValidationService;
    private final ConvCategoryValidationService convCategoryValidationService;
    private final SiteMemberValidationService siteMemberValidationService;
    private final MultipartDataProcessor multipartDataProcessor;
    private final ConvPostRepository convPostRepository;
    private final SiteMemberRepository siteMemberRepository;
    private final ConvCategoryRepository convCategoryRepository;
    private final ConvPostViewCountRedisRepository convPostViewCountRedisRepository;
    private final ConvPostViewLockRedisRepository convPostViewLockRedisRepository;
    private final ConvPostAppInfraMapper convPostAppInfraMapper = new ConvPostAppInfraMapperImpl();

    @Value("${redis.ttl.view_count}")
    private long ttlMinutes;

    public Page<ConvPostResponse> getAll(Pageable pageable) {
        return convPostRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable).map(entity -> {
            try {
                entity.updateContent(multipartDataProcessor.convertFileSrcToBinaryData(entity.getContent()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return convPostAppInfraMapper.toConvPostResponse(entity);
        });
    }

    public Page<ConvPostResponse> getByMemberUuid(UUID memberUuid, Pageable pageable) {
        SiteMemberEntity siteMember = siteMemberRepository.findByUuid(memberUuid).orElseThrow();
        return convPostRepository.findByAuthMemberAndIsDeletedFalseOrderByCreatedAtDesc(siteMember, pageable).map(entity -> {
            try {
                entity.updateContent(multipartDataProcessor.convertFileSrcToBinaryData(entity.getContent()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return convPostAppInfraMapper.toConvPostResponse(entity);
        });
    }

    public Page<ConvPostResponse> getByCategoryUuid(UUID categoryUuid, Pageable pageable) {
        ConvCategoryEntity convCategory = convCategoryRepository.findByUuid(categoryUuid).orElseThrow();
        return convPostRepository.findByCategoryAndIsDeletedFalseOrderByCreatedAtDesc(convCategory, pageable).map(entity -> {
            try {
                entity.updateContent(multipartDataProcessor.convertFileSrcToBinaryData(entity.getContent()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return convPostAppInfraMapper.toConvPostResponse(entity);
        });
    }

    public Page<ConvPostResponse> searchByKeyword(String keyword, Pageable pageable) {
        return convPostRepository.searchByTitleOrContent(keyword, pageable).map(entity -> {
            try {
                entity.updateContent(multipartDataProcessor.convertFileSrcToBinaryData(entity.getContent()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return convPostAppInfraMapper.toConvPostResponse(entity);
        });
    }

    public Optional<ConvPostResponse> getByUlid(String ulid) {
        return convPostRepository.findByUlid(ulid)
                .map(convPost -> {
                    try {
                        convPost.updateContent(multipartDataProcessor.convertFileSrcToBinaryData(convPost.getContent()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Optional.ofNullable(convPostViewCountRedisRepository.read(ulid))
                            .ifPresent(convPost::updateViewCount);
                    return convPostAppInfraMapper.toConvPostResponse(convPost);
                });
    }

    @Transactional
    public void insert(ConvPostInsertRequest convPostInsertRequest, UUID memberUuid) throws IOException {
        convPostValidationService.validateConvPostInsertRequest(convPostInsertRequest);
        convCategoryValidationService.validateNotFoundUuid(convPostInsertRequest.categoryUuid());
        siteMemberValidationService.validateNotFoundUuid(memberUuid);
        SiteMemberEntity siteMember = siteMemberRepository.findByUuid(memberUuid).orElseThrow();
        ConvPostEntity convPostEntity = ConvPostEntity.builder()
                .category(convCategoryRepository.findByUuid(convPostInsertRequest.categoryUuid()).orElseThrow())
                .authMember(siteMember)
                .createMember(siteMember)
                .title(convPostInsertRequest.title())
                .content(multipartDataProcessor.saveFilesAndGenerateContentJson(PostType.CONV_POST,convPostInsertRequest.content()))
                .build();
        convPostRepository.save(convPostEntity);
    }

    @Transactional
    public void update(ConvPostUpdateRequest convPostUpdateRequest, UUID memberUuid) throws IOException {
        convPostValidationService.validateConvPostUpdateRequest(convPostUpdateRequest);
        convPostValidationService.validateAccessibleConvPost(convPostUpdateRequest.ulid(), memberUuid);
        convCategoryValidationService.validateNotFoundUuid(convPostUpdateRequest.categoryUuid());
        ConvPostEntity convPostEntity = convPostRepository.findByUlid(convPostUpdateRequest.ulid()).orElseThrow();
        multipartDataProcessor.deleteFiles(convPostEntity.getContent());
        convPostEntity.updateCategory(convCategoryRepository.findByUuid(convPostUpdateRequest.categoryUuid()).orElseThrow());
        convPostEntity.updateTitle(convPostUpdateRequest.title());
        convPostEntity.updateContent(multipartDataProcessor.saveFilesAndGenerateContentJson(PostType.CONV_POST,convPostUpdateRequest.content()));
        convPostRepository.save(convPostEntity);
    }

    @Transactional
    public void removeByUlid(String ulid, UUID memberUuid) throws IOException {
        convPostValidationService.validateAccessibleConvPost(ulid,memberUuid);
        ConvPostEntity convPostEntity = convPostRepository.findByUlid(ulid).orElseThrow();
        multipartDataProcessor.deleteFiles(convPostEntity.getContent());
        convPostEntity.updateIsDeleted(true);
        convPostRepository.save(convPostEntity);
    }

    public Long readViewCount(String ulid) {
        Long redisViewCount = convPostViewCountRedisRepository.read(ulid);
        if (redisViewCount != null) {
            return redisViewCount;
        }
        Long dbViewCount = convPostRepository.findByUlid(ulid)
                .map(convPostEntity -> Optional.ofNullable(convPostEntity.getViewCount()).orElseThrow())
                .orElseThrow(PostNotFoundException::new);
        convPostViewCountRedisRepository.write(ulid, dbViewCount);
        return dbViewCount;
    }

    @Transactional
    public Long increaseViewCount(String ulid, UUID memberUuid) {
        // 조회수 어뷰징 정책 - 사용자는 게시글 1개당 ttl에 1번 조회수 증가
        if (!convPostViewLockRedisRepository.lock(ulid, memberUuid, ttlMinutes)) {
            return convPostViewCountRedisRepository.read(ulid);
        }
        // 조회수 증가
        return convPostViewCountRedisRepository.increase(ulid);
    }
}
