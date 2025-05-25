package kr.modusplant.domains.temp_like.temp_tip.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.global.vo.SnakeCaseWord.*;

@Entity
@Table(name = SNAKE_TIP_LIKE)
@IdClass(TipLikeId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TipLikeEntity {
    @Id
    @Column(name = SNAKE_POST_ULID, nullable = false)
    private String tipPostId;

    @Id
    @Column(name = SNAKE_MEMB_UUID, nullable = false)
    private UUID memberId;

    @Column(name = SNAKE_CREATED_AT, nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    private TipLikeEntity(String tipPostId, UUID memberId) {
        this.tipPostId = tipPostId;
        this.memberId = memberId;
    }

    public static TipLikeEntity of(String tipPostId, UUID memberId) {
        return new TipLikeEntity(tipPostId, memberId);
    }
}
