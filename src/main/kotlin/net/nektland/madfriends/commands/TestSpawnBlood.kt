package net.nektland.madfriends.commands

import io.papermc.paper.command.brigadier.argument.ArgumentTypes.player
import net.kyori.adventure.text.Component
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar
import net.nektland.madfriends.Blood
import net.nektland.madfriends.MadFriends.Companion.scoreboardLibrary
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player


class TestSpawnBlood : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val p = sender as? Player ?: return false

        Blood().spawnBlood(p)

        // ray의 pitch을 땅으로 조정하여 땅에 닿을 때 까지 계산

        val sidebar: Sidebar = scoreboardLibrary.createSidebar()

        sidebar.title(Component.text("Sidebar Title"))
        sidebar.line(0, Component.empty())
        sidebar.line(1, Component.text("Line 1"))
        sidebar.line(2, Component.text("Line 2"))
        sidebar.line(2, Component.empty())
        sidebar.line(3, Component.text("epicserver.net"))

        sidebar.addPlayer(p) // Add the player to the sidebar


// Don't forget to call sidebar.close() once you don't need it anymore!

        return false
    }
}