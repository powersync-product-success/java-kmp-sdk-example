# Java Kotlin SDK Integration Example

This example shows how PowerSync can be used in a Java 8+ application.

## Description
Notes on the important moving parts of this example client application:

 - `com.example.Main`: The entrypoint setting up PowerSync, waiting for a sync to complete and running a simple query.
 - `ExampleBackendConnector.java`: A Java implementation of a backend connector.
    The connector is responsible for obtaining a JWT token by implementing the [fetchCredentials()](/java-example-client/src/main/java/com/example/db/ExampleBackendConnector.java) 
    method. There is also a [uploadData()](/java-example-client/src/main/java/com/example/db/ExampleBackendConnector.java)
    method which connects to a backend to forward local changes that need to be applied to the source backend database (i.e. MongoDB).
    For more details on the role of the backend connector, see [Integrate with your Backend](https://docs.powersync.com/installation/client-side-setup/integrating-with-your-backend) in the PowerSync docs.
   - Make sure to update the following in the `ExampleBackendConnector.java` class:
     - `BACKEND_URL` - This is the URL of the backend API that should receive changes from the client and apply them to 
     the source backend database (i.e. MongoDB)
     - `POWERSYNC_URL` - This is the URL of the PowerSync instance that the client should connect to. It's required as part of
     the `PowerSyncCredentials` class returned by the `fetchCredentials()` method. 
 - `PowerSync.java`: A class which instantiates the `PowerSyncJavaWrapper` and takes in an instance of the `ExampleBackendConnector`. 
 - `AppSchema.java`: A class which defines the [client-side schema](https://docs.powersync.com/installation/client-side-setup/define-your-schema) for the local SQLite database. Update this class
    to define the tables that will be populated in the local SQLite database based on the configured Sync Rules.
 - `com.example.interfaces` and `com.example.wrappers`: This is used to provide a minimal Java interface to the asynchronous Kotlin code as part of the PowerSync client SDK. These wrappers are part of this demo and create a bridge between the PowerSync Kotlin client SDK and the plain Java example client, but we intend to refactor this in the future and move it into our Kotlin SDK.

## Run the Example Client

For the example client app to start successfully, it requires that a PowerSync Service instance is up and running. 
Once the PowerSync Service instance has started and available, simply navigate to this directory and run:
```text
./gradlew run
```
When the example all starts, it will attempt to connect to the PowerSync Service instance to retrieve the JWT and then the sync will start.
