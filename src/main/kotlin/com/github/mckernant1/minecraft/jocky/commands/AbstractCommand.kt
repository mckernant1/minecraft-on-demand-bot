package com.github.mckernant1.minecraft.jocky.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class AbstractCommand(val event: MessageReceivedEvent) {

    protected val words = event.message.contentRaw.split("\\s+".toRegex())
    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)

    abstract fun validate(): Boolean

    abstract suspend fun execute()

    fun getCloudformationTemplate(): String = this::class.java.getResource("/minecraft_on_ecs.yml").readText()

}
