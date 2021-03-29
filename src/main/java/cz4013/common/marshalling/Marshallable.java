package cz4013.common.marshalling;

import java.nio.ByteBuffer;

public interface Marshallable {
  void unmarshall(ByteBuffer buf);

  void marshall(ByteBuffer buf);
}
