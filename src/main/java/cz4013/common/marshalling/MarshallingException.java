package cz4013.common.marshalling;

public class MarshallingException extends RuntimeException {
  public MarshallingException() {
  }

  public MarshallingException(String message) {
    super(message);
  }

  public MarshallingException(String message, Throwable cause) {
    super(message, cause);
  }

  public MarshallingException(Throwable cause) {
    super(cause);
  }

  public MarshallingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
