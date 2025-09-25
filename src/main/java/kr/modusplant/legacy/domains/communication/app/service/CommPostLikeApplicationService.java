package kr.modusplant.legacy.domains.communication.app.service;

import kr.modusplant.framework.out.jpa.entity.CommPostLikeEntity;
import kr.modusplant.framework.out.jpa.entity.CommPostEntity;
import kr.modusplant.framework.out.jpa.repository.CommPostLikeRepository;
import kr.modusplant.framework.out.jpa.repository.CommPostRepository;
import kr.modusplant.infrastructure.persistence.constant.EntityName;
import kr.modusplant.legacy.domains.communication.app.http.response.CommPostLikeResponse;
import kr.modusplant.legacy.domains.communication.domain.service.CommPostLikeValidationService;
import kr.modusplant.shared.exception.EntityNotFoundException;
import kr.modusplant.shared.exception.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommPostLikeApplicationService {
    private final CommPostRepository commPostRepository;
    private final CommPostLikeRepository commPostLikeRepository;
    private final CommPostLikeValidationService commPostLikeValidationService;

    @Transactional
    public CommPostLikeResponse likeCommPost(String postId, UUID memberId) {
        commPostLikeValidationService.validateNotFoundCommPostOrMember(postId, memberId);
        commPostLikeValidationService.validateExistedCommPostLike(postId, memberId);

        CommPostEntity commPost = commPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_NOT_FOUND, EntityName.POST));
        commPost.increaseLikeCount();

        commPostLikeRepository.save(CommPostLikeEntity.of(postId, memberId));
        return CommPostLikeResponse.of(commPost.getLikeCount(), true);
    }

    @Transactional
    public CommPostLikeResponse unlikeCommPost(String postId, UUID memberId) {
        commPostLikeValidationService.validateNotFoundCommPostOrMember(postId, memberId);
        commPostLikeValidationService.validateNotFoundCommPostLike(postId, memberId);

        CommPostEntity commPost = commPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.POST_NOT_FOUND, EntityName.POST));
        commPost.decreaseLikeCount();

        commPostLikeRepository.deleteByPostIdAndMemberId(postId, memberId);
        return CommPostLikeResponse.of(commPost.getLikeCount(), false);
    }
}
