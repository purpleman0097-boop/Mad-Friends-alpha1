package net.nektland.madfriends

import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerJoinEvent

class InventoryClickListener : Listener {


    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        val p = e.whoClicked as? Player ?: return
        p.sendMessage("인벤토리 클릭 감지됨! ${e.slot}")

        if (e.click == ClickType.DOUBLE_CLICK) {
            return
        }

        if (e.slot == 35 && e.click == ClickType.LEFT) {
            p.world.playSound(p.location, Sound.ENTITY_GHAST_HURT, 1.0f, 0.75f)
        }
    }
}