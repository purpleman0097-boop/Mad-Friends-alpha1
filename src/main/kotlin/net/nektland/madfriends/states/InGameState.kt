package net.nektland.madfriends.states

import net.nektland.madfriends.MFGame
import net.nektland.madfriends.MFGameStatus
import net.nektland.madfriends.MFUtils

object InGameState : MFGameState {
    override val type = MFGameStatus.IN_GAME

    override fun onEnter(mfGame: MFGame) {
        MFUtils.sendConsoleMessage(0, MFUtils.Type.INFO, "게임을 ${type} 상태로 변경했습니다.")
        mfGame.resetInGameTime()
    }

    override fun onExit(mfGame: MFGame) {
        MFUtils.sendConsoleMessage(0, MFUtils.Type.INFO, "게임의 ${type} 상태를 종료합니다.")
    }

    override fun onTick(mfGame: MFGame) {
        mfGame.totalGameTime++

        if (mfGame.inGameTime <= 0) {
            mfGame.changeGameState(ResultState)
        }

        mfGame.inGameTime--

        if (!mfGame.hasEnoughPlayersToStart()) {
            MFUtils.sendConsoleMessage(0, MFUtils.Type.INFO, "최소 인원이 충족되지 않아 로비 상태로 돌아갑니다.")
            mfGame.changeGameState(LobbyState)
            return
        }

        if (mfGame.inGameTime <= 0) {
            MFUtils.sendConsoleMessage(0, MFUtils.Type.INFO, "라운드 시간이 종료되었습니다.")
            mfGame.changeGameState(ResultState)
            return
        }

        if (!mfGame.hasAlivePlayers()) {
            MFUtils.sendConsoleMessage(0, MFUtils.Type.INFO, "모든 플레이어가 탈락하여 라운드를 종료합니다.")
            mfGame.changeGameState(ResultState)
            return
        }

        if (!mfGame.hasAliveFriends()) {
            MFUtils.sendConsoleMessage(0, MFUtils.Type.INFO, "모든 친구들이 탈락하여 라운드를 종료합니다.")
            mfGame.changeGameState(ResultState)
            return
        }

        if (!mfGame.hasAliveKillers()) {
            MFUtils.sendConsoleMessage(0, MFUtils.Type.INFO, "모든 광란자들이 탈락하여 라운드를 종료합니다.")
            mfGame.changeGameState(ResultState)
            return
        }
    }
}