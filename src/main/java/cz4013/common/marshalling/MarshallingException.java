package cz4013.common.marshalling;

public class MarshallingException extends RuntimeException {
  public MarshallingException(String message) {
    super(message);
  }

  public MarshallingException(String message, Throwable cause) {
    super(message, cause);
  }
}
