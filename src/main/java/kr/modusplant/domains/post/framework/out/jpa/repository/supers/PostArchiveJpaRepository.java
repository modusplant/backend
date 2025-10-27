package kr.modusplant.domains.post.framework.out.jpa.repository.supers;

import kr.modusplant.domains.post.framework.out.jpa.entity.PostArchiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostArchiveJpaRepository extends JpaRepository<PostArchiveEntity, String> {
}
