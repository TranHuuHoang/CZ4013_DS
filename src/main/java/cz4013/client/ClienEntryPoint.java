package cz4013.client;

import cz4013.client.communication.Client;
import cz4013.client.communication.FacilityClientWrapper;
import cz4013.client.exceptions.FailedRequestException;
import cz4013.client.exceptions.NoResponseException;
import cz4013.common.container.BufferPool;
import cz4013.common.rpc.MessageComm;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.time.Duration;
import java.util.Map;
import java.util.Scanner;

public class ClienEntryPoint {
  public static void main(String[] args) throws SocketException {
    Map<String, String> env = System.getenv();
    String clientHost = env.getOrDefault("CLIENT_HOST", "0.0.0.0");
    String serverHost = env.getOrDefault("SERVER_HOST", "127.0.0.1");
    int clientPort = Integer.parseInt(env.getOrDefault("CLIENT_PORT", "12741"));
    int serverPort = Integer.parseInt(env.getOrDefault("SERVER_PORT", "12740"));
    Duration timeout = Duration.ofSeconds(Integer.parseInt(env.getOrDefault("TIMEOUT_SEC", "5")));
    int maxAttempts = Integer.parseInt(env.getOrDefault("MAX_ATTEMPTS", "5"));
    DatagramSocket socket = new DatagramSocket(new InetSocketAddress(clientHost, clientPort));
    socket.setSoTimeout((int) timeout.toMillis());
    String options = "----------------------------------------------------------------\n" +
      "Please choose a service by typing [0-8]:\n" +
      "1: Add a facility\n" +
      "2: Query the availability of a facility\n" +
      "3: Book a timeslot\n" +
      "4: Change a booked timeslot\n" +
      "5: Monitor the availability of a facility\n" +
      "6: Cancel a booking\n" +
      "7: Shift a booking towards one day\n" +
      "8: Print the list of options\n" +
      "0: Stop the service\n";

    FacilityClientWrapper facilityClient = new FacilityClientWrapper(new Client(
      new MessageComm(socket, new BufferPool(8192, 1024)),
      new InetSocketAddress(serverHost, serverPort), maxAttempts));

    Scanner sc = new Scanner(System.in);
    boolean shouldStop = false;
    System.out.print(options);
    while (!shouldStop) {
      int userChoice = Integer.parseInt(sc.nextLine());
      try {
        switch (userChoice) {
          case 1:
            facilityClient.runAddFacility();
            break;
          case 2:
            facilityClient.runQueryFacility();
            break;
          case 3:
            facilityClient.runBooking();
            break;
          case 4:
            facilityClient.runChangeBooking();
            break;
          case 5:
            facilityClient.runMonitor();
            break;
          case 6:
            facilityClient.runCancelBooking();
            break;
          case 7:
            facilityClient.runShiftBooking();
            break;
          case 8:
            System.out.println(options);
            break;
          case 0:
            shouldStop = true;
            break;
          default:
            System.out.println("Invalid choice!");
            break;
        }
      } catch (NoResponseException e) {
        System.out.println(e.noResponseMessage);
      } catch (FailedRequestException e) {
        System.out.printf("Failed to send request with error %s \n", e.status);
      }
    }

    System.out.println("Client Stopping...");
  }
}
