package kr.modusplant.infrastructure.event;

import java.util.UUID;

public record CommPostLikedEvent(UUID memberId, String postId) {
}
