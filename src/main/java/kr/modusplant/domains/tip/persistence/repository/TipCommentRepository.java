package kr.modusplant.domains.tip.persistence.repository;

import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import kr.modusplant.domains.tip.persistence.entity.TipCommentEntity;
import kr.modusplant.domains.tip.persistence.entity.compositekey.TipCommentCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipCommentRepository extends JpaRepository<TipCommentEntity, TipCommentCompositeKey> {

    Optional<TipCommentEntity> findByPostUlidAndMaterializedPath(String postUlid, String materializedPath);

    List<TipCommentEntity> findByPostUlid(String postUlid);

    List<TipCommentEntity> findByAuthMember(SiteMemberEntity authMember);

    List<TipCommentEntity> findByCreateMember(SiteMemberEntity createMember);

    List<TipCommentEntity> findByContent(String content);
}
