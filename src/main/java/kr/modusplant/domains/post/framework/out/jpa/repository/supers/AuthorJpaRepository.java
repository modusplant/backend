package kr.modusplant.domains.post.framework.out.jpa.repository.supers;

import kr.modusplant.domains.post.framework.out.jpa.entity.AuthorEntity;
import kr.modusplant.shared.persistence.repository.supers.CreatedAtAndLastModifiedAtRepository;
import kr.modusplant.shared.persistence.repository.supers.UuidPrimaryKeyRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthorJpaRepository extends JpaRepository<AuthorEntity, UUID>, CreatedAtAndLastModifiedAtRepository<AuthorEntity>, UuidPrimaryKeyRepository<AuthorEntity> {
}