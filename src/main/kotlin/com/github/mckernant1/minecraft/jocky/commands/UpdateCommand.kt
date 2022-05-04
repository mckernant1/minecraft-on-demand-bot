package com.github.mckernant1.minecraft.jocky.commands

import com.github.mckernant1.minecraft.jocky.cfn.cfnClient
import com.github.mckernant1.minecraft.jocky.core.getPublicIp
import com.github.mckernant1.minecraft.jocky.core.waitForCompletion
import com.github.mckernant1.minecraft.jocky.execptions.InvalidCommandException
import com.github.mckernant1.minecraft.jocky.model.ServerConfig
import com.github.mckernant1.minecraft.jocky.singletons.serverTable
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import software.amazon.awssdk.services.cloudformation.model.Capability
import software.amazon.awssdk.services.cloudformation.model.StackStatus

class UpdateCommand(event: MessageReceivedEvent) : AbstractCommand(event) {
    override fun validate(): Unit  {
        validateServerExists()
    }

    override suspend fun execute() {
        val newConfig = promptForServerConfig()

        cfnClient.updateStack {
            it.stackName(newConfig.getStackName())
            it.capabilities(Capability.CAPABILITY_NAMED_IAM)
            it.templateBody(getCloudformationTemplate())
            it.parameters(newConfig.toParameterList())
        }

        event.channel.sendMessage("Server ${newConfig.serverName} is updating...").complete()
        waitForCompletion(newConfig.getStackName(), listOf(StackStatus.UPDATE_COMPLETE), listOf(StackStatus.UPDATE_ROLLBACK_FAILED, StackStatus.ROLLBACK_COMPLETE))
        event.channel.sendMessage("Server ${newConfig.serverName} has updated and has IP: `${getPublicIp(newConfig.getStackName())}:25565`").complete()
        serverTable.putItem(newConfig)
    }
}
