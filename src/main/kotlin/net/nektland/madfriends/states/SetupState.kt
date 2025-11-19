package net.nektland.madfriends.states

import net.nektland.madfriends.*

object SetupState : MFGameState {
    override val type = MFGameStatus.SETUP

    override fun onEnter(mfGame: MFGame) {
        MFUtils.sendConsoleMessage(0, MFUtils.Type.INFO, "게임을 ${type} 상태로 변경했습니다.")
        mfGame.resetSetupTime()
        mfGame.resetPlayerSetting()
        mfGame.removeOfflinePlayers()
    }

    override fun onExit(mfGame: MFGame) {
        MFUtils.sendConsoleMessage(0, MFUtils.Type.INFO, "게임의 ${type} 상태를 종료합니다.")
    }

    override fun onTick(mfGame: MFGame) {
        mfGame.totalGameTime++

        if (mfGame.setupTime <= 0) {
            mfGame.changeGameState(InGameState)
            mfGame.assignRoles()
        }

        mfGame.setupTime--
    }
}