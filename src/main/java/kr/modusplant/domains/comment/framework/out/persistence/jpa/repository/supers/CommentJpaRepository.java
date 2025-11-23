package kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.supers;

import kr.modusplant.framework.jpa.entity.CommCommentEntity;
import kr.modusplant.shared.persistence.compositekey.CommCommentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<CommCommentEntity, CommCommentId> {

}
