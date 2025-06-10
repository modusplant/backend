package kr.modusplant.domains.communication.qna.persistence.repository;

import kr.modusplant.domains.communication.common.persistence.supers.CommunicationPostRepository;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaCategoryEntity;
import kr.modusplant.domains.communication.qna.persistence.entity.QnaPostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QnaPostRepository extends CommunicationPostRepository<QnaPostEntity, QnaCategoryEntity> {

    @Query(
            value = "SELECT * FROM qna_post p " +
                    "WHERE p.is_deleted = false AND (" +
                    "p.title ILIKE %:keyword% OR " +
                    "EXISTS (" +
                    "   SELECT 1 FROM jsonb_array_elements(p.content) c " +
                    "   WHERE c->>'type' = 'text' AND c->>'data' ILIKE %:keyword% " +
                    ")) " +
                    "ORDER BY p.created_at desc",
            countQuery = "SELECT COUNT(*) FROM qna_post p " +
                    "WHERE p.is_deleted = false AND (" +
                    "p.title ILIKE %:keyword% OR " +
                    "EXISTS (" +
                    "   SELECT 1 FROM jsonb_array_elements(p.content) c " +
                    "   WHERE c->>'type' = 'text' AND c->>'data' ILIKE %:keyword% " +
                    ")) " +
                    "ORDER BY p.created_at desc",
            nativeQuery = true
    )
    Page<QnaPostEntity> searchByTitleOrContent(@Param("keyword") String keyword, Pageable pageable);

    @Modifying
    @Query("UPDATE QnaPostEntity t SET t.viewCount = :viewCount WHERE t.ulid = :ulid AND t.viewCount < :viewCount")
    int updateViewCount(@Param("ulid") String ulid, @Param("viewCount") Long viewCount);
}
