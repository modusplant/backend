package kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.supers;

import kr.modusplant.domains.comment.framework.out.persistence.jpa.compositekey.CommentCompositeKey;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, CommentCompositeKey> {
}
