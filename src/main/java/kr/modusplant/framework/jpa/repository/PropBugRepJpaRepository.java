package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.PropBugRepEntity;
import kr.modusplant.framework.jpa.repository.supers.SiteMemberUuidPrimaryKeyJpaRepository;
import kr.modusplant.shared.persistence.repository.CreatedAtAndLastModifiedAtRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Primary
public interface PropBugRepJpaRepository extends CreatedAtAndLastModifiedAtRepository<PropBugRepEntity>, SiteMemberUuidPrimaryKeyJpaRepository<PropBugRepEntity>, JpaRepository<PropBugRepEntity, UUID> {
}