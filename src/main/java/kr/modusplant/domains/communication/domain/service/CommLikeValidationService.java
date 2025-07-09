package kr.modusplant.domains.communication.domain.service;

import kr.modusplant.domains.communication.error.CommunicationExistsException;
import kr.modusplant.domains.communication.error.CommunicationNotFoundException;
import kr.modusplant.domains.communication.persistence.repository.CommLikeRepository;
import kr.modusplant.domains.communication.persistence.repository.CommPostRepository;
import kr.modusplant.domains.member.error.MemberNotFoundException;
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
        if (!commPostRepository.existsById(postId)) {
            throw CommunicationNotFoundException.ofPost();
        }
        if (!memberRepository.existsById(memberId)) {
            throw MemberNotFoundException.ofMember();
        }
    }

    public void validateNotFoundCommLike(String postId, UUID memberId) {
        if (!commLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw CommunicationNotFoundException.ofLike();
        }
    }

    public void validateExistedCommLike(String postId, UUID memberId) {
        if (commLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw CommunicationExistsException.ofLike();
        }
    }
}
