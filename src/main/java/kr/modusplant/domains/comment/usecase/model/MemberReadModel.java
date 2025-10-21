package kr.modusplant.domains.comment.usecase.model;

import java.util.UUID;

public record MemberReadModel(
        UUID memberUuid,
        String nickname,
        boolean isActive
) {
}
