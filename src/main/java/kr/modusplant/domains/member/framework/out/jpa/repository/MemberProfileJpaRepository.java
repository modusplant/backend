package kr.modusplant.domains.member.framework.out.jpa.repository;

import kr.modusplant.domains.member.framework.out.jpa.entity.MemberProfileEntity;
import kr.modusplant.domains.member.framework.out.jpa.repository.supers.MemberUuidPrimaryKeyJpaRepository;
import kr.modusplant.shared.persistence.repository.LastModifiedAtRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Primary
public interface MemberProfileJpaRepository extends
        LastModifiedAtRepository<MemberProfileEntity>,
        MemberUuidPrimaryKeyJpaRepository<MemberProfileEntity>,
        JpaRepository<MemberProfileEntity, UUID> {
}