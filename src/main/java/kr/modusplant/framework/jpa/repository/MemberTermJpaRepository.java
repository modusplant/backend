package kr.modusplant.framework.jpa.repository;

import kr.modusplant.framework.jpa.entity.MemberTermEntity;
import kr.modusplant.framework.jpa.repository.supers.MemberUuidPrimaryKeyJpaRepository;
import kr.modusplant.shared.persistence.repository.LastModifiedAtRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@Primary
public interface MemberTermJpaRepository extends
        LastModifiedAtRepository<MemberTermEntity>,
        MemberUuidPrimaryKeyJpaRepository<MemberTermEntity>,
        JpaRepository<MemberTermEntity, UUID> {
    List<MemberTermEntity> findByAgreedTermsOfUseVersion(String agreedTermsOfUseVersion);

    List<MemberTermEntity> findByAgreedPrivacyPolicyVersion(String agreedPrivacyPolicyVersion);

    List<MemberTermEntity> findByAgreedCommunityPolicyVersion(String agreedCommunityPolicyVersion);
}