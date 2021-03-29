package cz4013.server;

import cz4013.common.rpc.Router;
import cz4013.server.service.FacilityService;
import cz4013.common.container.BufferPool;
import cz4013.common.container.LruCache;
import cz4013.common.request.reqbody.*;
import cz4013.common.response.Response;
import cz4013.common.rpc.Message;
import cz4013.common.rpc.MessageComm;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Map;

public class ServerEntryPoint {
  public static void main(String[] args) throws SocketException {
    Map<String, String> env = System.getenv();
    String host = env.getOrDefault("HOST", "0.0.0.0");
    int port = Integer.parseInt(env.getOrDefault("PORT", "12740"));
    boolean atMostOnce = Integer.parseInt(env.getOrDefault("AT_MOST_ONCE", "0")) != 0;
    double packetLossRate = Double.parseDouble(env.getOrDefault("PACKET_LOSS_RATE", "0.0"));

    BufferPool pool = new BufferPool(8192, 1024);
    MessageComm server = new MessageComm(new DatagramSocket(new InetSocketAddress(host, port)), pool);
    System.out.printf("Listening on udp://%s:%d\n", host, port);

    FacilityService svc = new FacilityService(server);
    Router r = new Router(new LruCache<>(atMostOnce ? 1024 : 0))
      .bind("addFacility", svc::processAddFacility, new AddFacilityRequestBody() {})
      .bind("queryFacility", svc::processQueryFacility, new QueryFacilityRequestBody() {})
      .bind("booking", svc::processBooking, new BookingRequestBody() {})
      .bind("changeBooking", svc::processChangeBooking, new ChangeBookingRequestBody() {})
      .bind("monitor", svc::processMonitor, new MonitorRequestBody() {})
      .bind("cancelBooking", svc::processCancelBooking, new CancelBookingRequestBody() {})
      .bind("shiftBooking", svc::processShiftBooking, new ShiftBookingRequestBody() {});

    while (true) {
      try (Message req = server.receive()) {
        if (Math.random() < packetLossRate) {
          System.out.printf("Dropped a request from %s.\n", req.remote);
          continue;
        }
        Response<?> resp = r.route(req);
        if (Math.random() < packetLossRate) {
          System.out.printf("Dropped a response to %s.\n", req.remote);
        } else {
          server.send(req.remote, resp);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
