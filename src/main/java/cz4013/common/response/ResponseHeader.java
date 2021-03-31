package cz4013.common.response;

import java.util.Objects;
import java.util.UUID;

public class ResponseHeader {
    public UUID uuid;
    public ResponseStatus status;

    public ResponseHeader() {}

    public ResponseHeader(UUID uuid, ResponseStatus status) {
        this.uuid = uuid;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResponseHeader)) return false;
        ResponseHeader header = (ResponseHeader) o;
        return Objects.equals(uuid, header.uuid) &&
            status == header.status;
    }

    @Override
    public String toString() {
        return "ResponseHeader(" + uuid + ", " + status + ")";
    }
}
