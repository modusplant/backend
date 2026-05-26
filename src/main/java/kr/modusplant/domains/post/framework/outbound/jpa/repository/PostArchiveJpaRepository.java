package kr.modusplant.domains.post.framework.outbound.jpa.repository;

import kr.modusplant.domains.post.framework.outbound.jpa.entity.PostArchiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostArchiveJpaRepository extends JpaRepository<PostArchiveEntity, String> {
}
