package by.innowise.user_service.exception;


public class UnaccessibleCardException extends RuntimeException {
  public UnaccessibleCardException() {
    super("You have no access to this card");
  }

  public UnaccessibleCardException(String message) {
    super(message);
  }

  public UnaccessibleCardException(String message, Throwable cause) {
    super(message, cause);
  }
}
