package com.github.mckernant1.minecraft.jocky.commands

import com.github.mckernant1.minecraft.jocky.cfn.cfnClient
import com.github.mckernant1.minecraft.jocky.core.getPublicIp
import com.github.mckernant1.minecraft.jocky.core.waitForCompletion
import com.github.mckernant1.minecraft.jocky.model.CurseForge
import com.github.mckernant1.minecraft.jocky.model.Paper
import com.github.mckernant1.minecraft.jocky.model.ServerConfig
import com.github.mckernant1.minecraft.jocky.model.ServerType
import com.github.mckernant1.minecraft.jocky.singletons.serverTable
import com.github.mckernant1.minecraft.jocky.util.promptFor
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import software.amazon.awssdk.services.cloudformation.model.Capability
import software.amazon.awssdk.services.cloudformation.model.CreateStackRequest
import software.amazon.awssdk.services.cloudformation.model.StackStatus

class CreateCommand(event: MessageReceivedEvent) : AbstractCommand(event) {

    override fun validate() {
        validateServerNotExists()
    }

    override suspend fun execute() {
        val config = promptForServerConfig()
        val stackName = config.getStackName()
        val templateFile = getCloudformationTemplate()

        val req = CreateStackRequest.builder()
            .stackName(stackName)
            .templateBody(templateFile)
            .parameters(config.toParameterList())
            .capabilities(Capability.CAPABILITY_NAMED_IAM)
            .build()

        cfnClient.createStack(req)
        logger.info("Creation has been started for $stackName")
        event.channel.sendMessage("We are now creating your server ${config.serverName}. Hold on...").complete()
        val success = waitForCompletion(
            stackName,
            listOf(StackStatus.CREATE_COMPLETE),
            listOf(StackStatus.ROLLBACK_COMPLETE, StackStatus.ROLLBACK_FAILED)
        )
        if (success) {
            val publicIp = getPublicIp(stackName)
            serverTable.putItem(config)
            event.channel.sendMessage("Your minecraft server ${config.serverName} has been created with ip: `$publicIp:25565`!")
                .complete()
        } else {
            event.channel.sendMessage("Something wrong happened with your Cloudformation stack for ${config.serverName}")
                .complete()
        }
    }

    private fun promptForServerConfig(): ServerConfig {
        val serverType =
            ServerType.valueOf(event.channel.promptFor("Please enter the server type you want. Options: ${ServerType.values()}"))
        val serverSettings = when (serverType) {
            ServerType.VANILLA -> Paper.createFromPrompts(event.channel)
            ServerType.CURSEFORGE -> CurseForge.createFromPrompts(event.channel)
        }
        val memory =
            event.channel.promptFor("Please enter the memory for this server. Use desired GB * 1024 to get number.")
                .toInt()
        val cpu = event.channel.promptFor("How many Cpus do you want for this server. Options: 1,2,4").toInt()

        return ServerConfig(
            event.guild.id,
            words[1],
            memory,
            cpu,
            serverType,
            serverSettings
        )
    }
}
