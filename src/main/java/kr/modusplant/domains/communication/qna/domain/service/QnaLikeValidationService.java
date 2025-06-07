package kr.modusplant.domains.communication.qna.domain.service;

import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaLikeRepository;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostRepository;
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
public class QnaLikeValidationService {
    private final QnaPostRepository qnaPostRepository;
    private final SiteMemberRepository memberRepository;
    private final QnaLikeRepository qnaLikeRepository;

    public void validateExistedQnaPostAndMember(String postId, UUID memberId) {
        if (postId == null || memberId == null) {
            throw new IllegalArgumentException("PostId and memberId must not be null.");
        }

        if (!qnaPostRepository.existsById(postId)) {
            throw new EntityNotFoundWithUlidException(postId, QnaPostEntity.class);
        }
        if (!memberRepository.existsById(memberId)) {
            throw new EntityExistsWithUuidException(memberId, SiteMemberEntity.class);
        }
    }

    public void validateNotFoundQnaLike(String postId, UUID memberId) {
        if (!qnaLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new IllegalArgumentException("Member not liked.");
        }
    }

    public void validateExistedQnaLike(String postId, UUID memberId) {
        if (qnaLikeRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new IllegalArgumentException("Member already liked.");
        }
    }
}
