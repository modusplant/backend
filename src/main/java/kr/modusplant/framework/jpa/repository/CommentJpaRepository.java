package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.CommentEntity;
import kr.modusplant.framework.jpa.entity.MemberEntity;
import kr.modusplant.framework.jpa.entity.PostEntity;
import kr.modusplant.shared.persistence.compositekey.CommentCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentJpaRepository extends JpaRepository<CommentEntity, CommentCompositeKey> {
    Optional<CommentEntity> findByPostUlidAndPath(String postUlid, String path);

    List<CommentEntity> findByPost(PostEntity post);

    List<CommentEntity> findByAuthMember(MemberEntity authMember);

    List<CommentEntity> findByContent(String content);

    void deleteByPostUlidAndPath(String postUlid, String path);

    boolean existsByPostUlidAndPath(String postUlid, String path);
}
