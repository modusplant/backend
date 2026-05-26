package kr.modusplant.domains.member.framework.outbound.jpa.repository;

import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberWithdrawEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.repository.supers.MemberUuidPrimaryKeyJpaRepository;
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