package kr.modusplant.domains.communication.conversation.app.service;

import kr.modusplant.domains.communication.common.app.http.response.LikeResponse;
import kr.modusplant.domains.communication.conversation.domain.service.ConvLikeValidationService;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvLikeEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvPostEntity;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvLikeRepository;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConvLikeApplicationService {
    private final ConvPostRepository convPostRepository;
    private final ConvLikeRepository convLikeRepository;
    private final ConvLikeValidationService convLikeValidationService;

    @Transactional
    public LikeResponse likeConvPost(String postId, UUID memberId) {
        convLikeValidationService.validateExistedConvPostAndMember(postId, memberId);
        convLikeValidationService.validateConvLikeNotExists(postId, memberId);

        ConvPostEntity convPost = convPostRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("conv post not found"));
        convPost.increaseLikeCount();

        convLikeRepository.save(ConvLikeEntity.of(postId, memberId));
        return LikeResponse.of(convPost.getLikeCount(), true);
    }

    @Transactional
    public LikeResponse unlikeConvPost(String postId, UUID memberId) {
        convLikeValidationService.validateExistedConvPostAndMember(postId, memberId);
        convLikeValidationService.validateConvLikeExists(postId, memberId);

        ConvPostEntity convPost = convPostRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("conv post not found"));
        convPost.decreaseLikeCount();

        convLikeRepository.deleteByPostIdAndMemberId(postId, memberId);
        return LikeResponse.of(convPost.getLikeCount(), false);
    }
}
