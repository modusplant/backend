package kr.modusplant.domains.communication.domain.service;

import kr.modusplant.domains.communication.persistence.repository.CommPostRepository;
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
                throw new IllegalArgumentException("현재 이용할 수 있는 페이지 범위(1 ~ 1)를 벗어났습니다.");
            }
            return;
        }

        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        if (pageable.getPageNumber() >= totalPages) {
            throw new IllegalArgumentException(
                    "현재 이용할 수 있는 페이지 범위(1 ~ " + (totalPages) + ")를 벗어났습니다.");
        }
    }
}