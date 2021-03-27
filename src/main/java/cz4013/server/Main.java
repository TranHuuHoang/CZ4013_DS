package cz4013.server;

import cz4013.server.rpc.Router;
import cz4013.server.service.FacilityService;
import cz4013.shared.container.BufferPool;
import cz4013.shared.container.LruCache;
import cz4013.shared.request.*;
import cz4013.shared.response.Response;
import cz4013.shared.rpc.RawMessage;
import cz4013.shared.rpc.Transport;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Map;

public class Main {
  public static void main(String[] args) throws SocketException {
    Map<String, String> env = System.getenv();
    String host = env.getOrDefault("HOST", "0.0.0.0");
    int port = Integer.parseInt(env.getOrDefault("PORT", "12740"));
    boolean atMostOnce = Integer.parseInt(env.getOrDefault("AT_MOST_ONCE", "0")) != 0;
    double packetLossRate = Double.parseDouble(env.getOrDefault("PACKET_LOSS_RATE", "0.0"));

    BufferPool pool = new BufferPool(8192, 1024);
    Transport server = new Transport(new DatagramSocket(new InetSocketAddress(host, port)), pool);
    System.out.printf("Listening on udp://%s:%d\n", host, port);

    FacilityService svc = new FacilityService(server);
    Router r = new Router(new LruCache<>(atMostOnce ? 1024 : 0))
      .bind("addFacility", svc::processAddFacility, new AddFacilityRequest() {})
      .bind("queryFacility", svc::processQueryFacility, new QueryFacilityRequest() {})
      .bind("booking", svc::processBooking, new BookingRequest() {})
      .bind("changeBooking", svc::processChangeBooking, new ChangeBookingRequest() {})
      .bind("monitor", svc::processMonitor, new MonitorRequest() {})
      .bind("cancelBooking", svc::processCancelBooking, new CancelBookingRequest() {})
      .bind("shiftBooking", svc::processShiftBooking, new ShiftBookingRequest() {});

    for (; ; ) {
      try (RawMessage req = server.recv()) {
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
