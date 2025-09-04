package kr.modusplant.domains.comment.framework.out.persistence.jpa.repository.supers;

import kr.modusplant.domains.comment.framework.out.persistence.jpa.compositekey.CommentCompositeKey;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentEntity;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentMemberEntity;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, CommentCompositeKey> {

    List<CommentEntity> findByPostEntity(PostEntity postEntity);

    List<CommentEntity> findByAuthMember(CommentMemberEntity authMember);

}
