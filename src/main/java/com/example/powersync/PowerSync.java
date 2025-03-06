package com.example.powersync;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.db.ExampleBackendConnector;
import com.example.wrappers.PowerSyncJavaWrapper;

/**
 * The PowerSync class is responsible for initializing and managing the connection
 * to the PowerSync service using a MongoDB connector and an executor service.
 *
 * <p>
 *     We use coroutines in our Kotlin SDK to integrate with async Android / iOS APIs efficiently.
 *     Coroutines use a method signatures/ABI that can't be called from Java directly.
 *     But we can transform coroutines into `CompletableFuture`s in Java, which can be a awaited with a blocking `.get()` call.
 *     We will also offer more utilities around this in a Java wrapper as part of our SDK, allowing blocking code to be used in the PowerSync connector for Java.
 * </p>
 */
public class PowerSync {
    /**
     * The wrapper instance used to interact with the PowerSync service.
     */
    public final PowerSyncJavaWrapper wrapper;

    /**
     * Constructs a new PowerSync instance, initializes the MongoDB connector,
     * executor service, and connects to the PowerSync service.
     */
    public PowerSync() {
        ExecutorService executor = Executors.newCachedThreadPool();
        ExampleBackendConnector connector = new ExampleBackendConnector(executor);
        wrapper = new PowerSyncJavaWrapper(executor);

        try {
            wrapper.connect(connector).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}