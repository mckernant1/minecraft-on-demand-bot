package com.github.mckernant1.minecraft.jocky.core

import com.github.mckernant1.minecraft.jocky.commands.AbstractCommand
import com.github.mckernant1.minecraft.jocky.commands.CreateCommand
import com.github.mckernant1.minecraft.jocky.commands.DestroyCommand
import com.github.mckernant1.minecraft.jocky.commands.ExamplesCommand
import com.github.mckernant1.minecraft.jocky.commands.HelpCommand
import com.github.mckernant1.minecraft.jocky.commands.InfoCommand
import com.github.mckernant1.minecraft.jocky.commands.ListCommand
import com.github.mckernant1.minecraft.jocky.commands.StartCommand
import com.github.mckernant1.minecraft.jocky.commands.StopCommand
import com.github.mckernant1.minecraft.jocky.commands.UpdateCommand
import kotlin.concurrent.thread
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.LoggerFactory

class MessageListener : ListenerAdapter() {

    companion object {
        private val logger = LoggerFactory.getLogger(MessageListener::class.java)
    }

    override fun onMessageReceived(event: MessageReceivedEvent) {
        val words = event.message.contentRaw.split("\\s+".toRegex())
        val command = getCommandFromString(words[0], event) ?: return

        if (event.author.id == "164148012019482625") {
            event.channel.sendMessage("Fahk you").complete()
            return
        }

        event.channel.sendTyping().complete()
        thread {
            try {
                command.validate()
            } catch (e: Exception) {
                logger.info("Hit invalid command", e)
                event.channel.sendMessage("Your command was invalid message: ${e.message}").complete()
                return@thread
            }
            runBlocking {
                try {
                    command.execute()
                } catch (e: Exception) {
                    logger.info("Hit Error: ", e)
                    event.channel.sendMessage("Your command experienced an internal failure: ${e.message}").complete()
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
