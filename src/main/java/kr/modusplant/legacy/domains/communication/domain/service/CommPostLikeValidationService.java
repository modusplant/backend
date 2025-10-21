package kr.modusplant.legacy.domains.communication.domain.service;

import kr.modusplant.framework.out.jpa.repository.CommPostJpaRepository;
import kr.modusplant.framework.out.jpa.repository.CommPostLikeJpaRepository;
import kr.modusplant.framework.out.jpa.repository.SiteMemberJpaRepository;
import kr.modusplant.infrastructure.persistence.constant.EntityName;
import kr.modusplant.shared.exception.EntityExistsException;
import kr.modusplant.shared.exception.EntityNotFoundException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommPostLikeValidationService {
    private final CommPostJpaRepository commPostRepository;
    private final SiteMemberJpaRepository memberRepository;
    private final CommPostLikeJpaRepository commPostLikeRepository;

    public void validateNotFoundCommPostOrMember(String postId, UUID memberId) {
        if (!commPostRepository.existsById(postId)) {
            throw new EntityNotFoundException(ErrorCode.POST_NOT_FOUND, EntityName.POST);
        }
        if (!memberRepository.existsById(memberId)) {
            throw new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND, EntityName.SITE_MEMBER);
        }
    }

    public void validateNotFoundCommPostLike(String postId, UUID memberId) {
        if (!commPostLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new EntityNotFoundException(ErrorCode.LIKE_NOT_FOUND, EntityName.LIKE);
        }
    }

    public void validateExistedCommPostLike(String postId, UUID memberId) {
        if (commPostLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new EntityExistsException(ErrorCode.LIKE_EXISTS, EntityName.LIKE);
        }
    }
}
