package com.github.mckernant1.minecraft.jocky.commands

import com.github.mckernant1.minecraft.jocky.cfn.cfnClient
import com.github.mckernant1.minecraft.jocky.core.waitForCompletion
import com.github.mckernant1.minecraft.jocky.singletons.serverTable
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import software.amazon.awssdk.services.cloudformation.model.StackStatus

class DestroyCommand(event: MessageReceivedEvent) : AbstractCommand(event) {
    override fun validate(): Boolean =
        serverTable.getItem(event.guild.id, words[1]) != null


    override suspend fun execute() {
        val server = serverTable.getItem(event.guild.id, words[1])!!

        cfnClient.deleteStack {
            it.stackName(server.getStackName())
        }
        event.channel.sendMessage("Server ${server.serverName} is being deleted").complete()
        waitForCompletion(server.getStackName(), listOf(StackStatus.DELETE_COMPLETE), listOf(StackStatus.DELETE_FAILED))
        event.channel.sendMessage("Server ${server.serverName} has been deleted successfully")
        serverTable.deleteItem(server)
    }
}
