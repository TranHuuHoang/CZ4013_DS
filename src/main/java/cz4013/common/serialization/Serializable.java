package cz4013.common.serialization;

import java.nio.ByteBuffer;

public interface Serializable {
  void deserialize(ByteBuffer buf);

  void serialize(ByteBuffer buf);
}
