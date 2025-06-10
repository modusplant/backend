package kr.modusplant.domains.communication.common.error;

import org.springframework.security.access.AccessDeniedException;

public class PostAccessDeniedException extends AccessDeniedException {
    public PostAccessDeniedException() {
        super("Post access denied.");
    }
}
