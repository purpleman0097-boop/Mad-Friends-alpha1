package net.nektland.madfriends.commands

import net.nektland.madfriends.MFUtils
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TestMadmanSelect : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        MFUtils.sendConsoleMessage(0, MFUtils.Type.INFO, "TestMadmanSelector 명령어가 실행되었습니다.")
        MFUtils.sendConsoleMessage(1, MFUtils.Type.INFO, "광란자 선택을 테스트합니다...")

        val playerList: List<String> = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P")
        val countMap = mutableMapOf<String, Int>()
        playerList.forEach { countMap[it] = 0 }

        repeat(100) {round ->
            val selected = playerList.shuffled().take(4)

            MFUtils.sendConsoleMessage(1, MFUtils.Type.INFO, "라운드 ${round + 1}, ${selected.joinToString(", ")}")
            selected.forEach {name ->
                countMap[name] = countMap[name]!! + 1
            }

        }

        MFUtils.sendConsoleMessage(0, MFUtils.Type.INFO, "=== 최종 등장 횟수 ===")
        countMap.forEach { (name, count) ->
            MFUtils.sendConsoleMessage(
                1,
                MFUtils.Type.INFO,
                "${name} : ${count}회"
            )
        }
        return false
    }
}