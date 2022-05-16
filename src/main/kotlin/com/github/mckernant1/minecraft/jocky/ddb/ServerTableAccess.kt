package com.github.mckernant1.minecraft.jocky.ddb

import com.github.mckernant1.minecraft.jocky.model.ServerConfig
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional

class ServerTableAccess {
    companion object {
        private val TABLE_NAME = System.getenv("MINECRAFT_SERVERS_TABLE_NAME")
            ?: error("Environment variable 'MINECRAFT_SERVERS_TABLE_NAME' is not defined")
        private val table: DynamoDbTable<ServerConfig> =
            ddbClient.table(TABLE_NAME, TableSchema.fromClass(ServerConfig::class.java))
    }

    fun getItem(discordServerId: String, serverName: String): ServerConfig? =
        table.getItem(Key.builder().partitionValue(discordServerId).sortValue(serverName).build())

    fun putItem(config: ServerConfig) = table.putItem(config)

    fun queryByDiscordServerId(discordServerId: String): List<ServerConfig> {
        val conditional = QueryConditional.keyEqualTo(Key.builder().partitionValue(discordServerId).build())
        return table.query(conditional).items().toList()
    }

    fun deleteItem(config: ServerConfig): ServerConfig = table.deleteItem(config)

}
