package kr.modusplant.legacy.domains.communication.domain.service;

import kr.modusplant.global.enums.ErrorCode;
import kr.modusplant.global.error.InvalidDataException;
import kr.modusplant.legacy.domains.communication.persistence.repository.CommPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommPageableValidationService {

    private final CommPostRepository postRepository;

    public void validatePageExistence(Pageable pageable) {
        long totalElements = postRepository.count();
        if (totalElements == 0L) {
            if (pageable.getPageNumber() > 1) {
                throw new InvalidDataException(ErrorCode.INVALID_PAGE_RANGE, "pageNumber");
            }
            return;
        }

        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        if (pageable.getPageNumber() >= totalPages) {
            throw new InvalidDataException(ErrorCode.INVALID_PAGE_RANGE, "pageNumber");
        }
    }
}