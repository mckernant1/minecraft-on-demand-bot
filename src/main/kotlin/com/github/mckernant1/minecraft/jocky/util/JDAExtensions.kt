package com.github.mckernant1.minecraft.jocky.util

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.entities.MessageChannel
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant


private val logger = LoggerFactory.getLogger("PromptForLogger")

fun MessageChannel.promptFor(message: String): String = runBlocking {
    this@promptFor.sendMessage(message).complete()
    val selfId = this@promptFor.jda.selfUser.id
    val startTime = Instant.now()
    while (this@promptFor.history.retrievePast(1).complete().first().author.id == selfId) {
        delay(500)
        if (startTime < Instant.now() - Duration.ofSeconds(30)) {
            throw RuntimeException("There was no message received for 30 seconds... quitting")
        }
    }

    val latestReply = this@promptFor.history.retrievePast(1).complete().first()
    return@runBlocking latestReply.contentRaw
}
