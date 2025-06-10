package kr.modusplant.domains.communication.conversation.persistence.repository;

import kr.modusplant.domains.communication.conversation.persistence.entity.ConvCommentEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.ConvPostEntity;
import kr.modusplant.domains.communication.conversation.persistence.entity.compositekey.ConvCommentCompositeKey;
import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConvCommentRepository extends JpaRepository<ConvCommentEntity, ConvCommentCompositeKey> {

    Optional<ConvCommentEntity> findByPostUlidAndPath(String postUlid, String path);

    List<ConvCommentEntity> findByPostEntity(ConvPostEntity postEntity);

    List<ConvCommentEntity> findByAuthMember(SiteMemberEntity authMember);

    List<ConvCommentEntity> findByCreateMember(SiteMemberEntity createMember);

    List<ConvCommentEntity> findByContent(String content);

    void deleteByPostUlidAndPath(String postUlid, String path);

    boolean existsByPostUlidAndPath(String postUlid, String path);
}
