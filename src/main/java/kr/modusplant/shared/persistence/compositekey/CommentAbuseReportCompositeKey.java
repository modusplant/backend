package kr.modusplant.shared.persistence.compositekey;

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
