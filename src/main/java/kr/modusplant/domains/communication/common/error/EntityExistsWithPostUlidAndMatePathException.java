package kr.modusplant.domains.communication.common.error;

import jakarta.persistence.EntityExistsException;

public class EntityExistsWithPostUlidAndMatePathException extends EntityExistsException {
    public EntityExistsWithPostUlidAndMatePathException(String message) {
        super(message);
    }

    public EntityExistsWithPostUlidAndMatePathException() {super("Entity exists with postUlid and path"); }
}
