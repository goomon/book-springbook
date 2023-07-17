package study.springbook.exception;

public class DuplicateMemberIdException extends RuntimeException {

    public DuplicateMemberIdException(Throwable cause) {
        super(cause);
    }
}
