package kr.modusplant.domains.communication.tip.domain.service;

import kr.modusplant.domains.communication.common.error.CommunicationExistsException;
import kr.modusplant.domains.communication.common.error.CommunicationNotFoundException;
import kr.modusplant.domains.communication.tip.persistence.repository.TipLikeRepository;
import kr.modusplant.domains.communication.tip.persistence.repository.TipPostRepository;
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
public class TipLikeValidationService {
    private final TipPostRepository tipPostRepository;
    private final SiteMemberRepository memberRepository;
    private final TipLikeRepository tipLikeRepository;

    public void validateNotFoundTipPostOrMember(String postId, UUID memberId) {
        if (postId == null || memberId == null) {
            throw new IllegalArgumentException("게시글 또는 회원 입력 창이 비어 있습니다.");
        }

        if (!tipPostRepository.existsById(postId)) {
            throw CommunicationNotFoundException.ofPost();
        }
        if (!memberRepository.existsById(memberId)) {
            throw MemberNotFoundException.ofMember();
        }
    }

    public void validateNotFoundTipLike(String postId, UUID memberId) {
        if (!tipLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw CommunicationNotFoundException.ofLike();
        }
    }

    public void validateExistedTipLike(String postId, UUID memberId) {
        if (tipLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw CommunicationExistsException.ofLike();
        }
    }
}
