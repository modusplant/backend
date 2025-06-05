package kr.modusplant.domains.communication.conversation.domain.service;

import kr.modusplant.domains.communication.conversation.persistence.entity.ConvPostEntity;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvLikeRepository;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvPostRepository;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import kr.modusplant.global.error.EntityExistsWithUuidException;
import kr.modusplant.global.error.EntityNotFoundWithUlidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConvLikeValidationService {
    private final ConvPostRepository convPostRepository;
    private final SiteMemberRepository memberRepository;
    private final ConvLikeRepository convLikeRepository;

    public void validateExistedConvPostAndMember(String postId, UUID memberId) {
        if (postId == null || memberId == null) {
            throw new IllegalArgumentException("postId and memberId must not be null");
        }

        if (!convPostRepository.existsById(postId)) {
            throw new EntityNotFoundWithUlidException(postId, ConvPostEntity.class);
        }
        if (!memberRepository.existsById(memberId)) {
            throw new EntityExistsWithUuidException(memberId, SiteMemberEntity.class);
        }
    }

    public void validateConvLikeExists(String postId, UUID memberId) {
        if (!convLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new IllegalArgumentException("member not liked status");
        }
    }

    public void validateConvLikeNotExists(String postId, UUID memberId) {
        if (convLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new IllegalArgumentException("member already liked");
        }
    }
}
