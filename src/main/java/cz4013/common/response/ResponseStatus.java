package cz4013.common.response;

public enum ResponseStatus {
    NOT_FOUND("service not found"),
    MALFORMED("malformed request"),
    INTERNAL_ERR("internal server error"),
    OK("ok");

    private final String reason;

    ResponseStatus(final String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return reason;
    }
}
