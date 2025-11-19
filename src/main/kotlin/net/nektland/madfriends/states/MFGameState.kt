package net.nektland.madfriends.states

import net.nektland.madfriends.MFGame
import net.nektland.madfriends.MFGameStatus

interface MFGameState {
    val type: MFGameStatus

    fun onEnter(mfGame: MFGame) {}
    fun onExit(mfGame: MFGame)  {}
    fun onTick(mfGame: MFGame)  {}
}