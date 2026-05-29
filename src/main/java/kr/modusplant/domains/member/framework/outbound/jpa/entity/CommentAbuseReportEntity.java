package kr.modusplant.domains.member.framework.outbound.jpa.entity;

import jakarta.persistence.*;
import kr.modusplant.domains.comment.framework.outbound.persistence.jpa.entity.CommentEntity;
import kr.modusplant.domains.member.framework.outbound.jpa.compositekey.CommentAbuseReportCompositeKey;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
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

    @Column(name = CREATED_AT, nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    public String getETagSource() {
        return getMemberId() + "-" + getComment().getPost().getUlid() + "-" + getComment().getPath();
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

    private CommentAbuseReportEntity(MemberEntity member, CommentEntity comment) {
        this.member = member;
        this.comment = comment;
    }

    public static CommentAbuseReportEntityBuilder builder() {
        return new CommentAbuseReportEntityBuilder();
    }

    public static final class CommentAbuseReportEntityBuilder {
        private MemberEntity member;
        private CommentEntity comment;

        public CommentAbuseReportEntityBuilder member(final MemberEntity member) {
            this.member = member;
            return this;
        }

        public CommentAbuseReportEntityBuilder comment(final CommentEntity comment) {
            this.comment = comment;
            return this;
        }

        public CommentAbuseReportEntityBuilder commentAbuseReport(final CommentAbuseReportEntity commentAbuRep) {
            this.member = commentAbuRep.getMember();
            this.comment = commentAbuRep.getComment();
            return this;
        }

        public CommentAbuseReportEntity build() {
            return new CommentAbuseReportEntity(this.member, this.comment);
        }
    }
}
