package kr.modusplant.domains.term.framework.out.jpa.repository;

import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.domains.term.framework.out.jpa.entity.MemberTermEntity;
import kr.modusplant.shared.persistence.repository.LastModifiedAtRepository;
import kr.modusplant.shared.persistence.repository.UuidPrimaryKeyRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
public interface MemberTermJpaRepository extends
        LastModifiedAtRepository<MemberTermEntity>,
        UuidPrimaryKeyRepository<MemberTermEntity>,
        JpaRepository<MemberTermEntity, UUID> {
    Optional<MemberTermEntity> findByMember(MemberEntity member);

    List<MemberTermEntity> findByAgreedTermsOfUseVersion(String agreedTermsOfUseVersion);

    List<MemberTermEntity> findByAgreedPrivacyPolicyVersion(String agreedPrivacyPolicyVersion);

    List<MemberTermEntity> findByAgreedCommunityPolicyVersion(String agreedCommunityPolicyVersion);
}