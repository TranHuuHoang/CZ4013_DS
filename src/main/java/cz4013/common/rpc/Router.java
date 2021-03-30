package cz4013.common.rpc;

import cz4013.common.container.LruCache;
import cz4013.common.request.RequestHeader;
import cz4013.common.response.Response;
import cz4013.common.response.ResponseStatus;
import cz4013.common.marshalling.MarshallingException;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

import static cz4013.common.marshalling.Unmarshaller.unmarshall;

public class Router {
  private final LruCache<UUID, Response<?>> lruCache;
  private final Map<String, Route> routeMap;

  public Router(LruCache<UUID, Response<?>> lruCache) {
    this.lruCache = lruCache;
    routeMap = new HashMap<>();
  }

  public <ReqBody, RespBody> Router bind(
    String method,
    Function<ReqBody, RespBody> handler,
    Object reqBody
  ) {
    routeMap.put(method, new Route(
      reqBody,
      (req, remote) -> ((Function<Object, Object>) handler).apply(req)
    ));
    return this;
  }

  public <ReqBody, RespBody> Router bind(
    String method,
    BiFunction<ReqBody, SocketAddress, RespBody> handler,
    Object reqBody
  ) {
    routeMap.put(method, new Route(
      reqBody,
      (req, remote) -> ((BiFunction<Object, SocketAddress, Object>) handler).apply(req, remote)
    ));
    return this;
  }

  public Response<?> route(Message req) {
    RequestHeader header = unmarshall(new RequestHeader() {}, req.payload.get());
    return lruCache.get(header.uuid).orElseGet(() -> {
      Response<?> resp;

      try {
        Route route = routeMap.get(header.requestMethod);
        if (route == null) {
          resp = Response.failed(header.uuid, ResponseStatus.NOT_FOUND);
        }
        else {
          resp = Response.ok(header.uuid, route.handler.apply(unmarshall(route.reqBody, req.payload.get().slice()), req.remoteSocketAddress));
        }
      } catch (MarshallingException e) {
        resp = Response.failed(header.uuid, ResponseStatus.MALFORMED);
      } catch (Exception e) {
        System.out.print(header.uuid);
        e.printStackTrace();
        resp = Response.failed(header.uuid, ResponseStatus.INTERNAL_ERR);
      }
      lruCache.put(header.uuid, resp);
      return resp;
    });
  }
}
