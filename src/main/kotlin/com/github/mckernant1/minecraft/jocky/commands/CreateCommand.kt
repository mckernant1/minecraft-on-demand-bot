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
import kotlinx.coroutines.delay
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import software.amazon.awssdk.services.cloudformation.model.Capability
import software.amazon.awssdk.services.cloudformation.model.CreateStackRequest
import software.amazon.awssdk.services.cloudformation.model.StackStatus
import java.time.Duration

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
            delay(Duration.ofSeconds(30).toMillis())
            val publicIp = getPublicIp(stackName)
            serverTable.putItem(config)
            event.channel.sendMessage("Your minecraft server ${config.serverName} has been created with ip: `$publicIp:25565`!")
                .complete()
        } else {
            event.channel.sendMessage("Something wrong happened with your Cloudformation stack for ${config.serverName}")
                .complete()
        }
    }
}
