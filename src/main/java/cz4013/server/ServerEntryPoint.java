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

public class ServerEntryPoint {
  public static void main(String[] args) throws SocketException {
    String host = "0.0.0.0";
    int port = Integer.parseInt("49152");
    boolean atMostOnce = Integer.parseInt("0") != 0;
    double packetLossRate = Double.parseDouble("0.0");

    BufferPool pool = new BufferPool(8192, 1024);
    MessageComm server = new MessageComm(new DatagramSocket(new InetSocketAddress(host, port)), pool);
    System.out.printf("Listening on udp://%s:%d\n", host, port);

    FacilityService facilityService = new FacilityService(server);
    Router router = new Router(new LruCache<>(atMostOnce ? 1024 : 0))
      .bind("addFacility", facilityService::doAddFacility, new AddFacilityRequestBody() {})
      .bind("queryFacility", facilityService::doQueryFacility, new QueryFacilityRequestBody() {})
      .bind("booking", facilityService::doBooking, new BookingRequestBody() {})
      .bind("changeBooking", facilityService::doChangeBooking, new ChangeBookingRequestBody() {})
      .bind("monitor", facilityService::doMonitor, new MonitorRequestBody() {})
      .bind("cancelBooking", facilityService::doCancelBooking, new CancelBookingRequestBody() {})
      .bind("shiftBooking", facilityService::doShiftBooking, new ShiftBookingRequestBody() {});

    while (true) {
      try (Message req = server.receive()) {
        if (Math.random() < packetLossRate) {
          System.out.printf("Dropped a request from %s.\n", req.remoteSocketAddress);
          continue;
        }
        Response<?> resp = router.route(req);
        if (Math.random() < packetLossRate) {
          System.out.printf("Dropped a response to %s.\n", req.remoteSocketAddress);
        } else {
          server.send(req.remoteSocketAddress, resp);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
