package kr.modusplant.infrastructure.jwt.claim;

import java.util.UUID;

public record UploadTokenClaims(
        String recordId,
        UUID memberId,
        String videoKey,
        String originalFilename
) {
}
