package cz4013.common.serialization;

import cz4013.common.currency.Currency;
import cz4013.common.response.respbody.MonitorStatusResponseBody;
import cz4013.common.response.respbody.MonitorUpdateResponseBody;
import org.junit.Test;

import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class ResponseSerializeDeserializeTest {
  @Test
  public void OpenAccountResponseTest() {
    OpenAccountResponse response = new OpenAccountResponse(123);
    ByteBuffer b = Serializer.serialize(response, ByteBuffer.allocate(8192));
    OpenAccountResponse deserialized = Deserializer.deserialize(new OpenAccountResponse() {}, b);
    assertEquals(response.toString(), deserialized.toString());
  }

  @Test
  public void CloseAccountResponseTest() {
    CloseAccountResponse response = new CloseAccountResponse(true, "");
    ByteBuffer b = Serializer.serialize(response, ByteBuffer.allocate(8192));
    CloseAccountResponse deserialized = Deserializer.deserialize(new CloseAccountResponse() {}, b);
    assertEquals(response.toString(), deserialized.toString());
  }

  @Test
  public void DepositResponseTest() {
    DepositResponse response = new DepositResponse(Currency.USD, 10, true, "");
    ByteBuffer b = Serializer.serialize(response, ByteBuffer.allocate(8192));
    DepositResponse deserialized = Deserializer.deserialize(new DepositResponse() {}, b);
    assertEquals(response.toString(), deserialized.toString());
  }

  @Test
  public void MonitorStatusResponseTest() {
    MonitorStatusResponseBody response = new MonitorStatusResponseBody(true);
    ByteBuffer b = Serializer.serialize(response, ByteBuffer.allocate(8192));
    MonitorStatusResponseBody deserialized = Deserializer.deserialize(new MonitorStatusResponseBody() {}, b);
    assertEquals(response.toString(), deserialized.toString());
  }

  @Test
  public void MonitorUpdateResponseTest() {
    MonitorUpdateResponseBody response = new MonitorUpdateResponseBody("asdf");
    ByteBuffer b = Serializer.serialize(response, ByteBuffer.allocate(8192));
    MonitorUpdateResponseBody deserialized = Deserializer.deserialize(new MonitorUpdateResponseBody() {}, b);
    assertEquals(response.toString(), deserialized.toString());
  }

  @Test
  public void QueryResponseTest() {
    QueryResponse response = new QueryResponse("asdf", Currency.USD, 1.2, true, "");
    ByteBuffer b = Serializer.serialize(response, ByteBuffer.allocate(8192));
    QueryResponse deserialized = Deserializer.deserialize(new QueryResponse() {}, b);
    assertEquals(response.toString(), deserialized.toString());
  }

  @Test
  public void PayMaintenanceResponseTest() {
    PayMaintenanceFeeResponse response = new PayMaintenanceFeeResponse(Currency.USD, 1.2, true, "");
    ByteBuffer b = Serializer.serialize(response, ByteBuffer.allocate(8192));
    PayMaintenanceFeeResponse deserialized = Deserializer.deserialize(new PayMaintenanceFeeResponse() {}, b);
    assertEquals(response.toString(), deserialized.toString());
  }
}
