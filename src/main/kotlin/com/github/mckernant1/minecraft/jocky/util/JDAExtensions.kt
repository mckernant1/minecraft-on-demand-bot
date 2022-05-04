package com.github.mckernant1.minecraft.jocky.util

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

fun MessageChannel.promptFor(message: String): String = runBlocking {
    this@promptFor.sendMessage(message).complete()
    val selfId = this@promptFor.jda.selfUser.id
    while (this@promptFor.history.retrievedHistory[0].author.id == selfId) {
        delay(500)
    }
    val latestReply = this@promptFor.history.retrievedHistory[0]
    return@runBlocking latestReply.contentRaw
}
