package kr.modusplant.domains.member.framework.outbound.jpa.compositekey;

import kr.modusplant.domains.comment.framework.outbound.persistence.jpa.compositekey.CommentCompositeKey;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CommentAbuseReportCompositeKey implements Serializable {
    private UUID memberId;
    private CommentCompositeKey comment;
}
