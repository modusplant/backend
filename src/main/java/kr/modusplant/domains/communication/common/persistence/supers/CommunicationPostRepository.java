package kr.modusplant.domains.communication.common.persistence.supers;

import kr.modusplant.domains.common.persistence.repository.supers.CreatedAtAndUpdatedAtRepository;
import kr.modusplant.domains.common.persistence.repository.supers.UlidPrimaryRepository;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@NoRepositoryBean
public interface CommunicationPostRepository<T, S> extends UlidPrimaryRepository<T>, CreatedAtAndUpdatedAtRepository<T>, JpaRepository<T, String> {

    Page<T> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<T> findByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    Page<T> findByCategoryAndIsDeletedFalseOrderByCreatedAtDesc(S category, Pageable pageable);

    Page<T> findByAuthMemberAndIsDeletedFalseOrderByCreatedAtDesc(SiteMemberEntity authMember, Pageable pageable);

    Optional<T> findByUlidAndIsDeletedFalse(String ulid);

    Page<T> searchByTitleOrContent(@Param("keyword") String keyword, Pageable pageable);

    int updateViewCount(@Param("ulid") String ulid, @Param("viewCount") Long viewCount);
}
