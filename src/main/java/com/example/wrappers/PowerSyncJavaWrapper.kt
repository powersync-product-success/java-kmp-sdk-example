package com.example.wrappers

import co.touchlab.kermit.Logger
import com.example.powersync.AppSchema
import com.example.interfaces.PowerSyncJavaBackendConnector
import com.powersync.DEFAULT_DB_FILENAME
import com.powersync.DatabaseDriverFactory
import com.powersync.PowerSyncDatabase
import com.powersync.connectors.PowerSyncBackendConnector
import com.powersync.connectors.PowerSyncCredentials
import com.powersync.db.SqlCursor
import com.powersync.db.crud.CrudBatch
import com.powersync.db.crud.CrudTransaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.function.Function

class PowerSyncJavaWrapper(service: ExecutorService) {

    val driverFactory = DatabaseDriverFactory()
    val database: PowerSyncDatabase
    val scope = CoroutineScope(service.asCoroutineDispatcher())

    init {
        database = PowerSyncDatabase(this.driverFactory, AppSchema().schema, DEFAULT_DB_FILENAME, scope, logger);
    }

    fun connect(connector: PowerSyncJavaBackendConnector): CompletableFuture<Unit> = scope.launch {
        database.connect(JavaConnectorBridge(connector), 2000L, 10000L, mapOf())
    }.asCompletableFuture()

    fun disconnect(): CompletableFuture<Unit> = scope.launch {
        database.disconnect()
    }.asCompletableFuture()


    fun execute(query: String, params: List<String>): CompletableFuture<Long> = scope.async { database.execute(query, params) }.asCompletableFuture()

    fun <R: Any> getAll(sql: String, params: List<String>, mapRow: Function<SqlCursor, R>): CompletableFuture<List<R>> {
        return scope.async {
            database.getAll(sql, params) { row -> mapRow.apply(row) }
        }.asCompletableFuture()
    }

    fun getNextCrudTransaction(): CompletableFuture<CrudTransaction?> = scope.async {
        database.getNextCrudTransaction()
    }.asCompletableFuture()

    fun getCrudBatch(limit: Int): CompletableFuture<CrudBatch?> = scope.async {
        database.getCrudBatch(limit)
    }.asCompletableFuture()

    fun waitForFirstSync(): CompletableFuture<Unit> = scope.launch { database.waitForFirstSync() }.asCompletableFuture()

    fun completeTransaction(transaction: CrudBatch, writeCheckpoint: String?): CompletableFuture<Unit> {
        return scope.launch {
            transaction.complete(writeCheckpoint)
        }.asCompletableFuture()
    }

    companion object {
        val logger = Logger.withTag("PowerSync")
    }

    private inner class JavaConnectorBridge(val javaImpl: PowerSyncJavaBackendConnector) : PowerSyncBackendConnector() {
        override suspend fun fetchCredentials(): PowerSyncCredentials? {
            return javaImpl.fetchCredentials().await()
        }

        override suspend fun uploadData(database: PowerSyncDatabase) {
            javaImpl.uploadData(this@PowerSyncJavaWrapper).await();
        }
    }

}