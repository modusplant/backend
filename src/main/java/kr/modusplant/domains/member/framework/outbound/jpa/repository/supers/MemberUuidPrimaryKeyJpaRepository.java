package kr.modusplant.domains.member.framework.outbound.jpa.repository.supers;

import kr.modusplant.domains.member.framework.outbound.jpa.entity.MemberEntity;
import kr.modusplant.shared.persistence.repository.UuidPrimaryKeyRepository;

import java.util.Optional;

public interface MemberUuidPrimaryKeyJpaRepository<T> extends UuidPrimaryKeyRepository<T> {
    Optional<T> findByMember(MemberEntity member);
}
