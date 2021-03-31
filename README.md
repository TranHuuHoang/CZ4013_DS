**Run**
- To run server: Download an IDE or use command line to run the file `src/main/java/cz4013/server/ServerEntryPoint.java`.
  The default config is:
  ```
  host=0.0.0.0
  port=49152 
  atMostOnce=0
  packetLossRate=0.0
  ```
  In order to change the config, edit the field `host`, `port`, `atMostOnce` and `packetLossRate` inside the `main` function of `src/main/java/cz4013/server/ServerEntryPoint.java`.
  
- To run client: Download an IDE or use command line to run the file `src/main/java/cz4013/client/ClienEntryPoint.java`.
  Default config:
  ```
  clientHost=0.0.0.0
  clientPort=49153
  serverHost=127.0.0.1
  serverPort=49152
  timeout=5 (sec)
  maxAttempts=5
  ```
  In order to change the config, edit the field `clientHost`, `clientPort`, `serverHost`, `serverPort`, `timeout` and `maxAttempts` inside the `main` function of `src/main/java/cz4013/client/ClienEntryPoint.java`.
