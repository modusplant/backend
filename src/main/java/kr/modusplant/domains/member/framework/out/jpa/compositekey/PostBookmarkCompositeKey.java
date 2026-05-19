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
public class PostBookmarkCompositeKey implements Serializable {
    private String postId;
    private UUID memberId;
}
