package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.SiteMemberProfileEntity;
import kr.modusplant.framework.jpa.repository.supers.SiteMemberUuidPrimaryKeyJpaRepository;
import kr.modusplant.shared.persistence.repository.LastModifiedAtRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Primary
public interface SiteMemberProfileJpaRepository extends LastModifiedAtRepository<SiteMemberProfileEntity>, SiteMemberUuidPrimaryKeyJpaRepository<SiteMemberProfileEntity>, JpaRepository<SiteMemberProfileEntity, UUID> {
}