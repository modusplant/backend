package kr.modusplant.domains.communication.domain.service;

import kr.modusplant.domains.common.error.EmptyValueException;
import kr.modusplant.domains.common.error.InvalidMultipartDataException;
import kr.modusplant.domains.common.persistence.repository.supers.UuidPrimaryKeyRepository;
import kr.modusplant.domains.communication.app.http.request.CommPostInsertRequest;
import kr.modusplant.domains.communication.app.http.request.CommPostUpdateRequest;
import kr.modusplant.domains.communication.app.http.request.FileOrder;
import kr.modusplant.domains.communication.error.AccessDeniedException;
import kr.modusplant.domains.communication.error.CommunicationNotFoundException;
import kr.modusplant.domains.communication.persistence.entity.CommPostEntity;
import kr.modusplant.domains.communication.persistence.repository.CommPostRepository;
import kr.modusplant.domains.communication.persistence.repository.CommSecondaryCategoryRepository;
import kr.modusplant.global.enums.ErrorCode;
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
            throw CommunicationNotFoundException.ofPost();
        }
    }

    private CommPostEntity findIfExistsByUlid(String ulid) {
        if (ulid == null) {
            throw CommunicationNotFoundException.ofPost();
        }
        return commPostRepository.findByUlidAndIsDeletedFalse(ulid)
                .orElseThrow(CommunicationNotFoundException::ofPost);
    }

    // TODO : Spring Security 적용 후 PreAuthorize 고려
    private void validateMemberHasPostAccess(CommPostEntity commPost, UUID memberUuid) {
        if(!commPost.getAuthMember().getUuid().equals(memberUuid)) {
            throw new AccessDeniedException(ErrorCode.POST_ACCESS_DENIED, "post");
        }
    }

    private void validateNotFoundCategoryUuid(UUID categoryUuid, UuidPrimaryKeyRepository<?> categoryRepository) {
        if (categoryUuid == null || !categoryRepository.existsByUuid(categoryUuid)) {
            throw CommunicationNotFoundException.ofCategory();
        }
    }

    private void validateContentAndOrderInfo(List<MultipartFile> content, List<FileOrder> orderInfo) {
        if(content.size() != orderInfo.size()) {
            throw new InvalidMultipartDataException(ErrorCode.CONTENT_AND_FILE_NUMBER_MISMATCH);
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
            throw new InvalidMultipartDataException(ErrorCode.CONTENT_AND_FILE_ORDER_MISMATCH);
        }
    }
}
