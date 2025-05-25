package kr.modusplant.domains.conversation.app.service;

import kr.modusplant.domains.common.domain.service.MediaContentService;
import kr.modusplant.domains.group.domain.service.PlantGroupValidationService;
import kr.modusplant.domains.group.persistence.entity.PlantGroupEntity;
import kr.modusplant.domains.group.persistence.repository.PlantGroupRepository;
import kr.modusplant.domains.member.domain.service.SiteMemberValidationService;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.domains.conversation.app.http.request.ConvPostRequest;
import kr.modusplant.domains.conversation.app.http.response.ConvPostResponse;
import kr.modusplant.domains.conversation.domain.service.ConvPostValidationService;
import kr.modusplant.domains.conversation.mapper.ConvPostAppInfraMapper;
import kr.modusplant.domains.conversation.mapper.ConvPostAppInfraMapperImpl;
import kr.modusplant.domains.conversation.persistence.entity.ConvPostEntity;
import kr.modusplant.domains.conversation.persistence.repository.ConvPostViewCountRedisRepository;
import kr.modusplant.domains.conversation.persistence.repository.ConvPostViewLockRedisRepository;
import kr.modusplant.domains.conversation.persistence.repository.ConvPostRepository;
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
public class ConvPostApplicationService {

    private final ConvPostValidationService convPostValidationService;
    private final PlantGroupValidationService plantGroupValidationService;
    private final SiteMemberValidationService siteMemberValidationService;
    private final MediaContentService mediaContentService;
    private final ConvPostRepository convPostRepository;
    private final SiteMemberRepository siteMemberRepository;
    private final PlantGroupRepository plantGroupRepository;
    private final ConvPostViewCountRedisRepository convPostViewCountRedisRepository;
    private final ConvPostViewLockRedisRepository convPostViewLockRedisRepository;
    private final ConvPostAppInfraMapper convPostAppInfraMapper = new ConvPostAppInfraMapperImpl();

    @Value("${REDIS_TTL.VIEW_COUNT}")
    private long ttlMinutes;

    public Page<ConvPostResponse> getAll(Pageable pageable) {
        return convPostRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc(pageable).map(entity -> {
            try {
                entity.updateContent(mediaContentService.convertFileSrcToBinaryData(entity.getContent()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return convPostAppInfraMapper.toConvPostResponse(entity);
        });
    }

    public Page<ConvPostResponse> getByMemberUuid(UUID memberUuid, Pageable pageable) {
        SiteMemberEntity siteMember = siteMemberRepository.findByUuid(memberUuid).orElseThrow();
        return convPostRepository.findByAuthMemberAndIsDeletedFalseOrderByCreatedAtDesc(siteMember,pageable).map(entity -> {
            try {
                entity.updateContent(mediaContentService.convertFileSrcToBinaryData(entity.getContent()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return convPostAppInfraMapper.toConvPostResponse(entity);
        });
    }

    public Page<ConvPostResponse> getByGroupOrder(Integer groupOrder, Pageable pageable) {
        PlantGroupEntity plantGroup = plantGroupRepository.findByOrder(groupOrder).orElseThrow();
        return convPostRepository.findByGroupAndIsDeletedFalseOrderByCreatedAtDesc(plantGroup,pageable).map(entity -> {
            try {
                entity.updateContent(mediaContentService.convertFileSrcToBinaryData(entity.getContent()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return convPostAppInfraMapper.toConvPostResponse(entity);
        });
    }

    public Page<ConvPostResponse> searchByKeyword(String keyword, Pageable pageable) {
        return convPostRepository.searchByTitleOrContent(keyword,pageable).map(entity -> {
            try {
                entity.updateContent(mediaContentService.convertFileSrcToBinaryData(entity.getContent()));
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
                        convPost.updateContent(mediaContentService.convertFileSrcToBinaryData(convPost.getContent()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return convPostAppInfraMapper.toConvPostResponse(convPost);
                });
    }

    @Transactional
    public void insert(ConvPostRequest convPostRequest, UUID memberUuid) throws IOException {
        convPostValidationService.validateConvPostRequest(convPostRequest);
        plantGroupValidationService.validateNotFoundOrder(convPostRequest.groupOrder());
        siteMemberValidationService.validateNotFoundUuid(memberUuid);
        SiteMemberEntity siteMember = siteMemberRepository.findByUuid(memberUuid).orElseThrow();
        ConvPostEntity convPostEntity = ConvPostEntity.builder()
                .group(plantGroupRepository.findByOrder(convPostRequest.groupOrder()).orElseThrow())
                .authMember(siteMember)
                .createMember(siteMember)
                .title(convPostRequest.title())
                .content(mediaContentService.saveFilesAndGenerateContentJson(convPostRequest.content()))
                .build();
        convPostRepository.save(convPostEntity);
    }

    @Transactional
    public void update(ConvPostRequest convPostRequest, String ulid, UUID memberUuid) throws IOException {
        convPostValidationService.validateConvPostRequest(convPostRequest);
        convPostValidationService.validateAccessibleConvPost(ulid, memberUuid);
        plantGroupValidationService.validateNotFoundOrder(convPostRequest.groupOrder());
        ConvPostEntity convPostEntity = convPostRepository.findByUlid(ulid).orElseThrow();
        mediaContentService.deleteFiles(convPostEntity.getContent());
        convPostEntity.updateGroup(plantGroupRepository.findByOrder(convPostRequest.groupOrder()).orElseThrow());
        convPostEntity.updateTitle(convPostRequest.title());
        convPostEntity.updateContent(mediaContentService.saveFilesAndGenerateContentJson(convPostRequest.content()));
        convPostRepository.save(convPostEntity);
    }

    @Transactional
    public void removeByUlid(String ulid, UUID memberUuid) throws IOException {
        convPostValidationService.validateAccessibleConvPost(ulid,memberUuid);
        ConvPostEntity convPostEntity = convPostRepository.findByUlid(ulid).orElseThrow();
        mediaContentService.deleteFiles(convPostEntity.getContent());
        convPostEntity.updateIsDeleted(true);
        convPostRepository.save(convPostEntity);
    }

    public Long countViews(String ulid) {
        Long redisViewCount = convPostViewCountRedisRepository.read(ulid);
        if (redisViewCount != null) {
            return redisViewCount;
        }
        Long dbViewCount = convPostRepository.findByUlid(ulid)
                .map(convPostEntity -> Optional.ofNullable(convPostEntity.getViewCount()).orElse(0L))
                .orElseThrow(() -> new EntityNotFoundWithUlidException(ulid,String.class));
        convPostViewCountRedisRepository.write(ulid, dbViewCount);
        return dbViewCount;
    }

    @Transactional
    public Long increase(String ulid, UUID memberUuid) {
        // 조회수 어뷰징 정책 - 사용자는 게시글 1개당 ttl에 1번 조회수 증가
        if (!convPostViewLockRedisRepository.lock(ulid,memberUuid,ttlMinutes)) {
            return convPostViewCountRedisRepository.read(ulid);
        }
        // 조회수 증가
        return convPostViewCountRedisRepository.increase(ulid);
    }
}
