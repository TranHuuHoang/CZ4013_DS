package cz4013.common.serialization;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import static cz4013.common.serialization.Utils.resetBuffer;
import static cz4013.common.serialization.Utils.serializableFields;

public class Serializer {

  public static ByteBuffer serialize(Object o, ByteBuffer buffer) {
    resetBuffer(buffer);
    try {
      outputStruct(o, buffer);
      return buffer;
    } catch (BufferOverflowException e) {
      throw new SerializingException("Buffer has not enough space.", e);
    }
  }

  public static void outputStruct(Object o, ByteBuffer buffer) {
    int pos = buffer.position();

    if (o instanceof Serializable) {
      ((Serializable) o).serialize(buffer);
    } else {
      serializableFields(o.getClass())
        .forEach(field -> {
          try {
            write(field.get(o), buffer);
          } catch (IllegalAccessException e) {
            assert false : "Unable to access " + field.getName() + " of " + o.getClass().getName();
          }
        });
    }


    assert pos < buffer.position() : o.getClass().getName() + " is not serializable.";
  }

  public static void write(Object x, ByteBuffer buffer) {
    if (x.getClass().isEnum()) {
      Enum<?> e = (Enum<?>) x;
      buffer.put((byte) e.ordinal());
    } else if (x instanceof Byte) {
      buffer.put((Byte) x);
    } else if (x instanceof Boolean) {
      buffer.put((byte) ((Boolean) x ? 1 : 0));
    } else if (x instanceof Character) {
      buffer.putChar((Character) x);
    } else if (x instanceof Short) {
      buffer.putShort((Short) x);
    } else if (x instanceof Integer) {
      buffer.putInt((Integer) x);
    } else if (x instanceof Float) {
      buffer.putFloat((Float) x);
    } else if (x instanceof Long) {
      buffer.putLong((Long) x);
    } else if (x instanceof Double) {
      buffer.putDouble((Double) x);
    } else if (x instanceof String) {
      byte[] utf8 = ((String) x).getBytes(StandardCharsets.UTF_8);
      buffer.putInt(utf8.length);
      buffer.put(utf8);
    } else if (x instanceof Optional) {
      write(((Optional) x), buffer);
    } else if (x instanceof Iterable<?>) {
      write(((Iterable<?>) x), buffer);
    } else if (x instanceof UUID) {
      write((UUID) x, buffer);
    } else {
      outputStruct(x, buffer);
    }
  }

  public static void write(Optional<?> x, ByteBuffer buffer) {
    if (!x.isPresent()) {
      buffer.put((byte) 0);
    } else {
      buffer.put((byte) 1);
      write(x.get(), buffer);
    }
  }

  public static <T> void write(Iterable<T> s, ByteBuffer buffer) {
    int i = buffer.position();

    buffer.putInt(0);

    s.forEach(x -> write(x, buffer));


    int length = buffer.position() - i - 4;
    buffer.putInt(i, length);
  }

  public static void write(UUID x, ByteBuffer buffer) {
    buffer.putLong(x.getMostSignificantBits());
    buffer.putLong(x.getLeastSignificantBits());
  }
}
