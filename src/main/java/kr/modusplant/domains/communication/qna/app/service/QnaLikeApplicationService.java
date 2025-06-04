package kr.modusplant.domains.communication.qna.app.service;

import kr.modusplant.domains.communication.common.app.http.response.LikeResponse;
import kr.modusplant.domains.communication.qna.domain.service.QnaLikeValidationService;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaLikeEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaLikeRepository;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QnaLikeApplicationService {
    private final QnaPostRepository qnaPostRepository;
    private final QnaLikeRepository qnaLikeRepository;
    private final QnaLikeValidationService qnaLikeValidationService;

    @Transactional
    public LikeResponse likeQnaPost(String qnaPostId, UUID memberId) {
        qnaLikeValidationService.validateExistedQnaPostAndMember(qnaPostId, memberId);
        qnaLikeValidationService.validateQnaLikeNotExists(qnaPostId, memberId);

        QnaPostEntity qnaPost = qnaPostRepository.findById(qnaPostId).orElseThrow(() -> new IllegalArgumentException("qna post not found"));
        qnaPost.increaseLikeCount();

        qnaLikeRepository.save(QnaLikeEntity.of(qnaPostId, memberId));
        return LikeResponse.of(qnaPost.getLikeCount(), true);
    }

    @Transactional
    public LikeResponse unlikeQnaPost(String qnaPostId, UUID memberId) {
        qnaLikeValidationService.validateExistedQnaPostAndMember(qnaPostId, memberId);
        qnaLikeValidationService.validateQnaLikeExists(qnaPostId, memberId);

        QnaPostEntity qnaPost = qnaPostRepository.findById(qnaPostId).orElseThrow(() -> new IllegalArgumentException("qna post not found"));
        qnaPost.decreaseLikeCount();

        qnaLikeRepository.deleteByQnaPostIdAndMemberId(qnaPostId, memberId);
        return LikeResponse.of(qnaPost.getLikeCount(), false);
    }
}
