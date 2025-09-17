package kr.modusplant.domains.post.framework.out.jpa.repository.supers;

import kr.modusplant.domains.post.framework.out.jpa.entity.SecondaryCategoryEntity;
import kr.modusplant.shared.persistence.repository.supers.CreatedAtRepository;
import kr.modusplant.shared.persistence.repository.supers.UuidPrimaryKeyRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

@Primary
public interface SecondaryCategoryJpaRepository extends UuidPrimaryKeyRepository<SecondaryCategoryEntity>, CreatedAtRepository<SecondaryCategoryEntity>, JpaRepository<SecondaryCategoryEntity, UUID> {
}
