package kr.modusplant.legacy.domains.communication.domain.model;

import lombok.*;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommCommentLike {
    private String postId;
    private UUID memberId;
}