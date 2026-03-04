package kr.modusplant.framework.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.framework.jpa.generator.UlidGenerator;
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

import static kr.modusplant.shared.persistence.constant.TableColumnName.*;
import static kr.modusplant.shared.persistence.constant.TableName.COMM_COMMENT_ABU_REP;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = COMM_COMMENT_ABU_REP)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class CommCommentAbuRepEntity {
    @Id
    @UlidGenerator
    @Column(nullable = false, updatable = false)
    private String ulid;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = MEMB_UUID, nullable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    private SiteMemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumns({
            @JoinColumn(name = POST_ULID, referencedColumnName = POST_ULID, nullable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT)),
            @JoinColumn(name = PATH, referencedColumnName = PATH, nullable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    })
    @ToString.Exclude
    private CommCommentEntity comment;

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
        return getUlid() + "-" + getVersionNumber();
    }

    public LocalDateTime getLastModifiedAtAsTruncatedToSeconds() {
        return getLastModifiedAt().truncatedTo(ChronoUnit.SECONDS);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;

        if (!(object instanceof CommCommentAbuRepEntity that)) return false;

        return new EqualsBuilder().append(getUlid(), that.getUlid()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getUlid()).toHashCode();
    }

    private CommCommentAbuRepEntity(String ulid, SiteMemberEntity member, CommCommentEntity comment, LocalDateTime checkedAt, LocalDateTime handledAt) {
        this.ulid = ulid;
        this.member = member;
        this.comment = comment;
        this.checkedAt = checkedAt;
        this.handledAt = handledAt;
    }

    public static CommCommentAbuRepEntityBuilder builder() {
        return new CommCommentAbuRepEntityBuilder();
    }

    public static final class CommCommentAbuRepEntityBuilder {
        private String ulid;
        private SiteMemberEntity member;
        private CommCommentEntity comment;
        private LocalDateTime checkedAt;
        private LocalDateTime handledAt;

        public CommCommentAbuRepEntityBuilder ulid(final String ulid) {
            this.ulid = ulid;
            return this;
        }

        public CommCommentAbuRepEntityBuilder member(final SiteMemberEntity member) {
            this.member = member;
            return this;
        }

        public CommCommentAbuRepEntityBuilder comment(final CommCommentEntity comment) {
            this.comment = comment;
            return this;
        }

        public CommCommentAbuRepEntityBuilder checkedAt(final LocalDateTime checkedAt) {
            this.checkedAt = checkedAt;
            return this;
        }

        public CommCommentAbuRepEntityBuilder handledAt(final LocalDateTime handledAt) {
            this.handledAt = handledAt;
            return this;
        }

        public CommCommentAbuRepEntityBuilder commCommentAbuRep(final CommCommentAbuRepEntity commentAbuRep) {
            this.ulid = commentAbuRep.getUlid();
            this.member = commentAbuRep.getMember();
            this.comment = commentAbuRep.getComment();
            this.checkedAt = commentAbuRep.getCheckedAt();
            this.handledAt = commentAbuRep.getHandledAt();
            return this;
        }

        public CommCommentAbuRepEntity build() {
            return new CommCommentAbuRepEntity(this.ulid, this.member, this.comment, this.checkedAt, this.handledAt);
        }
    }
}
