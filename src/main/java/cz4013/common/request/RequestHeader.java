package cz4013.common.request;

import java.util.Objects;
import java.util.UUID;

public class RequestHeader {
  public UUID uuid;
  public String requestMethod;

  public RequestHeader() {}

  public RequestHeader(UUID uuid, String requestMethod) {
    this.uuid = uuid;
    this.requestMethod = requestMethod;
  }

  @Override
  public String toString() {
    return "RequestHeader(" + uuid + ", " + requestMethod + ")";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof RequestHeader))
      return false;

    RequestHeader header = (RequestHeader) obj;
    return Objects.equals(uuid, header.uuid) &&
            Objects.equals(requestMethod, header.requestMethod);
  }
}
