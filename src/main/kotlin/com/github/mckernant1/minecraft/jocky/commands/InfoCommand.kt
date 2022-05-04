package com.github.mckernant1.minecraft.jocky.commands

import com.github.mckernant1.minecraft.jocky.core.getPublicIp
import com.github.mckernant1.minecraft.jocky.execptions.InvalidCommandException
import com.github.mckernant1.minecraft.jocky.model.CurseForge
import com.github.mckernant1.minecraft.jocky.model.Paper
import com.github.mckernant1.minecraft.jocky.model.ServerConfig
import com.github.mckernant1.minecraft.jocky.singletons.serverTable
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class InfoCommand(event: MessageReceivedEvent) : AbstractCommand(event) {
    override fun validate() {
        if (serverTable.getItem(event.guild.id, words[1]) == null) {
            throw InvalidCommandException("This server does not exist")
        }
    }

    override suspend fun execute() {
        val config = serverTable.getItem(event.guild.id, words[1])
            ?: error("This should never happen")

        event.channel.sendMessage("""
**Info for Server ${config.serverName}**
status: ${if (config.onOffSwitch == 1) "On with IP of `${getPublicIp(config.getStackName())}:25565`" else "Off"}
cpu: ${config.cpu}
memory: ${config.memory}
type: ${config.type}
ops: ${config.getServerSettings().ops}
${getPropsForType(config)}
            """.trimIndent()).complete()

    }

    private fun getPropsForType(config: ServerConfig): String = when (config.getServerSettings()) {
        is Paper -> """
            version: ${(config.getServerSettings() as Paper).version}
        """.trimIndent()
        is CurseForge -> """
            packId: ${(config.getServerSettings() as CurseForge).packId}
            hash: ${(config.getServerSettings() as CurseForge).hash}
        """.trimIndent()
        else -> ""
    }
}
