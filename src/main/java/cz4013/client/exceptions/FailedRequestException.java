package cz4013.client.exceptions;

import cz4013.common.response.ResponseStatus;

public class FailedRequestException extends RuntimeException {
  public final ResponseStatus status;

  public FailedRequestException(ResponseStatus status) {
    this.status = status;
  }
}
