package kr.modusplant.domains.comment.framework.out.persistence.jpa.repository;

import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentEntity;
import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.post.framework.out.jpa.entity.PostEntity;
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
