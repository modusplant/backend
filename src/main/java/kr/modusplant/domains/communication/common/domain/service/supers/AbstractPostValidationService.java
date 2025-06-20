package kr.modusplant.domains.communication.common.domain.service.supers;

import kr.modusplant.domains.common.persistence.repository.supers.UuidPrimaryKeyRepository;
import kr.modusplant.domains.communication.common.app.http.request.FileOrder;
import kr.modusplant.domains.communication.common.error.CategoryNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public abstract class AbstractPostValidationService {
    protected void validateNotFoundCategoryUuid(UUID categoryUuid, UuidPrimaryKeyRepository<?> categoryRepository) {
        if (categoryUuid == null || !categoryRepository.existsByUuid(categoryUuid)) {
            throw new CategoryNotFoundException();
        }
    }

    protected void validateContentAndOrderInfo(List<MultipartFile> content, List<FileOrder> orderInfo) {
        if (isContentNotValid(content,orderInfo)) {
            throw new IllegalArgumentException("컨텐츠 또는 순서 정보가 비어 있거나 그들의 파일명의 크기 혹은 순서가 일치하지 않습니다.");
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
