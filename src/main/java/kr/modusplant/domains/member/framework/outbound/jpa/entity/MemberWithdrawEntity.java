package kr.modusplant.domains.member.framework.outbound.jpa.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static kr.modusplant.shared.persistence.constant.TableName.SITE_MEMBER_WITHDRAW;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = SITE_MEMBER_WITHDRAW)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class MemberWithdrawEntity {
    @Id
    private UUID uuid;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = "uuid", nullable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    private MemberEntity member;

    @Column(nullable = false, updatable = false, length = 40)
    private String reason;

    @Column(updatable = false, length = 600)
    private String opinion;

    @Column(name = "withdrawn_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime withdrawnAt;

    public String getETagSource() {
        return String.valueOf(getUuid());
    }

    public LocalDateTime getWithdrawnAtAsTruncatedToSeconds() {
        return getWithdrawnAt().truncatedTo(ChronoUnit.SECONDS);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MemberWithdrawEntity that)) return false;
        return new EqualsBuilder().append(getMember(), that.getMember()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getMember()).toHashCode();
    }

    private MemberWithdrawEntity(MemberEntity member, String reason, String opinion) {
        this.member = member;
        this.reason = reason;
        this.opinion = opinion;
    }

    public static MemberWithdrawEntityBuilder builder() {
        return new MemberWithdrawEntityBuilder();
    }

    public static final class MemberWithdrawEntityBuilder {
        private MemberEntity member;
        private String reason;
        private String opinion;

        public MemberWithdrawEntityBuilder member(final MemberEntity member) {
            this.member = member;
            return this;
        }

        public MemberWithdrawEntityBuilder reason(final String reason) {
            this.reason = reason;
            return this;
        }

        public MemberWithdrawEntityBuilder opinion(final String opinion) {
            this.opinion = opinion;
            return this;
        }

        public MemberWithdrawEntityBuilder memberWithdraw(final MemberWithdrawEntity memberWithdraw) {
            this.member = memberWithdraw.getMember();
            this.reason = memberWithdraw.getReason();
            this.opinion = memberWithdraw.getOpinion();
            return this;
        }

        public MemberWithdrawEntity build() {
            return new MemberWithdrawEntity(this.member, this.reason, this.opinion);
        }
    }
}
