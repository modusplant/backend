package kr.modusplant.domains.communication.qna.persistence.entity;


import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class QnaLikeId implements Serializable {
    private String qnaPostId;
    private UUID memberId;
}
