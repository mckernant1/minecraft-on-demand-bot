package com.github.mckernant1.minecraft.jocky.commands

import com.github.mckernant1.minecraft.jocky.cfn.cfnClient
import com.github.mckernant1.minecraft.jocky.cfn.isFailure
import com.github.mckernant1.minecraft.jocky.cfn.isSuccess
import com.github.mckernant1.minecraft.jocky.ecs.ec2Client
import com.github.mckernant1.minecraft.jocky.ecs.ecsClient
import com.github.mckernant1.minecraft.jocky.model.ServerConfig
import com.github.mckernant1.minecraft.jocky.singletons.serverTable
import kotlinx.coroutines.delay
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import software.amazon.awssdk.services.cloudformation.model.*
import software.amazon.awssdk.services.ecs.model.DescribeTasksRequest

class CreateCommand(event: MessageReceivedEvent) : AbstractCommand(event) {

    override fun validate(): Boolean {
        return runCatching {
            ServerConfig.fromString(event.guild.id, words[1], words.drop(2).joinToString(""))
        }.isSuccess && serverTable.getItem(event.guild.id, words[1]) == null
    }

    override suspend fun execute() {
        val config = ServerConfig.fromString(event.guild.id, words[1], words.drop(2).joinToString(""))
        val stackName = config.getStackName()
        val templateFile = CreateCommand::class.java.getResource("/minecraft_on_ecs.yml").readText()
        val req = CreateStackRequest.builder()
            .stackName(stackName)
            .templateBody(templateFile)
            .parameters(config.toParameterList())
            .capabilities(Capability.CAPABILITY_NAMED_IAM)
            .build()
        cfnClient.createStack(req)
        logger.info("Creation has been started for $stackName")
        val success = waitForCompletion(stackName)
        if (success) {
            val publicIp = getPublicIp(stackName)
            serverTable.putItem(config)
            event.channel.sendMessage("Your minecraft server has been created with ip: `$publicIp:25565`!").complete()
        } else {
            event.channel.sendMessage("Something wrong happened with your Cloudformation stack").complete()
        }
    }


    private suspend fun waitForCompletion(stackName: String): Boolean {
        event.channel.sendMessage("We are now creating your server. Hold on...").complete()
        while (true) {
            logger.info("Waiting for creation to be complete for $stackName")
            val stack = describeStack(stackName)
            if (stack.stackStatus().isSuccess()) {
                return true
            }
            if (stack.stackStatus().isFailure()) {
                return false
            }
            delay(5000)
        }
    }

    private fun describeStack(stackName: String): Stack {
        val describeRequest = DescribeStacksRequest.builder()
            .stackName(stackName)
            .build()
        return cfnClient.describeStacks(describeRequest).stacks().first()
    }

    private fun getPublicIp(stackName: String): String {
        val clusterName = describeStack(stackName).outputs().find { it.outputKey() == "ClusterName" }?.outputValue()
            ?: error("Stack output was not found. Available outputs are ${describeStack(stackName).outputs().map { it.toString() }}")
        val tasksARN = ecsClient.listTasks {
            it.cluster(clusterName)
        }.taskArns().first()
        val describeTaskRequest = DescribeTasksRequest
            .builder()
            .cluster(clusterName)
            .tasks(tasksARN)
            .build()
        val task = ecsClient.describeTasks(describeTaskRequest).tasks().first()
        val networkInterfaceId =
            task.attachments().find { attachment -> attachment.details().any { it.name() == "networkInterfaceId" } }
                ?.details()?.find { it.name() == "networkInterfaceId" }?.value()

        return ec2Client.describeNetworkInterfaces {
            it.networkInterfaceIds(networkInterfaceId)
        }.networkInterfaces().first().association().publicIp()
    }
}
