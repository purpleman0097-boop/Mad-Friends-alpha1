package net.nektland.madfriends.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Pose

class TestCorpseCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val p = sender as? org.bukkit.entity.Player ?: return false

        p.sendMessage("씨발놈아!")

        val corpse = p.world.spawnEntity(p.location.add(p.location.direction.multiply(2)), EntityType.MANNEQUIN) as org.bukkit.entity.Mannequin

        corpse.pose = Pose.SLEEPING
        corpse.setGravity(false)


        // pitch, yaw 각도를 조정하여 눕는 방향 설정
        corpse.teleport(corpse.location.apply {
            pitch = 0f
            yaw = p.location.yaw + 180f
        })
        corpse.isCollidable = false

        for (px in Bukkit.getOnlinePlayers()) {
            // 본인만 빼고 서버에 있는 모든 플레이어에게 갱신
            if (px != p) {
                px.canSee(corpse)
            }


        }

        return true
    }
}