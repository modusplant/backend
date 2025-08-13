package kr.modusplant.legacy.domains.communication.persistence.entity;


import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CommLikeId implements Serializable {
    private String postId;
    private UUID memberId;
}
