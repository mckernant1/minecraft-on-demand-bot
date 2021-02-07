package com.github.mckernant1.minecraft.jocky

import com.github.mckernant1.minecraft.jocky.core.MessageListener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder


fun main() {
    val botToken: String = System.getenv("BOT_TOKEN") ?: error("BOT_TOKEN environment variable required")
    startBot(botToken)
}


fun startBot(token: String): JDA {

    return JDABuilder.createDefault(token)
        .addEventListeners(MessageListener())
        .build()

}
