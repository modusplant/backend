package kr.modusplant.domains.communication.conversation.domain.model;

import lombok.*;

import java.util.UUID;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@Builder(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConvLike {
    private String convPostId;
    private UUID memberId;
}