package com.github.mckernant1.minecraft.jocky.core

import com.github.mckernant1.minecraft.jocky.cfn.cfnClient
import com.github.mckernant1.minecraft.jocky.ecs.ec2Client
import com.github.mckernant1.minecraft.jocky.ecs.ecsClient
import kotlinx.coroutines.delay
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksRequest
import software.amazon.awssdk.services.cloudformation.model.Stack
import software.amazon.awssdk.services.cloudformation.model.StackStatus
import software.amazon.awssdk.services.ecs.model.DescribeTasksRequest


private val logger: Logger = LoggerFactory.getLogger("CommonUtils")

fun getPublicIp(stackName: String): String {

    val clusterName = describeStack(stackName).outputs().find { it.outputKey() == "ClusterName" }?.outputValue()
        ?: error(
            "Stack output was not found. Available outputs are ${
                describeStack(stackName).outputs().map { it.toString() }
            }"
        )
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

fun describeStack(stackName: String): Stack {
    if (cfnClient.listStacks()
        .stackSummaries()
        .none { it.stackName() == stackName }) {
        error("Stack is in the DynamoDB Table but not in cloudformation")
    }

    val describeRequest = DescribeStacksRequest.builder()
        .stackName(stackName)
        .build()
    return cfnClient.describeStacks(describeRequest).stacks().first()
}

suspend fun waitForCompletion(stackName: String, successStatus: List<StackStatus>, failedStatus: List<StackStatus>): Boolean {
    var timesToTry = 0
    val limit = 100
    while (true) {
        logger.info("Waiting for cloudformation action on $stackName ($timesToTry/$limit)")
        val stack = describeStack(stackName)
        if (successStatus.contains(stack.stackStatus())) {
            logger.info("${stack.stackStatus()} $stackName")
            return true
        }
        if (failedStatus.contains(stack.stackStatus())) {
            cfnClient.deleteStack {
                it.stackName(stackName)
            }
            logger.info("Stack failed to create $stackName: ${stack.stackStatus()}")
            logger.info("Reason $stack.")
            return false
        }
        if (timesToTry >= limit) {
            error("Something went wrong stabilizing the CFN stack")
        }
        delay(20000)
        timesToTry += 1
    }
}
