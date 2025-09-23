package kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.supers;

import kr.modusplant.domains.comment.usecase.response.CommentResponse;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.compositekey.CommentCompositeKey;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, CommentCompositeKey> {

    @Query("SELECT c.postEntity.ulid, c.id.path, m.nickname, c.content, c.isDeleted, c.createdAt " +
        "FROM CommentEntity c " +
        "INNER JOIN c.authMember m " +
        "WHERE c.postEntity.ulid = :postUlid " +
        "ORDER BY c.createdAt ASC")
    List<CommentResponse> findByPostUlid(@Param("postUlid") String postUlid);

    @Query("SELECT c.postEntity.ulid, c.id.path, m.nickname, c.content, c.isDeleted, c.createdAt " +
            "FROM CommentEntity c " +
            "INNER JOIN c.authMember m " +
            "WHERE c.authMember.uuid = :memberUuid " +
            "ORDER BY c.createdAt ASC")
    List<CommentResponse> findByAuthMemberUuid(@Param("memberUuid") UUID memberUuid);

}
