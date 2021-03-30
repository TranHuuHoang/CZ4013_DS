package cz4013.common.container;

import java.nio.ByteBuffer;

public class PooledByteBuffer implements AutoCloseable {
  private BufferPool parent;
  private ByteBuffer byteBuffer;

  public PooledByteBuffer(BufferPool parent, ByteBuffer byteBuffer) {
    this.parent = parent;
    this.byteBuffer = byteBuffer;
  }

  public ByteBuffer get() {
    return byteBuffer;
  }

  @Override
  public void close() {
    parent.put(byteBuffer);
  }
}
