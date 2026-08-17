package kr.modusplant.infrastructure.file.persistence.jpa.repository;

import kr.modusplant.infrastructure.file.persistence.jpa.entity.PendingFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PendingFileJpaRepository extends JpaRepository<PendingFileEntity, String> {

    @Query("SELECT p.fileKey FROM PendingFileEntity p WHERE p.createdAt < :threshold")
    List<String> findFileKeysByCreatedAtBefore(@Param("threshold") LocalDateTime threshold);

    @Query("SELECT p.fileKey FROM PendingFileEntity p WHERE p.fileKey IN :fileKeys")
    List<String> findFileKeysByFileKeyIn(@Param("fileKeys") List<String> fileKeys);

    @Modifying
    @Query("DELETE FROM PendingFileEntity p WHERE p.createdAt < :threshold")
    void deleteByCreatedAtBefore(@Param("threshold") LocalDateTime threshold);

    @Modifying
    @Query("DELETE FROM PendingFileEntity p WHERE p.fileKey IN :fileKeys")
    void deleteByFileKeyIn(@Param("fileKeys") List<String> fileKeys);
}
