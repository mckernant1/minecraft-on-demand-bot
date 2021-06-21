package com.github.mckernant1.minecraft.jocky.commands

import com.github.mckernant1.minecraft.jocky.execptions.InvalidCommandException
import com.github.mckernant1.minecraft.jocky.model.ServerConfig
import com.github.mckernant1.minecraft.jocky.singletons.serverTable
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.jvm.Throws

abstract class AbstractCommand(protected val event: MessageReceivedEvent) {

    protected val words = event.message.contentRaw.split("\\s+".toRegex())
    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)

    protected val server by lazy {
        serverTable.getItem(event.guild.id, words[1])
    }

    protected fun validateServerExists() = assert(server != null) { "Server does not exist" }

    protected fun validateServerNotExists() = assert(server == null) { "Server already exists with name '${words[1]}' Do \$list to see existing servers" }

    @Throws(InvalidCommandException::class)
    abstract fun validate()

    abstract suspend fun execute()

    fun getCloudformationTemplate(): String = this::class.java.getResource("/minecraft_on_ecs.yml").readText()

}
