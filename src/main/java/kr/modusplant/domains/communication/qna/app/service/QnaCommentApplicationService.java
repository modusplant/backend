package kr.modusplant.domains.communication.qna.app.service;

import kr.modusplant.domains.communication.qna.app.http.request.QnaCommentInsertRequest;
import kr.modusplant.domains.communication.qna.app.http.response.QnaCommentResponse;
import kr.modusplant.domains.communication.qna.domain.service.QnaCommentValidationService;
import kr.modusplant.domains.communication.qna.domain.service.QnaPostValidationService;
import kr.modusplant.domains.communication.qna.mapper.QnaCommentAppInfraMapper;
import kr.modusplant.domains.communication.qna.mapper.QnaCommentAppInfraMapperImpl;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCommentEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaCommentRepository;
import kr.modusplant.domains.communication.qna.persistence.repository.QnaPostRepository;
import kr.modusplant.domains.member.domain.service.SiteMemberValidationService;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.member.persistence.repository.SiteMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Primary
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class QnaCommentApplicationService {

    private final QnaCommentValidationService qnaCommentValidationService;
    private final QnaPostValidationService qnaPostValidationService;
    private final SiteMemberValidationService memberValidationService;
    private final QnaCommentAppInfraMapper qnaCommentAppInfraMapper = new QnaCommentAppInfraMapperImpl();
    private final QnaCommentRepository qnaCommentRepository;
    private final QnaPostRepository qnaPostRepository;
    private final SiteMemberRepository memberRepository;

    public List<QnaCommentResponse> getAll() {
        return qnaCommentRepository.findAll()
                .stream().map(qnaCommentAppInfraMapper::toQnaCommentResponse).toList();
    }

    public List<QnaCommentResponse> getByPostEntity(QnaPostEntity requestPostEntity) {
        String ulid = requestPostEntity.getUlid();
        qnaPostValidationService.validateNotFoundUlid(ulid);
        QnaPostEntity postEntity = qnaPostRepository.findByUlid(requestPostEntity.getUlid()).orElseThrow();

        return qnaCommentRepository.findByPostEntity(postEntity)
                .stream().map(qnaCommentAppInfraMapper::toQnaCommentResponse).toList();
    }

    public List<QnaCommentResponse> getByAuthMember(SiteMemberEntity authMember) {
        memberValidationService.validateNotFoundUuid(authMember.getUuid());
        SiteMemberEntity memberEntity = memberRepository.findByUuid(authMember.getUuid()).orElseThrow();

        return qnaCommentRepository.findByAuthMember(memberEntity)
                .stream().map(qnaCommentAppInfraMapper::toQnaCommentResponse).toList();
    }

    public List<QnaCommentResponse> getByCreateMember(SiteMemberEntity createMember) {
        memberValidationService.validateNotFoundUuid(createMember.getUuid());
        SiteMemberEntity memberEntity = memberRepository.findByUuid(createMember.getUuid()).orElseThrow();

        return qnaCommentRepository.findByCreateMember(memberEntity)
                .stream().map(qnaCommentAppInfraMapper::toQnaCommentResponse).toList();
    }

    public Optional<QnaCommentResponse> getByPostUlidAndPath(String postUlid, String path) {
        qnaPostValidationService.validateNotFoundUlid(postUlid);
        return Optional.of(
                qnaCommentAppInfraMapper.toQnaCommentResponse(
                        qnaCommentRepository.findByPostUlidAndPath(postUlid, path).orElseThrow()
                ));
    }

    @Transactional
    public QnaCommentResponse insert(QnaCommentInsertRequest commentInsertRequest, UUID memberUuid) {
        String postUlid = commentInsertRequest.postUlid();
        String path = commentInsertRequest.path();
        qnaCommentValidationService.validateExistedQnaCommentEntity(postUlid, path);
        memberValidationService.validateNotFoundUuid(memberUuid);

        SiteMemberEntity memberEntity = memberRepository.findByUuid(memberUuid).orElseThrow();
        QnaCommentEntity commentEntity = QnaCommentEntity.builder()
                .postEntity(qnaPostRepository.findByUlid(postUlid).orElseThrow())
                .path(path)
                .authMember(memberEntity)
                .createMember(memberEntity)
                .content(commentInsertRequest.content())
                .build();

        return qnaCommentAppInfraMapper.toQnaCommentResponse(qnaCommentRepository.save(commentEntity));
    }

    @Transactional
    public void removeByPostUlidAndPath(String postUlid, String path) {
        qnaCommentValidationService.validateNotFoundQnaCommentEntity(postUlid, path);
        qnaCommentRepository.deleteByPostUlidAndPath(postUlid, path);
    }
}
