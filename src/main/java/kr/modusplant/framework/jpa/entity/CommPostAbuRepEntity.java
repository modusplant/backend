package kr.modusplant.framework.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.shared.persistence.compositekey.CommPostAbuRepId;
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
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static kr.modusplant.shared.persistence.constant.TableColumnName.*;
import static kr.modusplant.shared.persistence.constant.TableName.COMM_POST_ABU_REP;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = COMM_POST_ABU_REP)
@IdClass(CommPostAbuRepId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class CommPostAbuRepEntity {
    @Id
    private UUID memberId;

    @MapsId("memberId")
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = MEMB_UUID, nullable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    private SiteMemberEntity member;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = POST_ULID, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    private CommPostEntity post;

    @Column(name = CHECKED_AT)
    private LocalDateTime checkedAt;

    @Column(name = HANDLED_AT)
    private LocalDateTime handledAt;

    @Column(name = CREATED_AT, nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = LAST_MODIFIED_AT, nullable = false)
    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

    @Version
    @Column(name = VER_NUM, nullable = false)
    @ToString.Exclude
    private Long versionNumber;

    public String getETagSource() {
        return getMemberId() + "-" + getPost().getUlid() + "-" + getVersionNumber();
    }

    public LocalDateTime getLastModifiedAtAsTruncatedToSeconds() {
        return getLastModifiedAt().truncatedTo(ChronoUnit.SECONDS);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;

        if (!(object instanceof CommPostAbuRepEntity that)) return false;

        return new EqualsBuilder()
                .append(getMember(), that.getMember())
                .append(getPost(), that.getPost())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getMember()).append(getPost()).toHashCode();
    }

    private CommPostAbuRepEntity(SiteMemberEntity member, CommPostEntity post, LocalDateTime checkedAt, LocalDateTime handledAt) {
        this.member = member;
        this.post = post;
        this.checkedAt = checkedAt;
        this.handledAt = handledAt;
    }

    public static CommPostAbuRepEntityBuilder builder() {
        return new CommPostAbuRepEntityBuilder();
    }

    public static final class CommPostAbuRepEntityBuilder {
        private SiteMemberEntity member;
        private CommPostEntity post;
        private LocalDateTime checkedAt;
        private LocalDateTime handledAt;

        public CommPostAbuRepEntityBuilder member(final SiteMemberEntity member) {
            this.member = member;
            return this;
        }

        public CommPostAbuRepEntityBuilder post(final CommPostEntity post) {
            this.post = post;
            return this;
        }

        public CommPostAbuRepEntityBuilder checkedAt(final LocalDateTime checkedAt) {
            this.checkedAt = checkedAt;
            return this;
        }

        public CommPostAbuRepEntityBuilder handledAt(final LocalDateTime handledAt) {
            this.handledAt = handledAt;
            return this;
        }

        public CommPostAbuRepEntityBuilder commPostAbuRep(final CommPostAbuRepEntity postAbuRep) {
            this.member = postAbuRep.getMember();
            this.post = postAbuRep.getPost();
            this.checkedAt = postAbuRep.getCheckedAt();
            this.handledAt = postAbuRep.getHandledAt();
            return this;
        }

        public CommPostAbuRepEntity build() {
            return new CommPostAbuRepEntity(this.member, this.post, this.checkedAt, this.handledAt);
        }
    }
}
