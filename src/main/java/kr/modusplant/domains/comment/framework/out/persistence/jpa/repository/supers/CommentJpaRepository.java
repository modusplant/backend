package kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.supers;

import kr.modusplant.domains.comment.adapter.response.CommentResponse;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.compositekey.CommentCompositeKey;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, CommentCompositeKey> {

    @Query("SELECT c.post_ulid, c.path, m.nickname, c.content, c.is_deleted, c.created_at " +
            "FROM comm_comment c " +
            "WHERE c.post_ulid = :postUlid " +
            "INNER JOIN site_member m ON c.auth_memb_uuid = m.uuid")
    List<CommentResponse> findByPostUlid(@Param("postUlid") String postUlid);

    @Query("SELECT c.post_ulid, c.path, m.nickname, c.content, c.is_deleted, c.created_at " +
            "FROM comm_comment c " +
            "WHERE c.auth_memb_uuid = :memberUuid " +
            "INNER JOIN site_member m ON c.auth_memb_uuid = m.uuid")
    List<CommentResponse> findByAuthMemberUuid(@Param("memberUuid") UUID memberUuid);

}
