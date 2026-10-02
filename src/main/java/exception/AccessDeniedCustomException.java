package Nathanael.expense_tracker.exception;

public class AccessDeniedCustomException extends RuntimeException {

    public AccessDeniedCustomException(String message) {
        super(message);
    }
}