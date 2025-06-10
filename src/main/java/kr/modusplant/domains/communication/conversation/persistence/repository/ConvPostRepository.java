package kr.modusplant.domains.communication.conversation.persistence.repository;

import kr.modusplant.domains.common.persistence.repository.supers.CreatedAtAndUpdatedAtRepository;
import kr.modusplant.domains.common.persistence.repository.supers.UlidPrimaryRepository;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCategoryEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvPostEntity;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConvPostRepository extends UlidPrimaryRepository<ConvPostEntity>, CreatedAtAndUpdatedAtRepository<ConvPostEntity>, JpaRepository<ConvPostEntity,String> {
    Page<ConvPostEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<ConvPostEntity> findByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    Page<ConvPostEntity> findByCategoryAndIsDeletedFalseOrderByCreatedAtDesc(ConvCategoryEntity category, Pageable pageable);

    Page<ConvPostEntity> findByAuthMemberAndIsDeletedFalseOrderByCreatedAtDesc(SiteMemberEntity authMember, Pageable pageable);

    Optional<ConvPostEntity> findByUlidAndIsDeletedFalse(String ulid);

    @Query(
            value = "SELECT * FROM conv_post p " +
                    "WHERE p.is_deleted = false AND (" +
                    "p.title ILIKE %:keyword% OR " +
                    "EXISTS (" +
                    "   SELECT 1 FROM jsonb_array_elements(p.content) c " +
                    "   WHERE c->>'type' = 'text' AND c->>'data' ILIKE %:keyword% " +
                    ")) " +
                    "ORDER BY p.created_at desc",
            countQuery = "SELECT COUNT(*) FROM conv_post p " +
                    "WHERE p.is_deleted = false AND (" +
                    "p.title ILIKE %:keyword% OR " +
                    "EXISTS (" +
                    "   SELECT 1 FROM jsonb_array_elements(p.content) c " +
                    "   WHERE c->>'type' = 'text' AND c->>'data' ILIKE %:keyword% " +
                    ")) " +
                    "ORDER BY p.created_at desc",
            nativeQuery = true
    )
    Page<ConvPostEntity> searchByTitleOrContent(@Param("keyword") String keyword, Pageable pageable);

    @Modifying
    @Query("UPDATE ConvPostEntity t SET t.viewCount = :viewCount WHERE t.ulid = :ulid AND t.viewCount < :viewCount")
    int updateViewCount(@Param("ulid") String ulid, @Param("viewCount") Long viewCount);
}
