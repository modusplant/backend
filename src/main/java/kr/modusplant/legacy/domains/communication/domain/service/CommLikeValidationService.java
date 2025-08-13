package kr.modusplant.legacy.domains.communication.domain.service;

import kr.modusplant.domain.exception.enums.ErrorCode;
import kr.modusplant.global.error.EntityExistsException;
import kr.modusplant.global.error.EntityNotFoundException;
import kr.modusplant.global.vo.EntityName;
import kr.modusplant.legacy.domains.communication.persistence.repository.CommLikeRepository;
import kr.modusplant.legacy.domains.communication.persistence.repository.CommPostRepository;
import kr.modusplant.legacy.domains.member.persistence.repository.SiteMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommLikeValidationService {
    private final CommPostRepository commPostRepository;
    private final SiteMemberRepository memberRepository;
    private final CommLikeRepository commLikeRepository;

    public void validateNotFoundCommPostOrMember(String postId, UUID memberId) {
        if (!commPostRepository.existsById(postId)) {
            throw new EntityNotFoundException(ErrorCode.POST_NOT_FOUND, EntityName.POST);
        }
        if (!memberRepository.existsById(memberId)) {
            throw new EntityNotFoundException(ErrorCode.SITEMEMBER_NOT_FOUND, EntityName.SITE_MEMBER);
        }
    }

    public void validateNotFoundCommLike(String postId, UUID memberId) {
        if (!commLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new EntityNotFoundException(ErrorCode.LIKE_NOT_FOUND, EntityName.LIKE);
        }
    }

    public void validateExistedCommLike(String postId, UUID memberId) {
        if (commLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new EntityExistsException(ErrorCode.LIKE_EXISTS, EntityName.LIKE);
        }
    }
}
