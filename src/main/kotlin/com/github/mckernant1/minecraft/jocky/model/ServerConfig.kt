package com.github.mckernant1.minecraft.jocky.model

import com.google.gson.Gson
import org.slf4j.LoggerFactory
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.EnumAttributeConverter
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import software.amazon.awssdk.services.cloudformation.model.Parameter


@DynamoDbImmutable(builder = ServerConfig.Builder::class)
class ServerConfig(
    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("discordServerId")
    val discordServerId: String,
    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("serverName")
    val serverName: String,
    var memory: Int,
    var cpu: Int,
    @get:DynamoDbConvertedBy(EnumAttributeConverter::class)
    val type: ServerType,
    private val serverSettings: String,
    var onOffSwitch: Int
) {

    constructor(
        discordServerId: String,
        serverName: String,
        memory: Int,
        cpu: Int,
        type: ServerType,
        serverSettings: Server,
    ) : this(discordServerId, serverName, memory, cpu, type, gson.toJson(serverSettings), 1)

    companion object {
        private val logger = LoggerFactory.getLogger(ServerConfig::class.java)
        private val gson = Gson()
    }

    fun toParameterList(): List<Parameter> = listOf(
        Parameter.builder().parameterKey("Memory").parameterValue(memory.toString()).build(),
        Parameter.builder().parameterKey("CPU").parameterValue(cpu.toString()).build(),
        Parameter.builder().parameterKey("StackName").parameterValue(this.getStackName()).build(),
        Parameter.builder().parameterKey("Type").parameterValue(type.toString()).build(),
        Parameter.builder().parameterKey("ServerJson").parameterValue(serverSettings).build(),
        Parameter.builder().parameterKey("OnOffSwitch").parameterValue(onOffSwitch.toString()).build()
    ).also {
        logger.info("Parameters $it")
    }

    fun getStackName(): String = "${serverName}-${discordServerId}"

    override fun toString(): String = gson.toJson(this)

    class Builder {
        private var discordServerId: String? = null
        fun discordServerId(value: String) = apply { discordServerId = value }

        private var serverName: String? = null
        fun serverName(value: String) = apply { serverName = value }

        private var memory: Int? = null
        fun memory(value: Int) = apply { memory = value }

        private var cpu: Int? = null
        fun cpu(value: Int) = apply { cpu = value }

        private var type: ServerType? = null
        fun type(value: ServerType) = apply { type = value }

        private var serverSettings: String? = null
        fun serverSettings(value: String) = apply { serverSettings = value }

        private var onOffSwitch: Int? = null
        fun onOffSwitch(value: Int) = apply { onOffSwitch = value }

        fun build() = ServerConfig(
            discordServerId!!,
            serverName!!,
            memory!!,
            cpu!!,
            type!!,
            serverSettings!!,
            onOffSwitch!!
        )

    }
}
