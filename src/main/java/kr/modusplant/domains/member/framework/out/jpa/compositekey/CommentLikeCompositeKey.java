package kr.modusplant.domains.member.framework.out.jpa.compositekey;

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
public class CommentLikeCompositeKey implements Serializable {
    private String postId;
    private String path;
    private UUID memberId;
}
