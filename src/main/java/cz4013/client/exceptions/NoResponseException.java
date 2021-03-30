package cz4013.client.exceptions;

public class NoResponseException extends RuntimeException {
    public final String noResponseMessage;
    public NoResponseException() {
        this.noResponseMessage = "No Response Received from Server!";
    }
}
