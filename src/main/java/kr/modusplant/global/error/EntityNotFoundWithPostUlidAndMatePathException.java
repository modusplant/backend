package kr.modusplant.global.error;

public class EntityNotFoundWithPostUlidAndMatePathException extends RuntimeException {
    public EntityNotFoundWithPostUlidAndMatePathException(String message) {
        super(message);
    }
    public EntityNotFoundWithPostUlidAndMatePathException() { super("Entity not found with postUlid and matePath"); }
}
