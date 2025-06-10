package kr.modusplant.domains.communication.qna.persistence.repository;

import kr.modusplant.domains.communication.qna.persistence.entity.QnaCommentEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.compositekey.QnaCommentCompositeKey;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QnaCommentRepository extends JpaRepository<QnaCommentEntity, QnaCommentCompositeKey> {

    Optional<QnaCommentEntity> findByPostUlidAndPath(String postUlid, String path);

    List<QnaCommentEntity> findByPostEntity(QnaPostEntity postEntity);

    List<QnaCommentEntity> findByAuthMember(SiteMemberEntity authMember);

    List<QnaCommentEntity> findByCreateMember(SiteMemberEntity createMember);

    List<QnaCommentEntity> findByContent(String content);

    void deleteByPostUlidAndPath(String postUlid, String path);

    boolean existsByPostUlidAndPath(String postUlid, String path);
}
