package kr.modusplant.domains.communication.tip.domain.service;

import kr.modusplant.domains.communication.common.domain.service.supers.AbstractCommPageableValidationService;
import kr.modusplant.domains.communication.common.error.InvalidPaginationRangeException;
import kr.modusplant.domains.communication.tip.persistence.repository.TipPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TipPageableValidationService extends AbstractCommPageableValidationService {

    private final TipPostRepository postRepository;

    public void validatePageExistence(Pageable pageable) {
        long totalElements = postRepository.count();
        if (totalElements == 0L) {
            if (pageable.getPageNumber() > 1) {
                throw new InvalidPaginationRangeException();
            }
            return;
        }

        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        if (pageable.getPageNumber() >= totalPages) {
            throw new InvalidPaginationRangeException();
        }
    }
}