package com.github.mckernant1.minecraft.jocky.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class HelpCommand(event: MessageReceivedEvent) : AbstractCommand(event) {
    override fun validate() = Unit

    override suspend fun execute() {
        event.channel.sendMessage(message).complete()
    }

    private val message = EmbedBuilder()
        .addField(
            "Commands",
            "`\$help` -> lists this menu\n" +
                    "`\$list` -> lists the servers you have created\n" +
                    "`\$create <serverName> [serverProperties]` -> creates a new server see creation options\n" +
                    "`\$start <serverName>` -> starts a server\n" +
                    "`\$stop <serverName>` -> stops a server\n" +
                    "`\$update <serverName> [serverProperties]` -> updates the config of an existing server\n" +
                    "`\$destroy <serverName>` -> destroys a server and its world",
            false
        )
        .addField(
            "Creation Options",
            "cpu -> 1024 = 1CPU [reference](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/task-cpu-memory-error.html)\n" +
                    "memory -> in MiB [reference](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/task-cpu-memory-error.html)\n" +
                    "type -> default is vanilla [reference](https://github.com/itzg/docker-minecraft-server)\n" +
                    "version -> default is latest [reference](https://github.com/itzg/docker-minecraft-server)\n" +
                    "ftbModpackId -> only for FTB server types [reference](https://github.com/itzg/docker-minecraft-server)\n" +
                    "ftbModPackVersionId -> only for FTB server types [reference](https://github.com/itzg/docker-minecraft-server)\n" +
                    "ops -> a string of bar delimited ops Example ops=TheeAlbinoTree|TheBearPenguin",
            false
        )
        .addField(
            "Creation examples",
            "Should be comma delimited like this NO SPACES: \n" +
                    "`\$create myserver memory=2048,version=1.16.5,cpu=1024`",
            false
        )
        .build()
}
