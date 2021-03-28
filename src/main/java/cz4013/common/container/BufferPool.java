package cz4013.common.container;

import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class BufferPool {
  private Queue<ByteBuffer> queue = new LinkedBlockingQueue<>();

  public BufferPool(int bufferSize, int poolSize) {
    for (int i = 0; i < poolSize; ++i) {
      putBack(ByteBuffer.allocate(bufferSize));
    }
  }

  public PooledByteBuffer take() {
    ByteBuffer byteBuffer = queue.remove();
    byteBuffer.clear();
    return new PooledByteBuffer(this, byteBuffer);
  }

  void putBack(ByteBuffer byteBuffer) {
    queue.add(byteBuffer);
  }
}
