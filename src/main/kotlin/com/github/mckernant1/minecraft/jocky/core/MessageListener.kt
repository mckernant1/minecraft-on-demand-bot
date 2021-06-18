package com.github.mckernant1.minecraft.jocky.core

import com.github.mckernant1.minecraft.jocky.commands.*
import com.github.mckernant1.minecraft.jocky.execptions.InvalidCommandException
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import kotlin.concurrent.thread

class MessageListener : ListenerAdapter() {

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val words = event.message.contentRaw.split("\\s+".toRegex())
        val command = getCommandFromString(words[0], event) ?: return
        event.channel.sendTyping().complete()
        thread {
            try {
                command.validate()
            } catch (e: Exception) {
                event.channel.sendMessage("Your command was invalid message: ${e.message}").complete()
                return@thread
            }
            runBlocking {
                try {
                    command.execute()
                } catch (e: Exception) {
                    event.channel.sendMessage("Your command experienced an internal failure: $e")
                }
            }
        }
    }

    private fun getCommandFromString(command: String, event: MessageReceivedEvent): AbstractCommand? = when (command) {
        "\$create" -> CreateCommand(event)
        "\$list" -> ListCommand(event)
        "\$start" -> StartCommand(event)
        "\$stop" -> StopCommand(event)
        "\$destroy" -> DestroyCommand(event)
        "\$update" -> UpdateCommand(event)
        "\$help" -> HelpCommand(event)
        "\$examples" -> ExamplesCommand(event)
        "\$info" -> InfoCommand(event)
        else -> null
    }

}
