package kr.modusplant.framework.out.jpa.entity.compositekey;


import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CommPostLikeId implements Serializable {
    private String postId;
    private UUID memberId;
}
