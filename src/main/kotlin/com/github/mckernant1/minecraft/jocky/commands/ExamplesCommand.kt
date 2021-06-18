package com.github.mckernant1.minecraft.jocky.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class ExamplesCommand(event: MessageReceivedEvent) : AbstractCommand(event) {
    override fun validate() = Unit

    override suspend fun execute() {
        event.channel.sendMessage(
            "**Create a Vanilla Server**\n" +
                    "\$create vanillla memory=4096,cpu=2048,version=1.17,ops=TheeAlbinoTree|TheBearPenguin" +
                    "\n\n" +
                    "**Create a FTB Server **\n" +
                    "for pack https://www.feed-the-beast.com/modpack/ftb_endeavour" +
                    "\n" +
                    "\$create ftbEndev memory=8192,cpu=2048,type=FTBA,ftbModpackId=80,ftbModPackVersionId=2053,ops=TheeAlbinoTree" +
                    "\n\n"
        ).complete()
    }
}
