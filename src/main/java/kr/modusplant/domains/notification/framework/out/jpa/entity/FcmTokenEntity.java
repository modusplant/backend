package kr.modusplant.domains.notification.framework.out.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.domains.member.framework.out.jpa.entity.MemberEntity;
import kr.modusplant.shared.enums.Platform;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static kr.modusplant.shared.persistence.constant.TableColumnName.*;
import static kr.modusplant.shared.persistence.constant.TableName.FCM_TOKEN;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = FCM_TOKEN)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class FcmTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = MEMB_UUID, nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private MemberEntity member;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @ToString.Include
    private Platform platform;

    @Column(name = CREATED_AT, nullable = false)
    @CreatedDate
    @ToString.Include
    private LocalDateTime createdAt;

    @Column(name = LAST_MODIFIED_AT, nullable = false)
    @LastModifiedDate
    @ToString.Include
    private LocalDateTime lastModifiedAt;

    public void updateMember(MemberEntity member) {
        this.member = member;
    }

    public void updatePlatform(Platform platform) {
        this.platform = platform;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FcmTokenEntity that)) return false;
        return new EqualsBuilder().append(getId(), that.getId()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17,37).append(getId()).toHashCode();
    }

    private FcmTokenEntity(Long id, MemberEntity member, String token, Platform platform) {
        this.id = id;
        this.member = member;
        this.token = token;
        this.platform = platform;
    }

    public static FcmTokenEntityBuilder builder() {
        return new FcmTokenEntityBuilder();
    }

    public static final class FcmTokenEntityBuilder {
        private Long id;
        private MemberEntity member;
        private String token;
        private Platform platform;

        public FcmTokenEntityBuilder id(final Long id) {
            this.id = id;
            return this;
        }

        public FcmTokenEntityBuilder member(final MemberEntity member) {
            this.member = member;
            return this;
        }

        public FcmTokenEntityBuilder token(final String token) {
            this.token = token;
            return this;
        }

        public FcmTokenEntityBuilder platform(final Platform platform) {
            this.platform = platform;
            return this;
        }

        public FcmTokenEntityBuilder fcmToken(final FcmTokenEntity fcmTokenEntity) {
            this.id = fcmTokenEntity.getId();
            this.member = fcmTokenEntity.getMember();
            this.token = fcmTokenEntity.getToken();
            this.platform = fcmTokenEntity.getPlatform();
            return this;
        }

        public FcmTokenEntity build() {
            return new FcmTokenEntity(this.id, this.member, this.token, this.platform);
        }
    }

}
