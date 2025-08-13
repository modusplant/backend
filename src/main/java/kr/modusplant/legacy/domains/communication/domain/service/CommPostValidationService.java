package kr.modusplant.legacy.domains.communication.domain.service;

import kr.modusplant.global.vo.EntityName;
import kr.modusplant.legacy.domains.common.error.DataPairNumberMismatchException;
import kr.modusplant.legacy.domains.common.error.DataPairOrderMismatchException;
import kr.modusplant.legacy.domains.common.error.EmptyValueException;
import kr.modusplant.legacy.domains.common.persistence.repository.supers.UuidPrimaryKeyRepository;
import kr.modusplant.legacy.domains.communication.app.http.request.CommPostInsertRequest;
import kr.modusplant.legacy.domains.communication.app.http.request.CommPostUpdateRequest;
import kr.modusplant.legacy.domains.communication.app.http.request.FileOrder;
import kr.modusplant.legacy.domains.communication.error.AccessDeniedException;
import kr.modusplant.legacy.domains.communication.persistence.entity.CommPostEntity;
import kr.modusplant.legacy.domains.communication.persistence.repository.CommPostRepository;
import kr.modusplant.legacy.domains.communication.persistence.repository.CommSecondaryCategoryRepository;
import kr.modusplant.shared.exception.EntityNotFoundException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommPostValidationService {

    private final CommPostRepository commPostRepository;
    private final CommSecondaryCategoryRepository commCategoryRepository;

    public void validateCommPostInsertRequest(CommPostInsertRequest request) {
        validateNotFoundCategoryUuid(request.primaryCategoryUuid(), commCategoryRepository);
        validateContentAndOrderInfo(request.content(),request.orderInfo());
    }

    public void validateCommPostUpdateRequest(CommPostUpdateRequest request) {
        validateNotFoundCategoryUuid(request.primaryCategoryUuid(), commCategoryRepository);
        validateContentAndOrderInfo(request.content(),request.orderInfo());
    }

    public void validateAccessibleCommPost(String ulid, UUID memberUuid) {
        CommPostEntity commPost = findIfExistsByUlid(ulid);
        validateMemberHasPostAccess(commPost,memberUuid);
    }

    public void validateNotFoundUlid(String ulid) {
        if (ulid == null || !commPostRepository.existsByUlid(ulid)) {
            throw new EntityNotFoundException(ErrorCode.POST_NOT_FOUND, EntityName.POST);
        }
    }

    private CommPostEntity findIfExistsByUlid(String ulid) {
        if (ulid == null) {
            throw new EntityNotFoundException(ErrorCode.POST_NOT_FOUND, EntityName.POST);
        }
        return commPostRepository.findByUlidAndIsDeletedFalse(ulid)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_NOT_FOUND, EntityName.POST));
    }

    // TODO : Spring Security 적용 후 PreAuthorize 고려
    private void validateMemberHasPostAccess(CommPostEntity commPost, UUID memberUuid) {
        if(!commPost.getAuthMember().getUuid().equals(memberUuid)) {
            throw new AccessDeniedException(ErrorCode.POST_ACCESS_DENIED, "post");
        }
    }

    private void validateNotFoundCategoryUuid(UUID categoryUuid, UuidPrimaryKeyRepository<?> categoryRepository) {
        if (categoryUuid == null || !categoryRepository.existsByUuid(categoryUuid)) {
            throw new EntityNotFoundException(ErrorCode.CATEGORY_NOT_FOUND, EntityName.CATEGORY);
        }
    }

    private void validateContentAndOrderInfo(List<MultipartFile> content, List<FileOrder> orderInfo) {
        if(content.size() != orderInfo.size()) {
            throw new DataPairNumberMismatchException(ErrorCode.DATA_NUMBERS_MISMATCH, List.of("content", "orderInfo"));
        }

        List<String> contentFilenames = new ArrayList<>(content.size());
        for(MultipartFile part:content) {
            String fileName = part.getOriginalFilename();
            if (fileName == null || fileName.isBlank()) {
                throw new EmptyValueException(ErrorCode.FILE_NAME_EMPTY, "fileName");
            }
            String contentType = part.getContentType();
            if (contentType == null || contentType.isBlank()) {
                throw new EmptyValueException(ErrorCode.CONTENT_TYPE_EMPTY, "contentType");
            }
            contentFilenames.add(fileName);
        }

        List<String> orderFilenames = orderInfo.stream()
                .sorted(Comparator.comparingInt(FileOrder::order))
                .map(FileOrder::filename)
                .toList();

        if (!contentFilenames.equals(orderFilenames)) {
            throw new DataPairOrderMismatchException(ErrorCode.DATA_ORDERS_MISMATCH, List.of("content", "orderInfo"));
        }
    }
}
