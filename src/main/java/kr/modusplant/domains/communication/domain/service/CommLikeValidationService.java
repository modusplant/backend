package kr.modusplant.domains.communication.domain.service;

import kr.modusplant.domains.communication.error.LikeExistsException;
import kr.modusplant.domains.communication.error.LikeNotFoundException;
import kr.modusplant.domains.communication.error.PostNotFoundException;
import kr.modusplant.domains.communication.persistence.repository.CommLikeRepository;
import kr.modusplant.domains.communication.persistence.repository.CommPostRepository;
import kr.modusplant.domains.member.error.SiteMemberNotFoundException;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
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
        if (postId == null || memberId == null) {
            throw new IllegalArgumentException("게시글 또는 회원 값이 비어 있습니다.");
        }

        if (!commPostRepository.existsById(postId)) {
            throw new PostNotFoundException();
        }
        if (!memberRepository.existsById(memberId)) {
            throw new SiteMemberNotFoundException();
        }
    }

    public void validateNotFoundCommLike(String postId, UUID memberId) {
        if (!commLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new LikeNotFoundException();
        }
    }

    public void validateExistedCommLike(String postId, UUID memberId) {
        if (commLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new LikeExistsException();
        }
    }
}
