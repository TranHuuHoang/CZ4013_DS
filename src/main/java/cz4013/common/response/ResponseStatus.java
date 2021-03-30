package cz4013.common.response;

public enum ResponseStatus {
    OK("Status OK"),
    NOT_FOUND("Service Not Found"),
    MALFUNCTIONED("Request Is Malformed"),
    INTERNAL_ERROR("Internal Server Error");

    private final String reason;

    ResponseStatus(final String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return reason;
    }
}
