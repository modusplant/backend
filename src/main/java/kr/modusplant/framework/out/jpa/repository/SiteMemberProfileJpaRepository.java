package kr.modusplant.framework.out.jpa.repository;

import kr.modusplant.framework.out.jpa.entity.SiteMemberProfileEntity;
import kr.modusplant.framework.out.jpa.repository.supers.SiteMemberUuidPrimaryKeyJpaRepository;
import kr.modusplant.shared.persistence.repository.supers.LastModifiedAtRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Primary
public interface SiteMemberProfileJpaRepository extends LastModifiedAtRepository<SiteMemberProfileEntity>, SiteMemberUuidPrimaryKeyJpaRepository<SiteMemberProfileEntity>, JpaRepository<SiteMemberProfileEntity, UUID> {
}