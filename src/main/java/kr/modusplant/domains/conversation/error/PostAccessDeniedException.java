package kr.modusplant.domains.conversation.error;

import java.util.Map;

public class PostAccessDeniedException extends RuntimeException {
    public enum Action {
        UPDATE, DELETE
    }

    private static final String DEFAULT_MESSAGE = "Post access denied";
    private static final Map<Action, String> ACTION_MESSAGE = Map.of(
            Action.UPDATE, "Post update access denied",
            Action.DELETE, "Post delete access denied"
    );

    public PostAccessDeniedException() {
        super(DEFAULT_MESSAGE);
    }

    public PostAccessDeniedException(Action action) {
        super(action == null ? DEFAULT_MESSAGE : ACTION_MESSAGE.getOrDefault(action, DEFAULT_MESSAGE));
    }

    public PostAccessDeniedException(String message) {
        super(message);
    }

}
