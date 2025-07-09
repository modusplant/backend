package kr.modusplant.domains.communication.app.service;

import kr.modusplant.domains.communication.app.http.response.CommLikeResponse;
import kr.modusplant.domains.communication.domain.service.CommLikeValidationService;
import kr.modusplant.domains.communication.persistence.entity.CommLikeEntity;
import kr.modusplant.domains.communication.persistence.entity.CommPostEntity;
import kr.modusplant.domains.communication.persistence.repository.CommLikeRepository;
import kr.modusplant.domains.communication.persistence.repository.CommPostRepository;
import kr.modusplant.global.enums.ErrorCode;
import kr.modusplant.global.error.EntityNotFoundException;
import kr.modusplant.global.vo.EntityName;
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
