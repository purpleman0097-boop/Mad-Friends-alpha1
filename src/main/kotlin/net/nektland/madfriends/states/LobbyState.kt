package net.nektland.madfriends.states

import net.nektland.madfriends.MFGame
import net.nektland.madfriends.MFGameStatus
import net.nektland.madfriends.MFUtils

object LobbyState : MFGameState {
    override val type = MFGameStatus.LOBBY

    override fun onEnter(mfGame: MFGame) {
        MFUtils.sendConsoleMessage(0, MFUtils.Type.INFO, "게임을 ${type} 상태로 변경했습니다.")
        mfGame.removeOfflinePlayers()
    }

    override fun onExit(mfGame: MFGame) {
        MFUtils.sendConsoleMessage(0, MFUtils.Type.INFO, "게임의 ${type} 상태를 종료합니다.")
    }

    override fun onTick(mfGame: MFGame) {
        if (mfGame.hasEnoughPlayersToStart()) {
            MFUtils.sendConsoleMessage(0, MFUtils.Type.INFO, "최소 인원이 모여 라운드를 시작합니다.")
            mfGame.changeGameState(SetupState)
        }
    }
}