package kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.supers;

import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentAuthorJpaRepository extends JpaRepository<CommentMemberEntity, UUID> {

    CommentMemberEntity findByUuid(UUID authMemberUuid);
}
