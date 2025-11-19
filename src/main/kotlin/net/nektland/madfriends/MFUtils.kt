package net.nektland.madfriends

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit

object MFUtils {
    enum class Type {
        INFO,
        WARN,
        ERROR,
        SUCCESS
    }
    private const val INDENT_SIZE = 4
    private val mm = MiniMessage.miniMessage()

    fun sendConsoleMessage(tab: Int, type: Type, message: String) {
        val indent = "·".repeat(tab * INDENT_SIZE)

        val colored = when (type) {
            Type.INFO -> "<blue>$message</blue>"
            Type.WARN -> "<yellow>$message</yellow>"
            Type.ERROR -> "<red>$message</red>"
            Type.SUCCESS -> "<green>$message</green>"
        }

        if (tab <= 0) {
            Bukkit.getConsoleSender().sendMessage(mm.deserialize(colored))
            return
        }

        val serialized = "<dark_gray>$indent</dark_gray><reset>$colored"
        Bukkit.getConsoleSender().sendMessage(mm.deserialize(serialized))
    }

    private fun component(string: String): Component {
        return mm.deserialize(string)
    }
}