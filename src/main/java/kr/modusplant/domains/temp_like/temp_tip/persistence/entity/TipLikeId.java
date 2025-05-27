package kr.modusplant.domains.temp_like.temp_tip.persistence.entity;


import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TipLikeId implements Serializable {
    private String tipPostId;
    private UUID memberId;
}
