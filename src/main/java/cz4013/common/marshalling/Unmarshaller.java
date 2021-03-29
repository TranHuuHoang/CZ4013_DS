package cz4013.common.marshalling;

import one.util.streamex.StreamEx;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static cz4013.common.marshalling.Utils.marshallableFields;

public class Unmarshaller {
  public static Map<String, Class> EMPTY_TYPE_MAP = new HashMap<>();
  
  public static <T> T unmarshall(T obj, ByteBuffer buf) {
    buf.order(ByteOrder.LITTLE_ENDIAN);
    buf.clear();
    try {
      Class<?> currClass = obj.getClass();
      assert currClass.isAnonymousClass() : "The given object must be created with anonymous class syntax.";
      return (T) inputStruct(currClass.getGenericSuperclass(), buf, EMPTY_TYPE_MAP);
    } catch (BufferOverflowException e) {
      throw new MarshallingException("Data is corrupted", e);
    }
  }

  public static Object inputStruct(Type type, ByteBuffer buffer, Map<String, Class> parentTypeMap) {
    Class<?> currClass;
    Map<String, Class> typeMap = EMPTY_TYPE_MAP;
    if (type instanceof ParameterizedType) {
      ParameterizedType pty = (ParameterizedType) type;
      currClass = ((Class) pty.getRawType());
      typeMap = makeTypeMap(pty, parentTypeMap);
    } else {
      currClass = (Class<?>) type;
    }

    Object obj = currInstance(currClass);
    if (obj instanceof Marshallable) {
      ((Marshallable) obj).unmarshall(buffer);
    } else {
      Map<String, Class> finalTypeMap = typeMap;
      marshallableFields(currClass)
        .forEach(field -> read(obj, field, buffer, finalTypeMap));
    }

    return obj;
  }

  private static <T> T currInstance(Class<T> currClass) {
    try {
      return currClass.newInstance();
    } catch (InstantiationException e) {
      assert false : "Unable to create an instance, " + currClass.getName() + ". Make sure class indicated is not abstract and has a 0-argument constructor.";
    } catch (IllegalAccessException e) {
      assert false : "Unable to access " + currClass.getName() + ". Ensure that class is public.";
    }

    throw new RuntimeException("!!!!");
  }

  public static Object readInput(Type type, ByteBuffer buffer, Map<String, Class> typeMap) {
    if (type == Byte.TYPE || type == Byte.class) {
      return buffer.get();
    }

    if (type == Boolean.TYPE || type == Boolean.class) {
      Byte b = buffer.get();
      switch (b) {
        case 1:
          return true;

        case 0:
          return false;

        default:
          throw new MarshallingException(
            String.format("Unexpected byte, %d when reading a bool value at offset %d", b, buffer.position())
          );
      }
    }

    if (type == Short.TYPE || type == Short.class) {
      return buffer.getShort();
    }

    if (type == Character.TYPE || type == Character.class) {
      return buffer.getChar();
    }

    if (type == Integer.TYPE || type == Integer.class) {
      return buffer.getInt();
    }

    if (type == Float.TYPE || type == Float.class) {
      return buffer.getFloat();
    }

    if (type == Long.TYPE || type == Long.class) {
      return buffer.getLong();
    }

    if (type == Double.TYPE || type == Double.class) {
      return buffer.getDouble();
    }

    if (type == String.class) {
      int len = buffer.getInt();
      byte[] utf8 = new byte[len];
      buffer.get(utf8);
      return new String(utf8, StandardCharsets.UTF_8);
    }

    if (type == UUID.class) {
      return new UUID(buffer.getLong(), buffer.getLong());
    }

    if (ParameterizedType.class.isAssignableFrom(type.getClass())) {
      ParameterizedType pty = (ParameterizedType) type;
      Class<?> currClass = (Class<?>) pty.getRawType();

      if (Optional.class.isAssignableFrom(currClass)) {
        Byte b = buffer.get();
        switch (b) {
          case 0:
            return Optional.empty();

          case 1:
            return Optional.of(readInput(
              resolveGenericType(pty.getActualTypeArguments()[0], typeMap),
              buffer,
              typeMap
            ));

          default:
            throw new MarshallingException(
              String.format("Unexpected tag %d while reading an optional value at offset %d", b, buffer.position())
            );
        }
      }

      if (Collection.class.isAssignableFrom(currClass)) {
        Collection<Object> s = ((Collection<Object>) currInstance(currClass));
        int length = buffer.getInt();
        int pos = buffer.position();

        for (; buffer.position() - pos < length; s.add(readInput(resolveGenericType(pty.getActualTypeArguments()[0], typeMap), buffer, typeMap)))
          ;

        int read = buffer.position() - pos;
        if (read != length) {
          throw new MarshallingException(
            String.format(
              "Mismatch collection length, expected %d bytes, read %d bytes at offset %d.",
              length,
              read,
              buffer.position()
            )
          );
        }
        return s;
      }

      return inputStruct(type, buffer, typeMap);
    }

    Class<?> currClass = (Class) type;
    if (currClass.isEnum()) {
      int i = buffer.get();
      Object[] c = currClass.getEnumConstants();
      if (i < 0 || i > c.length) {
        throw new MarshallingException(
          String.format("Invalid ordinal %d of %s at offset %d.", i, currClass.getName(), buffer.position())
        );
      }

      return c[i];
    }

    return inputStruct(type, buffer, typeMap);
  }

  public static void read(Object obj, Field field, ByteBuffer buffer, Map<String, Class> typeMap) {
    try {
      Type type = field.getGenericType();
      if (type instanceof TypeVariable) {
        type = typeMap.get(((TypeVariable) type).getName());
      }
      Object r = readInput(type, buffer, typeMap);
      if (type == Byte.TYPE) {
        field.setByte(obj, (Byte) r);
      } else if (type == Boolean.TYPE) {
        field.setBoolean(obj, (Boolean) r);
      } else if (type == Short.TYPE) {
        field.setShort(obj, (Short) r);
      } else if (type == Character.TYPE) {
        field.setChar(obj, (Character) r);
      } else if (type == Integer.TYPE) {
        field.setInt(obj, (Integer) r);
      } else if (type == Float.TYPE) {
        field.setFloat(obj, (Float) r);
      } else if (type == Long.TYPE) {
        field.setLong(obj, (Long) r);
      } else if (type == Double.TYPE) {
        field.setDouble(obj, (Double) r);
      } else {
        field.set(obj, r);
      }
    } catch (IllegalAccessException e) {
      assert false : "Unable to write to field " + field.getName() + " of " + obj.getClass().getName();
    }
  }


  public static Map<String, Class> makeTypeMap(ParameterizedType pty, Map<String, Class> parentTypeMap) {
    return StreamEx.of(((Class<?>) pty.getRawType()).getTypeParameters())
      .map(TypeVariable::getName)
      .zipWith(StreamEx.of(pty.getActualTypeArguments())
        .map(arg -> (arg instanceof Class) ? (Class) arg : parentTypeMap.get(((TypeVariable) arg).getName()))
      )
      .toMap();
  }

  public static Type resolveGenericType(Type type, Map<String, Class> typeMap) {
    if (type instanceof TypeVariable) {
      return typeMap.get(((TypeVariable) type).getName());
    }
    return type;
  }
}
