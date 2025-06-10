package kr.modusplant.domains.communication.common.persistence.supers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.UUID;

@NoRepositoryBean
public interface CommunicationLikeRepository<T, ID> extends JpaRepository<T, ID> {

    List<T> findByMemberId(UUID memberId);

    List<T> findByMemberIdAndPostIdIn(UUID memberId, List<String> postIds);

    boolean existsByPostIdAndMemberId(String postId, UUID memberId);

    void deleteByPostIdAndMemberId(String postId, UUID memberId);
}
