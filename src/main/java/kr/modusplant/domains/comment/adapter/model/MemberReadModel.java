package kr.modusplant.domains.comment.adapter.model;

import java.util.UUID;

public record MemberReadModel(
        UUID memberUuid,
        String nickname,
        boolean isActive
) {
}
