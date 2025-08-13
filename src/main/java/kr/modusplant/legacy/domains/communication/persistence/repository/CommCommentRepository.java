package kr.modusplant.legacy.domains.communication.persistence.repository;

import kr.modusplant.legacy.domains.communication.persistence.entity.CommCommentEntity;
import kr.modusplant.legacy.domains.communication.persistence.entity.CommPostEntity;
import kr.modusplant.legacy.domains.communication.persistence.entity.compositekey.CommCommentCompositeKey;
import kr.modusplant.legacy.domains.member.persistence.entity.SiteMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommCommentRepository extends JpaRepository<CommCommentEntity, CommCommentCompositeKey> {
    Optional<CommCommentEntity> findByPostUlidAndPath(String postUlid, String path);

    List<CommCommentEntity> findByPostEntity(CommPostEntity postEntity);

    List<CommCommentEntity> findByAuthMember(SiteMemberEntity authMember);

    List<CommCommentEntity> findByCreateMember(SiteMemberEntity createMember);

    List<CommCommentEntity> findByContent(String content);

    void deleteByPostUlidAndPath(String postUlid, String path);

    boolean existsByPostUlidAndPath(String postUlid, String path);
}
