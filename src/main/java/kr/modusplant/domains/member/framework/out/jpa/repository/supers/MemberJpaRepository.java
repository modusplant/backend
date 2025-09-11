package kr.modusplant.domains.member.framework.out.jpa.repository.supers;

import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.shared.persistence.repository.supers.CreatedAtAndLastModifiedAtRepository;
import kr.modusplant.shared.persistence.repository.supers.UuidPrimaryKeyRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MemberJpaRepository extends JpaRepository<MemberEntity, UUID>, CreatedAtAndLastModifiedAtRepository<MemberEntity>, UuidPrimaryKeyRepository<MemberEntity> {
}