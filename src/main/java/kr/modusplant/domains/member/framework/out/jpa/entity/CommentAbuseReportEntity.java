package kr.modusplant.domains.member.framework.out.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.domains.comment.framework.out.persistence.jpa.entity.CommentEntity;
import kr.modusplant.domains.member.framework.out.jpa.compositekey.CommentAbuseReportCompositeKey;
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
import static kr.modusplant.shared.persistence.constant.TableName.COMM_COMMENT_ABU_REP;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = COMM_COMMENT_ABU_REP)
@IdClass(CommentAbuseReportCompositeKey.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class CommentAbuseReportEntity {
    @Id
    private UUID memberId;

    @MapsId("memberId")
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = MEMB_UUID, nullable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @ToString.Exclude
    private MemberEntity member;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, optional = false)
    @JoinColumns({
            @JoinColumn(name = POST_ULID, referencedColumnName = POST_ULID, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT)),
            @JoinColumn(name = PATH, referencedColumnName = PATH, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    })
    @ToString.Exclude
    private CommentEntity comment;

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
        return getMemberId() + "-" + getComment().getPost().getUlid() + "-" + getComment().getPath() + "-" + getVersionNumber();
    }

    public LocalDateTime getLastModifiedAtAsTruncatedToSeconds() {
        return getLastModifiedAt().truncatedTo(ChronoUnit.SECONDS);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;

        if (!(object instanceof CommentAbuseReportEntity that)) return false;

        return new EqualsBuilder().append(getMember(), that.getMember()).append(getComment(), that.getComment()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getMember()).append(getComment()).toHashCode();
    }

    private CommentAbuseReportEntity(MemberEntity member, CommentEntity comment, LocalDateTime checkedAt, LocalDateTime handledAt) {
        this.member = member;
        this.comment = comment;
        this.checkedAt = checkedAt;
        this.handledAt = handledAt;
    }

    public static CommentAbuseReportEntityBuilder builder() {
        return new CommentAbuseReportEntityBuilder();
    }

    public static final class CommentAbuseReportEntityBuilder {
        private MemberEntity member;
        private CommentEntity comment;
        private LocalDateTime checkedAt;
        private LocalDateTime handledAt;

        public CommentAbuseReportEntityBuilder member(final MemberEntity member) {
            this.member = member;
            return this;
        }

        public CommentAbuseReportEntityBuilder comment(final CommentEntity comment) {
            this.comment = comment;
            return this;
        }

        public CommentAbuseReportEntityBuilder checkedAt(final LocalDateTime checkedAt) {
            this.checkedAt = checkedAt;
            return this;
        }

        public CommentAbuseReportEntityBuilder handledAt(final LocalDateTime handledAt) {
            this.handledAt = handledAt;
            return this;
        }

        public CommentAbuseReportEntityBuilder commentAbuseReport(final CommentAbuseReportEntity commentAbuRep) {
            this.member = commentAbuRep.getMember();
            this.comment = commentAbuRep.getComment();
            this.checkedAt = commentAbuRep.getCheckedAt();
            this.handledAt = commentAbuRep.getHandledAt();
            return this;
        }

        public CommentAbuseReportEntity build() {
            return new CommentAbuseReportEntity(this.member, this.comment, this.checkedAt, this.handledAt);
        }
    }
}
