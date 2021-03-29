package cz4013.client.communication;

import cz4013.client.exceptions.FailedRequestException;
import cz4013.client.exceptions.NoResponseException;
import cz4013.common.request.Request;
import cz4013.common.request.RequestHeader;
import cz4013.common.response.Response;
import cz4013.common.response.ResponseStatus;
import cz4013.common.rpc.RawMessage;
import cz4013.common.rpc.Transport;

import java.io.InterruptedIOException;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Consumer;

import static cz4013.common.marshalling.Unmarshaller.unmarshall;

public class Client {
  private final Transport transport;
  private final int maxTryAttempts;
  private final SocketAddress serverAddress;

  public Client(Transport transport, SocketAddress serverAddress, int maxTryAttempts) {
    this.transport = transport;
    this.serverAddress = serverAddress;
    this.maxTryAttempts = maxTryAttempts;
  }

  public <ReqBody, RespBody> RespBody request(String method, ReqBody reqBody, Response<RespBody> respObj) {
    UUID id = UUID.randomUUID();

    for (int numTriesLeft = maxTryAttempts; numTriesLeft > 0; --numTriesLeft) {
      try {
        transport.send(serverAddress, new Request<>(new RequestHeader(id, method), reqBody));
        try (RawMessage rawResp = transport.receive()) {
          Response<RespBody> resp = unmarshall(respObj, rawResp.payload.get());
          if (!resp.header.uuid.equals(id)) {
            continue;
          }

          if (resp.header.status != ResponseStatus.OK) {
            throw new FailedRequestException(resp.header.status);
          }

          return resp.body.get();
        }
      } catch (RuntimeException e) {
        if (e.getCause() instanceof SocketTimeoutException) {
          System.out.println("Socket timeout, retrying...");
          continue;
        }

        throw e;
      }
    }

    throw new NoResponseException();
  }

  public <RespBody> void poll(Response<RespBody> respObj, Duration interval, Consumer<RespBody> callback) {
    Instant end = Instant.now().plus(interval);

    Thread pollingThread = new Thread(() -> {
      while (true){
        if (Instant.now().isAfter(end)) {
          return;
        }
        try (RawMessage msg = transport.receive()) {
          unmarshall(respObj, msg.payload.get()).body.ifPresent(callback);
        } catch (RuntimeException e) {
          if (e.getCause() instanceof SocketTimeoutException) {
            continue;
          }

          if (e.getCause() instanceof InterruptedIOException) {
            return;
          }
        }
      }
    });
    pollingThread.run();

    try {
      pollingThread.join(interval.toMillis());
    } catch (InterruptedException e) {
    }

    pollingThread.interrupt();
  }
}
