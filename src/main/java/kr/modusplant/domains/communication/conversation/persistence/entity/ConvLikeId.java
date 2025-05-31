package kr.modusplant.domains.communication.conversation.persistence.entity;


import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ConvLikeId implements Serializable {
    private String convPostId;
    private UUID memberId;
}
