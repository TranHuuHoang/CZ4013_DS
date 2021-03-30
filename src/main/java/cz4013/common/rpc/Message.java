package cz4013.common.rpc;

import cz4013.common.container.PooledByteBuffer;

import java.net.SocketAddress;

public class Message implements AutoCloseable {
  public SocketAddress remoteSocketAddress;
  public PooledByteBuffer payload;

  public Message(SocketAddress remoteSocketAddress, PooledByteBuffer payload) {
    this.remoteSocketAddress = remoteSocketAddress;
    this.payload = payload;
  }

  @Override
  public void close() {
    payload.close();
  }
}
