package net.nektland.madfriends

import org.bukkit.entity.Player
import java.util.UUID

enum class MFPlayerStatus { ALIVE, DEAD, MISSING, SPECTATOR }
enum class MFPlayerRoles { FRIEND, KILLER }

class MFPlayer(
    var player: Player,
    val mfGame: MFGame
) {
    val uuid: UUID = player.uniqueId
    val playerName: String = player.name

    var aliasName: String? = null
    var status: MFPlayerStatus = MFPlayerStatus.SPECTATOR
    var role: MFPlayerRoles? = null

    var gold:    Int = 0
    var emerald: Int = 0

    var exp:   Int = 0
    var level: Int = 0

    var isDead:    Boolean = false
    var isMissing: Boolean = false
    var isOnline:  Boolean = false

    fun initialization() {
        aliasName = null
        status    = MFPlayerStatus.SPECTATOR
        role      = null

        gold    = 0
        emerald = 0

        exp   = 0
        level = 0

        isDead    = false
        isMissing = false
        isOnline  = true
    }
}