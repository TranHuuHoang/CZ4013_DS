package cz4013.common.rpc;

import cz4013.common.container.PooledByteBuffer;

import java.net.SocketAddress;

public class RawMessage implements AutoCloseable {
  public SocketAddress remote;
  public PooledByteBuffer payload;

  public RawMessage(SocketAddress remote, PooledByteBuffer payload) {
    this.remote = remote;
    this.payload = payload;
  }

  @Override
  public void close() {
    payload.close();
  }
}
