package kr.modusplant.domains.communication.common.persistence.supers;

import kr.modusplant.domains.member.persistence.entity.SiteMemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface CommunicationCommentRepository<T, S, ID> extends JpaRepository<T, ID> {

    Optional<T> findByPostUlidAndPath(String postUlid, String path);

    List<T> findByPostEntity(S postEntity);

    List<T> findByAuthMember(SiteMemberEntity authMember);

    List<T> findByCreateMember(SiteMemberEntity createMember);

    List<T> findByContent(String content);

    void deleteByPostUlidAndPath(String postUlid, String path);

    boolean existsByPostUlidAndPath(String postUlid, String path);
}
