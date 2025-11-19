package net.nektland.madfriends.states

import net.nektland.madfriends.MFGame
import net.nektland.madfriends.MFGameStatus
import net.nektland.madfriends.MFUtils

object ResultState : MFGameState {
    override val type = MFGameStatus.RESULT

    override fun onEnter(mfGame: MFGame) {
        MFUtils.sendConsoleMessage(0, MFUtils.Type.INFO, "게임을 ${type} 상태로 변경했습니다.")
        mfGame.resetResultTime()
        mfGame.resetTotalGameTime()
    }

    override fun onExit(mfGame: MFGame) {
        MFUtils.sendConsoleMessage(0, MFUtils.Type.INFO, "게임의 ${type} 상태를 종료합니다.")
    }

    override fun onTick(mfGame: MFGame) {
        if (mfGame.resultTime <= 0) {
            if (!mfGame.hasEnoughPlayersToStart()) {
                MFUtils.sendConsoleMessage(0, MFUtils.Type.INFO, "최소 인원이 충족되지 않아 로비 상태로 돌아갑니다.")
                mfGame.changeGameState(LobbyState)
                return
            }
            mfGame.changeGameState(SetupState)
        }
        mfGame.resultTime--
    }
}