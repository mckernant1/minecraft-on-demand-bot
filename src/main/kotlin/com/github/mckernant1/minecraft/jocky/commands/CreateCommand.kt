package com.github.mckernant1.minecraft.jocky.commands

import com.github.mckernant1.minecraft.jocky.cfn.cfnClient
import com.github.mckernant1.minecraft.jocky.core.getPublicIp
import com.github.mckernant1.minecraft.jocky.core.waitForCompletion
import com.github.mckernant1.minecraft.jocky.model.ServerConfig
import com.github.mckernant1.minecraft.jocky.singletons.serverTable
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import software.amazon.awssdk.services.cloudformation.model.Capability
import software.amazon.awssdk.services.cloudformation.model.CreateStackRequest
import software.amazon.awssdk.services.cloudformation.model.StackStatus

class CreateCommand(event: MessageReceivedEvent) : AbstractCommand(event) {

    override fun validate(): Boolean = runCatching {
        ServerConfig.fromString(event.guild.id, words[1], words.drop(2).joinToString(""))
    }.isSuccess && serverTable.getItem(event.guild.id, words[1]) == null

    override suspend fun execute() {
        val config = ServerConfig.fromString(event.guild.id, words[1], words.drop(2).joinToString(""))
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
        event.channel.sendMessage("We are now creating your server. Hold on...").complete()
        val success = waitForCompletion(stackName, listOf(StackStatus.CREATE_COMPLETE), listOf(StackStatus.ROLLBACK_COMPLETE, StackStatus.ROLLBACK_FAILED))
        if (success) {
            val publicIp = getPublicIp(stackName)
            serverTable.putItem(config)
            event.channel.sendMessage("Your minecraft server has been created with ip: `$publicIp:25565`!").complete()
        } else {
            event.channel.sendMessage("Something wrong happened with your Cloudformation stack").complete()
        }
    }
}
