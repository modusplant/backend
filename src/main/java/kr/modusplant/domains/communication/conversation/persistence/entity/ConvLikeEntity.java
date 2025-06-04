package kr.modusplant.domains.communication.conversation.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

import static kr.modusplant.global.vo.SnakeCaseWord.*;

@Entity
@Table(name = SNAKE_CONV_LIKE)
@IdClass(ConvLikeId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class ConvLikeEntity {
    @Id
    @Column(name = SNAKE_POST_ULID, nullable = false)
    private String convPostId;

    @Id
    @Column(name = SNAKE_MEMB_UUID, nullable = false)
    private UUID memberId;

    @Column(name = SNAKE_CREATED_AT, nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    private ConvLikeEntity(String convPostId, UUID memberId) {
        this.convPostId = convPostId;
        this.memberId = memberId;
    }

    public static ConvLikeEntity of(String convPostId, UUID memberId) {
        return new ConvLikeEntity(convPostId, memberId);
    }
}
