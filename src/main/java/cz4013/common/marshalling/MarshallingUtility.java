package cz4013.common.marshalling;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.stream.Stream;

class MarshallingUtility {
  static Stream<Field> marshallableFields(Class<?> clazz) {
    return Arrays.stream(clazz.getFields())
      .filter(field -> Modifier.isPublic(field.getModifiers()) && !Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())
      );
  }

  static void resetBuffer(ByteBuffer buf) {
    buf.clear();
    buf.order(ByteOrder.LITTLE_ENDIAN);
  }
}
