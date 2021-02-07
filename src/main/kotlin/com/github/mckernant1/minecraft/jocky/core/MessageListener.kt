package com.github.mckernant1.minecraft.jocky.core

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class MessageListener : ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val words = event.message.contentRaw.split("\\s+".toRegex())
        val command = words[0]
    }

    fun getCommandFromString(command: String) = when (command) {
        else -> null
    }

}
