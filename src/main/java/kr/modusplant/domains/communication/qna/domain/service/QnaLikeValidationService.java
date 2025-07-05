package kr.modusplant.domains.communication.qna.domain.service;

import kr.modusplant.domains.communication.common.error.CommunicationExistsException;
import kr.modusplant.domains.communication.common.error.CommunicationNotFoundException;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaLikeRepository;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostRepository;
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
public class QnaLikeValidationService {
    private final QnaPostRepository qnaPostRepository;
    private final SiteMemberRepository memberRepository;
    private final QnaLikeRepository qnaLikeRepository;

    public void validateNotFoundQnaPostOrMember(String postId, UUID memberId) {
        if (postId == null || memberId == null) {
            throw new IllegalArgumentException("게시글 또는 회원 값이 비어 있습니다.");
        }

        if (!qnaPostRepository.existsById(postId)) {
            throw CommunicationNotFoundException.ofPost();
        }
        if (!memberRepository.existsById(memberId)) {
            throw MemberNotFoundException.ofMember();
        }
    }

    public void validateNotFoundQnaLike(String postId, UUID memberId) {
        if (!qnaLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw CommunicationNotFoundException.ofLike();
        }
    }

    public void validateExistedQnaLike(String postId, UUID memberId) {
        if (qnaLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw CommunicationExistsException.ofLike();
        }
    }
}
