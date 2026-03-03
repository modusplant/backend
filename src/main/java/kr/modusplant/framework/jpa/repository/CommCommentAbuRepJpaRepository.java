package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.CommCommentAbuRepEntity;
import kr.modusplant.shared.persistence.compositekey.CommCommentId;
import kr.modusplant.shared.persistence.repository.CreatedAtAndLastModifiedAtRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public interface CommCommentAbuRepJpaRepository extends CreatedAtAndLastModifiedAtRepository<CommCommentAbuRepEntity>, JpaRepository<CommCommentAbuRepEntity, CommCommentId> {
}