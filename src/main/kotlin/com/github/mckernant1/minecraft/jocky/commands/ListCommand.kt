package com.github.mckernant1.minecraft.jocky.commands

import com.github.mckernant1.minecraft.jocky.core.getPublicIp
import com.github.mckernant1.minecraft.jocky.singletons.serverTable
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class ListCommand(event: MessageReceivedEvent) : AbstractCommand(event) {
    override fun validate() = Unit

    override suspend fun execute() {
        val servers = serverTable.queryByDiscordServerId(event.guild.id).ifEmpty {
            event.channel.sendMessage("There are no servers listed").complete()
            return
        }

        val serverStrings = servers.joinToString("\n") {
            "${it.serverName} is currently ${if (it.onOffSwitch == 1) "On with IP of ${getPublicIp(it.getStackName())}" else "Off"}"
        }

        event.channel.sendMessage("Servers:\n$serverStrings").complete()
    }
}
