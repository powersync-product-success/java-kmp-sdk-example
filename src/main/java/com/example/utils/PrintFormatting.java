package com.example.utils;

import com.powersync.sync.SyncStatus;

public class PrintFormatting {
    public static String formatSyncStatus(SyncStatus status) {
        return String.format(
                "-------------------------\n" +
                        "SyncStatus:\n" +
                        "\tConnected: %s\n" +
                        "\tConnecting: %s\n" +
                        "\tDownloading: %s\n" +
                        "\tUploading: %s\n" +
                        "\tLast Synced At: %s\n" +
                        "\tHas Synced: %s\n" +
                        "\tAny Error: %s\n" +
                        "-------------------------",
                status.getConnected(), status.getConnecting(), status.getDownloading(),
                status.getUploading(), status.getLastSyncedAt(), status.getHasSynced(),
                status.getAnyError());
    }
}
