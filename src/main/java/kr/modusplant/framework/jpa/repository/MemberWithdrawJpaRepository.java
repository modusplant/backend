package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.MemberWithdrawEntity;
import kr.modusplant.framework.jpa.repository.supers.MemberUuidPrimaryKeyJpaRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Primary
public interface MemberWithdrawJpaRepository extends
        MemberUuidPrimaryKeyJpaRepository<MemberWithdrawEntity>,
        JpaRepository<MemberWithdrawEntity, UUID> {
}