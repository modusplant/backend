package kr.modusplant.domains.member.framework.outbound.jpa.compositekey;

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
public class PostLikeCompositeKey implements Serializable {
    private String postId;
    private UUID memberId;
}
