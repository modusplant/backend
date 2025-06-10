package kr.modusplant.domains.communication.common.domain.service.supers;

import kr.modusplant.domains.common.persistence.repository.supers.UuidPrimaryKeyRepository;
import kr.modusplant.domains.communication.common.app.http.request.FileOrder;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public abstract class AbstractPostValidationService {
    protected void validateExistedCategoryUuid(UUID categoryUuid, UuidPrimaryKeyRepository<?> categoryRepository) {
        if (categoryUuid == null) {
            return;
        }
        if (categoryRepository.findByUuid(categoryUuid).isPresent()) {
            throw new EntityExistsWithUuidException(categoryUuid, categoryRepository.getClass());
        }
    }

    protected void validateNotFoundCategoryUuid(UUID categoryUuid, UuidPrimaryKeyRepository<?> categoryRepository) {
        if (categoryUuid == null || categoryRepository.findByUuid(categoryUuid).isEmpty()) {
            throw new EntityExistsWithUuidException(categoryUuid, categoryRepository.getClass());
        }
    }

    protected void validateTitle(String title) {
        if (title == null || title.isBlank() || title.length() > 150) {
            throw new IllegalArgumentException("Title must not be null or blank and must be at most 150 characters long.");
        }
    }

    protected void validateContentAndOrderInfo(List<MultipartFile> content, List<FileOrder> orderInfo) {
        boolean contentEmpty = content == null || content.isEmpty();
        boolean orderInfoEmpty = orderInfo==null || orderInfo.isEmpty();
        if (contentEmpty || orderInfoEmpty || isContentNotValid(content,orderInfo)) {
            throw new IllegalArgumentException("Content and orderInfo must not be empty, and their filenames must match in size and order.");
        }
    }

    private boolean isContentNotValid(List<MultipartFile> content, List<FileOrder> orderInfo) {
        if(content.size() != orderInfo.size()) {
            return true;
        }

        List<String> contentFilenames = new ArrayList<>(content.size());
        for(MultipartFile part:content) {
            String fileName = part.getOriginalFilename();
            String contentType = part.getContentType();
            if (fileName.isEmpty() || fileName.isBlank() || contentType.isEmpty() || contentType.isBlank()) {
                return true;
            }
            contentFilenames.add(fileName);
        }

        List<String> orderFilenames = orderInfo.stream()
                .sorted(Comparator.comparingInt(FileOrder::order))
                .map(FileOrder::filename)
                .toList();

        return !contentFilenames.equals(orderFilenames);
    }

}
