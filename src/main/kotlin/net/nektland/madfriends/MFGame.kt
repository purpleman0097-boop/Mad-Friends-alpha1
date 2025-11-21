package net.nektland.madfriends

import net.nektland.madfriends.states.LobbyState
import net.nektland.madfriends.states.MFGameState
import org.bukkit.Bukkit
import org.bukkit.GameRule
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.bukkit.scoreboard.Team
import java.util.*

class MFGame() {
    companion object {
        private const val MIN_PLAYERS: Int = 2

        private const val SETUP_TIME:   Int = 10 * 1 // 라운드를 준비하는 시간
        private const val IN_GAME_TIME: Int = 10 * 1 // 라운드를 진행하는 시간
        private const val RESULT_TIME:  Int = 10 * 1 // 라운드를 마무리하는 시간
    }

    var totalGameTime: Int = 0 // 로그 기록용 시간. 이것은 0에서 증가한다

    var setupTime:  Int = 0 // 라운드를 준비하는 남은 시간
    var inGameTime: Int = 0 // 라운드를 진행하는 남은 시간
    var resultTime: Int = 0 // 라운드를 마무리하는 남은 시간

    private val mfPlayers: MutableMap<UUID, MFPlayer> = mutableMapOf()
    private var mfGameStatus: MFGameStatus = MFGameStatus.LOBBY

    private var task: BukkitTask? = null

    private var state: MFGameState = LobbyState

    private fun ensureEnoughPlayersOrBackToLobbyState(): Boolean {
        if (!hasEnoughPlayersToStart()) {
            MFUtils.sendConsoleMessage(
                0,
                MFUtils.Type.INFO,
                "최소 인원이 충족되지 않아 로비 상태로 돌아갑니다."
            )
            changeGameState(LobbyState)
            return false
        }
        return true
    }

    fun changeGameState(newState: MFGameState) {
        if (state == newState) return

        state.onExit(this)
        state = newState
        mfGameStatus = state.type
        state.onEnter(this)
    }

    private fun calculateKillerCount(): Int {
        return when (mfPlayers.size) {
            in 2..7   -> 1
            in 8..11  -> 2
            in 12..15 -> 3
            16        -> 4
            else      -> 0
        }
    }

    fun resetPlayerSetting() {
        mfPlayers.values.forEach {
            it.role = MFPlayerRoles.FRIEND
            it.status = MFPlayerStatus.ALIVE
        }
    }

    fun assignRoles() {
        val players = getPlayerList().filter { it.status != MFPlayerStatus.SPECTATOR }
        val killerCount = calculateKillerCount().coerceAtMost(players.size)

        val killers = players.shuffled().take(killerCount)

        players.forEach { it.role = MFPlayerRoles.FRIEND }
        killers.forEach { it.role = MFPlayerRoles.KILLER }

        showRoleAssignmentResult(players)
    }

    fun showRoleAssignmentResult(players: List<MFPlayer>) {
        players.forEach { mfPlayer ->
            val message = when (mfPlayer.role) {
                MFPlayerRoles.FRIEND -> "§a당신은 §f친구§a 입니다. 함께 협력하여 광란자를 찾아내세요!"
                MFPlayerRoles.KILLER -> "§c당신은 §f광란자§c 입니다. 친구들을 속이고 모두 제거하세요!"
                else                 -> return@forEach
            }

            mfPlayer.player.sendMessage(message)
        }
    }

    private fun getPlayerList(): List<MFPlayer> {
        return mfPlayers.values.toList()
    }

    fun getAlivePlayers(): List<MFPlayer> =
        mfPlayers.values.filter { it.status == MFPlayerStatus.ALIVE }

    fun getAliveFriends(): List<MFPlayer> =
        getAlivePlayers().filter { it.role == MFPlayerRoles.FRIEND }

    fun getAliveKillers(): List<MFPlayer> =
        getAlivePlayers().filter { it.role == MFPlayerRoles.KILLER }

    fun hasAlivePlayers(): Boolean =
        mfPlayers.values.any { it.status == MFPlayerStatus.ALIVE }

    fun hasAliveFriends(): Boolean =
        getAlivePlayers().any { it.status == MFPlayerStatus.ALIVE && it.role == MFPlayerRoles.FRIEND }

    fun hasAliveKillers(): Boolean =
        getAlivePlayers().any { it.status == MFPlayerStatus.ALIVE && it.role == MFPlayerRoles.KILLER }

    fun hasEnoughPlayersToStart(): Boolean {
        return getPlayerList().filter { it.player.isOnline } .size >= MIN_PLAYERS
    }


    fun addMFPlayer(player: Player) {
        val uuid: UUID = player.uniqueId
        val existing = mfPlayers[uuid]

        if (existing != null) {
            existing.player = player
            return
        }

        mfPlayers[uuid] = MFPlayer(player, this)
        val mfPlayer = mfPlayers[uuid]

        if (mfPlayer == null) {
            MFUtils.sendConsoleMessage(0, MFUtils.Type.ERROR, "MFPlayer 추가에 실패했습니다: ${player.name} (${player.uniqueId})")
            player.kick()
            return
        }

        MFUtils.sendConsoleMessage(0, MFUtils.Type.INFO, "MFPlayer가 추가되었습니다: ${player.name} (${player.uniqueId})")
        MFUtils.sendConsoleMessage(1, MFUtils.Type.INFO, "${mfPlayer.isMissing}")
    }

    fun removeMFPlayer(player: Player) {
        val uuid: UUID = player.uniqueId
        if (mfPlayers[uuid] == null) return

        mfPlayers.remove(uuid)
    }

    fun removeOfflinePlayers() {
        val iterator = mfPlayers.entries.iterator()
        while (iterator.hasNext()) {
            val entry: MutableMap.MutableEntry<UUID, MFPlayer> = iterator.next()
            val uuid: UUID = entry.key

            if (isOfflinePlayer(uuid)) {
                iterator.remove()
            }
        }
    }

    fun isOfflinePlayer(uuid: UUID): Boolean {
        return Bukkit.getPlayer(uuid) == null
    }

    fun joinTeam(player: Player) {
        val manager = Bukkit.getScoreboardManager() ?: return
        val board   = manager.mainScoreboard

        val friends = board.getTeam("friends") ?: return
        friends.addPlayer(player)
    }

    fun registerTeam() {
        val manager = Bukkit.getScoreboardManager() ?: return
        val board   = manager.mainScoreboard

        val friends = board.getTeam("friends") ?: board.registerNewTeam("friends").apply {
            setCanSeeFriendlyInvisibles(false)
            setAllowFriendlyFire(true)
            setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER)
            setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.NEVER)
            setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.ALWAYS)
        }
    }

    fun unregisterTeam() {
        val manager = Bukkit.getScoreboardManager() ?: return
        val board   = manager.mainScoreboard

        val friends = board.getTeam("friends") ?: return
        friends.unregister()
    }

    fun initializeGame() {
        unregisterTeam()
        registerTeam()

        changeGameState(LobbyState)

        task = object : BukkitRunnable() {
            override fun run() {
                this@MFGame.tick()
            }
        }.runTaskTimer(MadFriends.plugin, 0L, 20L)

        inGameTime = 60 * 4
        totalGameTime = 0
    }

    fun tick() {
        if (state.type == MFGameStatus.SETUP || state.type == MFGameStatus.IN_GAME) {
            if (!ensureEnoughPlayersOrBackToLobbyState()) return
        }

        state.onTick(this)
    }

    fun getMFPlayers(): MutableList<MFPlayer> {
        return mfPlayers.values.toMutableList()
    }

    fun resetSetupTime()     { setupTime     = SETUP_TIME   }
    fun resetInGameTime()    { inGameTime    = IN_GAME_TIME }
    fun resetResultTime()    { resultTime    = RESULT_TIME  }
    fun resetTotalGameTime() { totalGameTime = 0            }

    fun settingGameRules(world: World) {
        world.setGameRule(GameRule.ALLOW_ENTERING_NETHER_USING_PORTALS, false)
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)

        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false)
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)

        world.setGameRule(GameRule.DO_FIRE_TICK, false)

        world.setGameRule(GameRule.DO_WARDEN_SPAWNING, false)
        world.setGameRule(GameRule.DO_TRADER_SPAWNING, false)
        world.setGameRule(GameRule.DO_PATROL_SPAWNING, false)
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false)
        world.setGameRule(GameRule.DO_MOB_LOOT, false)
        world.setGameRule(GameRule.DO_INSOMNIA, false)
        world.setGameRule(GameRule.DISABLE_RAIDS, true)

        world.setGameRule(GameRule.GLOBAL_SOUND_EVENTS, false)

        world.setGameRule(GameRule.DO_TILE_DROPS, false)
        world.setGameRule(GameRule.DO_ENTITY_DROPS, false)

        world.setGameRule(GameRule.LOCATOR_BAR, true)
        world.setGameRule(GameRule.NATURAL_REGENERATION, false)
        world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false)
        world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false)
        world.setGameRule(GameRule.PVP, true)
        world.setGameRule(GameRule.REDUCED_DEBUG_INFO, true)
    }
}