package cz4013.common.rpc;

import cz4013.common.container.BufferPool;
import cz4013.common.container.PooledByteBuffer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

import static cz4013.common.marshalling.Marshaller.marshall;

public class MessageComm {
  DatagramSocket socket;
  BufferPool bufferPool;

  public MessageComm(DatagramSocket socket, BufferPool bufferPool) {
    this.socket = socket;
    this.bufferPool = bufferPool;
  }

  public <T> void send(SocketAddress dest, T obj) {
    try (PooledByteBuffer buf = bufferPool.take()) {
      marshall(obj, buf.get());
      byte[] rawBuf = buf.get().array();
      socket.send(new DatagramPacket(rawBuf, rawBuf.length, dest));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Message receive() {
    PooledByteBuffer buf = bufferPool.take();
    byte[] rawBuf = buf.get().array();
    DatagramPacket packet = new DatagramPacket(rawBuf, rawBuf.length);
    try {
      socket.receive(packet);
      return new Message(packet.getSocketAddress(), buf);
    } catch (Exception e) {
      buf.close();
      throw new RuntimeException(e);
    }
  }
}
