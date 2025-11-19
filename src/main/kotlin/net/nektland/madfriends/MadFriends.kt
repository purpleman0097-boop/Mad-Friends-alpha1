package net.nektland.madfriends

import net.megavex.scoreboardlibrary.api.ScoreboardLibrary
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException
import net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary
import net.nektland.madfriends.commands.TestCorpseCommand
import net.nektland.madfriends.commands.TestMadmanSelect
import net.nektland.madfriends.commands.TestSpawnBlood
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin


class MadFriends : JavaPlugin() {
    companion object {
        lateinit var plugin: MadFriends
        lateinit var scoreboardLibrary: ScoreboardLibrary
        lateinit var mfGame: MFGame
    }

    override fun onEnable() {
        MFUtils.sendConsoleMessage(0, MFUtils.Type.INFO, "MadFriends를 활성화하는 중입니다...")
        plugin = this

        try {
            scoreboardLibrary = ScoreboardLibrary.loadScoreboardLibrary(plugin)
        } catch (e: NoPacketAdapterAvailableException) {
            scoreboardLibrary = NoopScoreboardLibrary()
            plugin.logger.warning("No scoreboard packet adapter available!")
        }

        MFUtils.sendConsoleMessage(1, MFUtils.Type.INFO, "이벤트 리스너를 등록하는 중...")
        Bukkit.getPluginManager().registerEvents(InventoryClickListener(), this)
        Bukkit.getPluginManager().registerEvents(PlayerListener(), this)

        MFUtils.sendConsoleMessage(1, MFUtils.Type.INFO, "명령어를 등록하는 중...")
        getCommand("testCorpse")?.setExecutor(TestCorpseCommand())
        getCommand("testSpawnBlood")?.setExecutor(TestSpawnBlood())
        getCommand("TestMadmanSelect")?.setExecutor(TestMadmanSelect())

        MFUtils.sendConsoleMessage(1, MFUtils.Type.SUCCESS, "MadFriends가 활성화되었습니다!")

        mfGame = MFGame()
        mfGame.initializeGame()
    }

    override fun onDisable() {
        // Plugin shutdown logic
        scoreboardLibrary.close()
        logger.info("MadFriends has been disabled!")
    }


}