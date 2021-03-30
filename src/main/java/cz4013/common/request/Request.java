package cz4013.common.request;

import java.util.Objects;

public class Request<ReqBody> {
  public RequestHeader requestHeader;
  public ReqBody reqBody;

  public Request(RequestHeader requestHeader, ReqBody reqBody) {
    this.requestHeader = requestHeader;
    this.reqBody = reqBody;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Request)) return false;
    Request<?> request = (Request<?>) o;
    return Objects.equals(requestHeader, request.requestHeader) &&
      Objects.equals(reqBody, request.reqBody);
  }
}

