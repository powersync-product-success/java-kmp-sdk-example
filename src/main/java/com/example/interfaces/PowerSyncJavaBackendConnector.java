package com.example.interfaces;

import java.util.concurrent.CompletionStage;

import com.example.wrappers.PowerSyncJavaWrapper;
import com.powersync.connectors.PowerSyncCredentials;
import kotlin.Unit;


public interface PowerSyncJavaBackendConnector {
    CompletionStage<PowerSyncCredentials> fetchCredentials();
    CompletionStage<Unit> uploadData(PowerSyncJavaWrapper database);
}
