package kr.modusplant.legacy.domains.member.persistence.repository.supers;

import kr.modusplant.legacy.domains.common.persistence.repository.supers.UuidPrimaryKeyRepository;
import kr.modusplant.legacy.domains.member.persistence.entity.SiteMemberEntity;

import java.util.Optional;

public interface SiteMemberUuidPrimaryKeyRepository<T> extends UuidPrimaryKeyRepository<T> {
    Optional<T> findByMember(SiteMemberEntity member);
}
