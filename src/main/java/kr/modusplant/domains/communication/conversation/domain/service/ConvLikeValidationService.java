package kr.modusplant.domains.communication.conversation.domain.service;

import kr.modusplant.domains.communication.common.error.CommunicationExistsException;
import kr.modusplant.domains.communication.common.error.CommunicationNotFoundException;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvLikeRepository;
import kr.modusplant.domains.communication.conversation.persistence.repository.ConvPostRepository;
import kr.modusplant.domains.member.error.MemberNotFoundException;
import kr.modusplant.domains.member.error.SiteMemberNotFoundException;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
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

    public void validateNotFoundConvPostOrMember(String postId, UUID memberId) {
        if (postId == null || memberId == null) {
            throw new IllegalArgumentException("게시글 또는 회원 값이 비어 있습니다.");
        }
        if (!convPostRepository.existsById(postId)) {
            throw CommunicationNotFoundException.ofPost();
        }
        if (!memberRepository.existsById(memberId)) {
            throw MemberNotFoundException.ofMember();
        }
    }

    public void validateNotFoundConvLike(String postId, UUID memberId) {
        if (!convLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw CommunicationNotFoundException.ofLike();
        }
    }

    public void validateExistedConvLike(String postId, UUID memberId) {
        if (convLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw CommunicationExistsException.ofLike();
        }
    }
}
