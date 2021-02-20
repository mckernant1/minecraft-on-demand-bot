package com.github.mckernant1.minecraft.jocky.model

import org.slf4j.LoggerFactory
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import software.amazon.awssdk.services.cloudformation.model.Parameter


@DynamoDbBean
class ServerConfig(
    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("discordServerId")
    var discordServerId: String? = null,
    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("serverName")
    var serverName: String? = null,
    var memory: Int = 2048,
    var cpu: Int = 1024,
    var type: String = "vanilla",
    var version: String = "LATEST",
    var ops: String = "",
    var onOffSwitch: Int = 1
) {

    companion object {

        private val logger = LoggerFactory.getLogger(ServerConfig::class.java)

        // Config string should be like key=value,key2=value2
        fun fromString(discordServerId: String, serverName: String, s: String): ServerConfig {
            val propertiesToAdd = s.split(",").map { pair ->
                val split = pair.split("=")
                split[0] to split[1]
            }.toMap()
            return ServerConfig(
                discordServerId,
                serverName,
                memory = propertiesToAdd["memory"]?.toInt() ?: 2048,
                cpu = propertiesToAdd["cpu"]?.toInt() ?: 1024,
                type = propertiesToAdd["type"] ?: "vanilla",
                version = propertiesToAdd["version"] ?: "LATEST",
                ops = propertiesToAdd["ops"] ?: ""
            )
        }
    }

    fun toParameterList(): List<Parameter> = listOf(
        Parameter.builder().parameterKey("Memory").parameterValue(memory.toString()).build(),
        Parameter.builder().parameterKey("CPU").parameterValue(cpu.toString()).build(),
        Parameter.builder().parameterKey("StackName").parameterValue("${serverName}-${discordServerId}").build(),
        Parameter.builder().parameterKey("Type").parameterValue(type).build(),
        Parameter.builder().parameterKey("Version").parameterValue(version).build(),
        Parameter.builder().parameterKey("Ops").parameterValue(ops).build(),
        Parameter.builder().parameterValue("OnOffSwitch").parameterValue(onOffSwitch.toString()).build()
    ).also {
        logger.info("Parameters $it")
    }

    fun getStackName(): String = "${serverName}-${discordServerId}"

    override fun toString(): String =
        "ServerConfig(discordServerId='$discordServerId', serverName='$serverName', memory=$memory, cpu=$cpu, type='$type', version='$version', ops='$ops')"

}
