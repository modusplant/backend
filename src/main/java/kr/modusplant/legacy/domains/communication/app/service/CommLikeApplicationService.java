package kr.modusplant.legacy.domains.communication.app.service;

import kr.modusplant.framework.out.jpa.entity.CommLikeEntity;
import kr.modusplant.framework.out.jpa.entity.CommPostEntity;
import kr.modusplant.framework.out.jpa.repository.CommLikeRepository;
import kr.modusplant.framework.out.jpa.repository.CommPostRepository;
import kr.modusplant.infrastructure.persistence.constant.EntityName;
import kr.modusplant.legacy.domains.communication.app.http.response.CommLikeResponse;
import kr.modusplant.legacy.domains.communication.domain.service.CommLikeValidationService;
import kr.modusplant.shared.exception.EntityNotFoundException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommLikeApplicationService {
    private final CommPostRepository commPostRepository;
    private final CommLikeRepository commLikeRepository;
    private final CommLikeValidationService commLikeValidationService;

    @Transactional
    public CommLikeResponse likeCommPost(String postId, UUID memberId) {
        commLikeValidationService.validateNotFoundCommPostOrMember(postId, memberId);
        commLikeValidationService.validateExistedCommLike(postId, memberId);

        CommPostEntity commPost = commPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_NOT_FOUND, EntityName.POST));
        commPost.increaseLikeCount();

        commLikeRepository.save(CommLikeEntity.of(postId, memberId));
        return CommLikeResponse.of(commPost.getLikeCount(), true);
    }

    @Transactional
    public CommLikeResponse unlikeCommPost(String postId, UUID memberId) {
        commLikeValidationService.validateNotFoundCommPostOrMember(postId, memberId);
        commLikeValidationService.validateNotFoundCommLike(postId, memberId);

        CommPostEntity commPost = commPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_NOT_FOUND, EntityName.POST));
        commPost.decreaseLikeCount();

        commLikeRepository.deleteByPostIdAndMemberId(postId, memberId);
        return CommLikeResponse.of(commPost.getLikeCount(), false);
    }
}
