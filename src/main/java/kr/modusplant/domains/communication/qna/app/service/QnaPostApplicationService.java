package kr.modusplant.domains.communication.qna.app.service;

import kr.modusplant.domains.common.domain.service.MediaContentService;
import kr.modusplant.domains.communication.qna.app.http.request.QnaPostInsertRequest;
import kr.modusplant.domains.communication.qna.app.http.request.QnaPostUpdateRequest;
import kr.modusplant.domains.communication.qna.app.http.response.QnaPostResponse;
import kr.modusplant.domains.communication.qna.domain.service.QnaCategoryValidationService;
import kr.modusplant.domains.communication.qna.domain.service.QnaPostValidationService;
import kr.modusplant.domains.communication.qna.mapper.QnaPostAppInfraMapper;
import kr.modusplant.domains.communication.qna.mapper.QnaPostAppInfraMapperImpl;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCategoryEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaCategoryRepository;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostRepository;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostViewCountRedisRepository;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostViewLockRedisRepository;
import kr.modusplant.domains.member.domain.service.SiteMemberValidationService;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
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
public class QnaPostApplicationService {

    private final QnaPostValidationService qnaPostValidationService;
    private final QnaCategoryValidationService qnaCategoryValidationService;
    private final SiteMemberValidationService siteMemberValidationService;
    private final MediaContentService mediaContentService;
    private final QnaPostRepository qnaPostRepository;
    private final SiteMemberRepository siteMemberRepository;
    private final QnaCategoryRepository qnaCategoryRepository;
    private final QnaPostViewCountRedisRepository qnaPostViewCountRedisRepository;
    private final QnaPostViewLockRedisRepository qnaPostViewLockRedisRepository;
    private final QnaPostAppInfraMapper qnaPostAppInfraMapper = new QnaPostAppInfraMapperImpl();

    @Value("${redis.ttl.view_count}")
    private long ttlMinutes;

    public Page<QnaPostResponse> getAll(Pageable pageable) {
        return qnaPostRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable).map(entity -> {
            try {
                entity.updateContent(mediaContentService.convertFileSrcToBinaryData(entity.getContent()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return qnaPostAppInfraMapper.toQnaPostResponse(entity);
        });
    }

    public Page<QnaPostResponse> getByMemberUuid(UUID memberUuid, Pageable pageable) {
        SiteMemberEntity siteMember = siteMemberRepository.findByUuid(memberUuid).orElseThrow();
        return qnaPostRepository.findByAuthMemberAndIsDeletedFalseOrderByCreatedAtDesc(siteMember, pageable).map(entity -> {
            try {
                entity.updateContent(mediaContentService.convertFileSrcToBinaryData(entity.getContent()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return qnaPostAppInfraMapper.toQnaPostResponse(entity);
        });
    }

    public Page<QnaPostResponse> getByCategoryUuid(UUID categoryUuid, Pageable pageable) {
        QnaCategoryEntity qnaCategory = qnaCategoryRepository.findByUuid(categoryUuid).orElseThrow();
        return qnaPostRepository.findByCategoryAndIsDeletedFalseOrderByCreatedAtDesc(qnaCategory, pageable).map(entity -> {
            try {
                entity.updateContent(mediaContentService.convertFileSrcToBinaryData(entity.getContent()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return qnaPostAppInfraMapper.toQnaPostResponse(entity);
        });
    }

    public Page<QnaPostResponse> searchByKeyword(String keyword, Pageable pageable) {
        return qnaPostRepository.searchByTitleOrContent(keyword, pageable).map(entity -> {
            try {
                entity.updateContent(mediaContentService.convertFileSrcToBinaryData(entity.getContent()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return qnaPostAppInfraMapper.toQnaPostResponse(entity);
        });
    }

    public Optional<QnaPostResponse> getByUlid(String ulid) {
        return qnaPostRepository.findByUlid(ulid)
                .map(qnaPost -> {
                    try {
                        qnaPost.updateContent(mediaContentService.convertFileSrcToBinaryData(qnaPost.getContent()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Optional.ofNullable(qnaPostViewCountRedisRepository.read(ulid))
                            .ifPresent(qnaPost::updateViewCount);
                    return qnaPostAppInfraMapper.toQnaPostResponse(qnaPost);
                });
    }

    @Transactional
    public void insert(QnaPostInsertRequest qnaPostInsertRequest, UUID memberUuid) throws IOException {
        qnaPostValidationService.validateQnaPostInsertRequest(qnaPostInsertRequest);
        qnaCategoryValidationService.validateNotFoundUuid(qnaPostInsertRequest.categoryUuid());
        siteMemberValidationService.validateNotFoundUuid(memberUuid);
        SiteMemberEntity siteMember = siteMemberRepository.findByUuid(memberUuid).orElseThrow();
        QnaPostEntity qnaPostEntity = QnaPostEntity.builder()
                .category(qnaCategoryRepository.findByUuid(qnaPostInsertRequest.categoryUuid()).orElseThrow())
                .authMember(siteMember)
                .createMember(siteMember)
                .title(qnaPostInsertRequest.title())
                .content(mediaContentService.saveFilesAndGenerateContentJson(qnaPostInsertRequest.content()))
                .build();
        qnaPostRepository.save(qnaPostEntity);
    }

    @Transactional
    public void update(QnaPostUpdateRequest qnaPostUpdateRequest, UUID memberUuid) throws IOException {
        qnaPostValidationService.validateQnaPostUpdateRequest(qnaPostUpdateRequest);
        qnaPostValidationService.validateAccessibleQnaPost(qnaPostUpdateRequest.ulid(), memberUuid);
        qnaCategoryValidationService.validateNotFoundUuid(qnaPostUpdateRequest.categoryUuid());
        QnaPostEntity qnaPostEntity = qnaPostRepository.findByUlid(qnaPostUpdateRequest.ulid()).orElseThrow();
        mediaContentService.deleteFiles(qnaPostEntity.getContent());
        qnaPostEntity.updateCategory(qnaCategoryRepository.findByUuid(qnaPostUpdateRequest.categoryUuid()).orElseThrow());
        qnaPostEntity.updateTitle(qnaPostUpdateRequest.title());
        qnaPostEntity.updateContent(mediaContentService.saveFilesAndGenerateContentJson(qnaPostUpdateRequest.content()));
        qnaPostRepository.save(qnaPostEntity);
    }

    @Transactional
    public void removeByUlid(String ulid, UUID memberUuid) throws IOException {
        qnaPostValidationService.validateAccessibleQnaPost(ulid,memberUuid);
        QnaPostEntity qnaPostEntity = qnaPostRepository.findByUlid(ulid).orElseThrow();
        mediaContentService.deleteFiles(qnaPostEntity.getContent());
        qnaPostEntity.updateIsDeleted(true);
        qnaPostRepository.save(qnaPostEntity);
    }

    public Long readViewCount(String ulid) {
        Long redisViewCount = qnaPostViewCountRedisRepository.read(ulid);
        if (redisViewCount != null) {
            return redisViewCount;
        }
        Long dbViewCount = qnaPostRepository.findByUlid(ulid)
                .map(qnaPostEntity -> Optional.ofNullable(qnaPostEntity.getViewCount()).orElseThrow())
                .orElseThrow(() -> new EntityNotFoundWithUlidException(ulid, String.class));
        qnaPostViewCountRedisRepository.write(ulid, dbViewCount);
        return dbViewCount;
    }

    @Transactional
    public Long increaseViewCount(String ulid, UUID memberUuid) {
        // 조회수 어뷰징 정책 - 사용자는 게시글 1개당 ttl에 1번 조회수 증가
        if (!qnaPostViewLockRedisRepository.lock(ulid, memberUuid, ttlMinutes)) {
            return qnaPostViewCountRedisRepository.read(ulid);
        }
        // 조회수 증가
        return qnaPostViewCountRedisRepository.increase(ulid);
    }
}
