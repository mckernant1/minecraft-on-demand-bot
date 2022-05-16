package com.github.mckernant1.minecraft.jocky.model

import com.github.mckernant1.minecraft.jocky.util.promptFor
import net.dv8tion.jda.api.entities.MessageChannel


interface Server {
    val ops: List<String>
}

data class Paper(
    override val ops: List<String>,
    val version: String
) : Server {
    companion object {
        fun createFromPrompts(channel: MessageChannel): Paper {
            val ops = channel.promptFor("Please list out the ops you want on this server. Example: TheeAlbinoTree TheBearPenguin").split(" ")
            val version = channel.promptFor("Please specify the version you want to play. Example: 1.18.2")
            return Paper(ops, version)
        }
    }
}

data class CurseForge(
    override val ops: List<String>,
    val packId: Int,
    val hash: String
) : Server {
    companion object {
        fun createFromPrompts(channel: MessageChannel): CurseForge {
            val ops = channel.promptFor("Please list out the ops you want on this server. Example: TheeAlbinoTree TheBearPenguin").split(" ")
            val packId = channel.promptFor("Please specify the packId you want to play. For example the packId of https://www.curseforge.com/minecraft/modpacks/rlcraft is 285109").toInt()
            val hash = channel.promptFor("Please paste the hash of the MAIN FILE on the files page. For example https://www.curseforge.com/minecraft/modpacks/rlcraft/files is 'd04f9ce4638344b3366ce1e6f524b1d6'")
            return CurseForge(ops, packId, hash)
        }
    }
}
