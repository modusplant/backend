package kr.modusplant.domains.communication.tip.persistence.repository;

import kr.modusplant.domains.common.persistence.repository.supers.CreatedAtAndUpdatedAtRepository;
import kr.modusplant.domains.common.persistence.repository.supers.UlidPrimaryRepository;
import kr.modusplant.domains.group.persistence.entity.PlantGroupEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.communication.tip.persistence.entity.TipPostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface TipPostRepository extends UlidPrimaryRepository<TipPostEntity>, CreatedAtAndUpdatedAtRepository<TipPostEntity>, JpaRepository<TipPostEntity,String> {
    Page<TipPostEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<TipPostEntity> findAllByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    Page<TipPostEntity> findByGroupAndIsDeletedFalseOrderByCreatedAtDesc(PlantGroupEntity group, Pageable pageable);

    Page<TipPostEntity> findByAuthMemberAndIsDeletedFalseOrderByCreatedAtDesc(SiteMemberEntity authMember, Pageable pageable);

}
