package com.github.mckernant1.minecraft.jocky.commands

import com.github.mckernant1.minecraft.jocky.cfn.cfnClient
import com.github.mckernant1.minecraft.jocky.core.waitForCompletion
import com.github.mckernant1.minecraft.jocky.execptions.InvalidCommandException
import com.github.mckernant1.minecraft.jocky.singletons.serverTable
import kotlinx.coroutines.delay
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import software.amazon.awssdk.services.cloudformation.model.StackStatus
import java.time.Duration

class DestroyCommand(event: MessageReceivedEvent) : AbstractCommand(event) {
    override fun validate(): Unit {
        validateServerExists()
    }

    override suspend fun execute() {
        val server = server!!

        event.channel.sendMessage(
            "You are requesting to delete the server. This requires 2 member confirmation\n" +
                    "Another member of the server must type: `\$confirm delete ${server.serverName}`"
        ).complete()
        delay(Duration.ofSeconds(30).toMillis())

        val confirmation = event.channel.history.retrievedHistory.any {
            it.author.id != event.author.id && it.contentRaw == "\$confirm delete ${server.serverName}"
        }

        if (!confirmation) {
            event.channel.sendMessage("You have failed to confirm deletion").complete()
            return
        }
        
        serverTable.deleteItem(server)
        cfnClient.deleteStack {
            it.stackName(server.getStackName())
        }
        event.channel.sendMessage("Server ${server.serverName} is being deleted").complete()
        waitForCompletion(server.getStackName(), listOf(StackStatus.DELETE_COMPLETE), listOf(StackStatus.DELETE_FAILED))
        event.channel.sendMessage("Server ${server.serverName} has been deleted successfully")
    }
}
