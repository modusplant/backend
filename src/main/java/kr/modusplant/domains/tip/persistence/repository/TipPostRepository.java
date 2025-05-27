package kr.modusplant.domains.tip.persistence.repository;

import kr.modusplant.domains.common.persistence.repository.supers.CreatedAtAndUpdatedAtRepository;
import kr.modusplant.domains.common.persistence.repository.supers.UlidPrimaryRepository;
import kr.modusplant.domains.group.persistence.entity.PlantGroupEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.tip.persistence.entity.TipPostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipPostRepository extends UlidPrimaryRepository<TipPostEntity>, CreatedAtAndUpdatedAtRepository<TipPostEntity>, JpaRepository<TipPostEntity,String> {
    Page<TipPostEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<TipPostEntity> findByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    Page<TipPostEntity> findByGroupAndIsDeletedFalseOrderByCreatedAtDesc(PlantGroupEntity group, Pageable pageable);

    Page<TipPostEntity> findByAuthMemberAndIsDeletedFalseOrderByCreatedAtDesc(SiteMemberEntity authMember, Pageable pageable);

    Optional<TipPostEntity> findByUlidAndIsDeletedFalse(String ulid);

    @Query(
            value = "SELECT * FROM tip_post p " +
                    "WHERE p.is_deleted = false AND (" +
                    "p.title ILIKE %:keyword% OR " +
                    "EXISTS (" +
                    "   SELECT 1 FROM jsonb_array_elements(p.content) c " +
                    "   WHERE c->>'type' = 'text' AND c->>'data' ILIKE %:keyword% " +
                    ")) " +
                    "ORDER BY p.created_at desc",
            countQuery = "SELECT COUNT(*) FROM tip_post p " +
                    "WHERE p.is_deleted = false AND (" +
                    "p.title ILIKE %:keyword% OR " +
                    "EXISTS (" +
                    "   SELECT 1 FROM jsonb_array_elements(p.content) c " +
                    "   WHERE c->>'type' = 'text' AND c->>'data' ILIKE %:keyword% " +
                    ")) " +
                    "ORDER BY p.created_at desc",
            nativeQuery = true
    )
    Page<TipPostEntity> searchByTitleOrContent(@Param("keyword") String keyword, Pageable pageable);

    @Modifying
    @Query("UPDATE TipPostEntity t SET t.viewCount = :viewCount WHERE t.ulid = :ulid AND t.viewCount < :viewCount")
    int updateViewCount(@Param("ulid") String ulid, @Param("viewCount") Long viewCount);
}
