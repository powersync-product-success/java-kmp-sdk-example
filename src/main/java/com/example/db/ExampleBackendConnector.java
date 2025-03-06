package com.example.db;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.prefs.Preferences;

import com.example.interfaces.PowerSyncJavaBackendConnector;
import com.example.wrappers.PowerSyncJavaWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.powersync.connectors.PowerSyncCredentials;
import com.powersync.db.crud.CrudBatch;
import kotlin.Unit;
import okhttp3.*;
import org.json.JSONObject;


public class ExampleBackendConnector implements PowerSyncJavaBackendConnector {
    // This is currently a hardcoded user ID, you should update this and retrieve
    // the user ID from a session.
    private static final String USER_ID_STORAGE_KEY = "ps_user_id";
    // Update this URL and point it to your backend.
    private static final String BACKEND_URL = "http://localhost:6060";
    // Update this URL and point the app to your PowerSync instance.
    private static final String POWERSYNC_URL = "http://localhost:8080";

    private final ExecutorService executors;
    private OkHttpClient client = new OkHttpClient();
    private final String userId;

    public ExampleBackendConnector(ExecutorService executors) {
        this.executors = executors;
        Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
        String storedUserId = prefs.get(USER_ID_STORAGE_KEY, null);

        if (storedUserId == null) {
            storedUserId = UUID.randomUUID().toString();
            prefs.put(USER_ID_STORAGE_KEY, storedUserId);
        }

        this.userId = storedUserId;
    }

    @Override
    public CompletionStage<PowerSyncCredentials> fetchCredentials() {
        String tokenEndpoint = "api/auth/token";
        String url = BACKEND_URL + "/" + tokenEndpoint + "?user_id=" + userId;
        System.out.println("Expected URL: " + url);

        return CompletableFuture.supplyAsync(() -> {
            try {
                Request request = new Request.Builder()
                        .url(url)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    JSONObject body = new JSONObject(response.body().string());
                    return new PowerSyncCredentials(POWERSYNC_URL, body.getString("token"), userId);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executors);
    }

    @Override
    public CompletionStage<Unit> uploadData(PowerSyncJavaWrapper database) {
        return database.getCrudBatch(100).thenCompose(transaction -> {
            System.out.println("Uploading data to PowerSync...");
            assert !transaction.getHasMore(): "TODO: Larger transactions";
            return processBatch(transaction).thenCompose(_c -> database.completeTransaction(transaction, null));
        });
    }

    private CompletableFuture<Void> processBatch(CrudBatch batch) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody;
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("batch", batch.getCrud());
            jsonBody = objectMapper.writeValueAsString(body);
            System.out.println("Sending data: " + jsonBody);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json"));
                Request request = new Request.Builder()
                        .url(BACKEND_URL + "/api/data")
                        .post(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.code() != 200) {
                        throw new RuntimeException("Received " + response.code() + " from /upload: " + response.body());
                    }
                    return null;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, executors);
    }
}
