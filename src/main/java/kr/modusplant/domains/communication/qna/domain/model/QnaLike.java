package kr.modusplant.domains.communication.qna.domain.model;

import lombok.*;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Builder(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class QnaLike {
    private String postId;
    private UUID memberId;
}