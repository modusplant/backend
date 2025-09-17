package kr.modusplant.domains.post.framework.out.jpa.repository.supers;

import kr.modusplant.domains.post.framework.out.jpa.entity.PrimaryCategoryEntity;
import kr.modusplant.shared.persistence.repository.supers.CreatedAtRepository;
import kr.modusplant.shared.persistence.repository.supers.UuidPrimaryKeyRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

@Primary
public interface PrimaryCategoryJpaRepository extends UuidPrimaryKeyRepository<PrimaryCategoryEntity>, CreatedAtRepository<PrimaryCategoryEntity>, JpaRepository<PrimaryCategoryEntity, UUID> {
}
