package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.SiteMemberWithdrawEntity;
import kr.modusplant.framework.jpa.repository.supers.SiteMemberUuidPrimaryKeyJpaRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Primary
public interface SiteMemberWithdrawJpaRepository extends
        SiteMemberUuidPrimaryKeyJpaRepository<SiteMemberWithdrawEntity>,
        JpaRepository<SiteMemberWithdrawEntity, UUID> {
}