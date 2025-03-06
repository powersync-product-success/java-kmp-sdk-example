package com.example;

import static com.example.utils.PrintFormatting.formatSyncStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.example.db.TodoList;
import com.example.powersync.PowerSync;
import com.powersync.PowerSyncException;
import com.powersync.db.SqlCursorKt;
import com.powersync.sync.SyncStatus;


public class Main {

    public static void main(String[] args) throws Exception {
        PowerSync powerSync = new PowerSync();

        powerSync.wrapper.waitForFirstSync().get();
        SyncStatus status = powerSync.wrapper.getDatabase().getCurrentStatus();
        System.out.println(formatSyncStatus(status));

        List<TodoList> lists = powerSync.wrapper.getAll("SELECT * FROM lists", Collections.emptyList(),
                sqlCursor -> {
                    try {
                        return new TodoList(SqlCursorKt.getString(sqlCursor, "id"), SqlCursorKt.getString(sqlCursor, "name"),
                                SqlCursorKt.getString(sqlCursor, "created_at"), SqlCursorKt.getString(sqlCursor, "owner_id"));
                    } catch (PowerSyncException e) {
                        throw new RuntimeException(e);
                    }
                }).get();
        System.out.println(lists.toString());
    }
}
