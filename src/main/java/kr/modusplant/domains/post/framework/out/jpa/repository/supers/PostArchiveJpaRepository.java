package kr.modusplant.domains.post.framework.out.jpa.repository.supers;

import kr.modusplant.framework.jpa.entity.CommPostArchiveEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostArchiveJpaRepository extends JpaRepository<CommPostArchiveEntity, String> {
}
