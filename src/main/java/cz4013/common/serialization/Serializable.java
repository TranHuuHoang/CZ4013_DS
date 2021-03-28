package cz4013.common.serialization;

import java.nio.ByteBuffer;

/**
 * Interface for classes which implements custom serialization/deserialization.
 */
public interface Serializable {
  void deserialize(ByteBuffer buf);

  void serialize(ByteBuffer buf);
}
