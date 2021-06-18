package com.github.mckernant1.minecraft.jocky.commands

import com.github.mckernant1.minecraft.jocky.cfn.cfnClient
import com.github.mckernant1.minecraft.jocky.core.waitForCompletion
import com.github.mckernant1.minecraft.jocky.execptions.InvalidCommandException
import com.github.mckernant1.minecraft.jocky.singletons.serverTable
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import software.amazon.awssdk.services.cloudformation.model.StackStatus

class DestroyCommand(event: MessageReceivedEvent) : AbstractCommand(event) {
    override fun validate(): Unit {
        if (serverTable.getItem(event.guild.id, words[1]) == null) {
            throw InvalidCommandException("This server doesn't exist")
        }
    }

    override suspend fun execute() {
        val server = serverTable.getItem(event.guild.id, words[1])!!
        serverTable.deleteItem(server)
        cfnClient.deleteStack {
            it.stackName(server.getStackName())
        }
        event.channel.sendMessage("Server ${server.serverName} is being deleted").complete()
        waitForCompletion(server.getStackName(), listOf(StackStatus.DELETE_COMPLETE), listOf(StackStatus.DELETE_FAILED))
        event.channel.sendMessage("Server ${server.serverName} has been deleted successfully")
    }
}
