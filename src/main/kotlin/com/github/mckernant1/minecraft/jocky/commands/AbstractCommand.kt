package com.github.mckernant1.minecraft.jocky.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

abstract class AbstractCommand(val event: MessageReceivedEvent) {

    abstract fun validate(): Boolean

    abstract fun execute()

}
