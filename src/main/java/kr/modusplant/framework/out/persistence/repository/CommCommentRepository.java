package kr.modusplant.framework.out.persistence.repository;

import kr.modusplant.framework.out.persistence.entity.SiteMemberEntity;
import kr.modusplant.framework.out.persistence.entity.CommCommentEntity;
import kr.modusplant.framework.out.persistence.entity.CommPostEntity;
import kr.modusplant.framework.out.persistence.entity.compositekey.CommCommentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommCommentRepository extends JpaRepository<CommCommentEntity, CommCommentId> {
    Optional<CommCommentEntity> findByPostUlidAndPath(String postUlid, String path);

    List<CommCommentEntity> findByPostEntity(CommPostEntity postEntity);

    List<CommCommentEntity> findByAuthMember(SiteMemberEntity authMember);

    List<CommCommentEntity> findByCreateMember(SiteMemberEntity createMember);

    List<CommCommentEntity> findByContent(String content);

    void deleteByPostUlidAndPath(String postUlid, String path);

    boolean existsByPostUlidAndPath(String postUlid, String path);
}
