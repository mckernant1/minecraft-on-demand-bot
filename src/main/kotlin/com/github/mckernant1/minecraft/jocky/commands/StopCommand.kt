package com.github.mckernant1.minecraft.jocky.commands

import com.github.mckernant1.minecraft.jocky.cfn.cfnClient
import com.github.mckernant1.minecraft.jocky.core.waitForCompletion
import com.github.mckernant1.minecraft.jocky.execptions.InvalidCommandException
import com.github.mckernant1.minecraft.jocky.singletons.serverTable
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import software.amazon.awssdk.services.cloudformation.model.Capability
import software.amazon.awssdk.services.cloudformation.model.StackStatus

class StopCommand(event: MessageReceivedEvent) : AbstractCommand(event) {

    override fun validate(): Unit {
        if(serverTable.getItem(event.guild.id, words[1])?.onOffSwitch != 1) {
            throw InvalidCommandException("This server is already stopped")
        }
    }

    override suspend fun execute() {
        val server = serverTable.getItem(event.guild.id, words[1])!!
        server.onOffSwitch = 0
        cfnClient.updateStack {
            it.stackName(server.getStackName())
            it.capabilities(Capability.CAPABILITY_NAMED_IAM)
            it.templateBody(getCloudformationTemplate())
            it.parameters(server.toParameterList())
        }
        event.channel.sendMessage("Server ${server.serverName} is shutting down").complete()
        waitForCompletion(server.getStackName(), listOf(StackStatus.UPDATE_COMPLETE), listOf(StackStatus.UPDATE_ROLLBACK_FAILED, StackStatus.ROLLBACK_COMPLETE))
        event.channel.sendMessage("Server ${server.serverName} has shut down successfully").complete()
        serverTable.putItem(server)
    }
}
