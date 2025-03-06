package com.example.powersync;

import java.util.Arrays;
import java.util.Collections;

import com.powersync.db.schema.Column;
import com.powersync.db.schema.ColumnType;
import com.powersync.db.schema.Index;
import com.powersync.db.schema.IndexedColumn;
import com.powersync.db.schema.Schema;
import com.powersync.db.schema.Table;

/**
 * Add your specific object definitions for each table you want to replicate to the local database.
 * This example includes a lists table as an example.
 */
public class AppSchema {
    public Schema schema;
    public AppSchema() {
        this.schema = new Schema(
            Collections.singletonList(
                    new Table(
                            "lists",
                            Arrays.asList(
                                    new Column("name", ColumnType.TEXT),
                                    new Column("created_at", ColumnType.TEXT),
                                    new Column("owner_id", ColumnType.TEXT)
                            ),
                            Collections.singletonList(new Index("name", IndexedColumn.Companion.descending("name"))),
                            false, false, null
                    )
            )
        );
    }
}
