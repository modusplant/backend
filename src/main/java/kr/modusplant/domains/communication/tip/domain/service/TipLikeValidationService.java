package kr.modusplant.domains.communication.tip.domain.service;

import kr.modusplant.domains.communication.common.error.LikeExistsException;
import kr.modusplant.domains.communication.common.error.LikeNotFoundException;
import kr.modusplant.domains.communication.common.error.PostNotFoundException;
import kr.modusplant.domains.communication.tip.persistence.repository.TipLikeRepository;
import kr.modusplant.domains.communication.tip.persistence.repository.TipPostRepository;
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

    public void validateExistedTipPostAndMember(String postId, UUID memberId) {
        if (postId == null || memberId == null) {
            throw new IllegalArgumentException("게시글 또는 회원 입력 창이 비어 있습니다.");
        }

        if (!tipPostRepository.existsById(postId)) {
            throw new PostNotFoundException();
        }
        if (!memberRepository.existsById(memberId)) {
            throw new SiteMemberNotFoundException();
        }
    }

    public void validateNotFoundTipLike(String postId, UUID memberId) {
        if (!tipLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new LikeNotFoundException();
        }
    }

    public void validateExistedTipLike(String postId, UUID memberId) {
        if (tipLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new LikeExistsException();
        }
    }
}
