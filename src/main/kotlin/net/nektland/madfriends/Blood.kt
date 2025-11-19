package net.nektland.madfriends

import org.bukkit.FluidCollisionMode
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.entity.BlockDisplay
import org.bukkit.entity.Player
import org.bukkit.util.RayTraceResult
import org.bukkit.util.Transformation
import org.bukkit.util.Vector
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.random.Random

class Blood {

    private fun rayTraceDown(p: Player, maxDistance: Double = 256.0): RayTraceResult? {
        val w = p.world
        val start = p.eyeLocation
        val dir = Vector(0.0, -1.0, 0.0)
        return w.rayTraceBlocks(start, dir, maxDistance, FluidCollisionMode.NEVER, true)
    }

    fun spawnBlood(p: Player) {
        val playerLocation = p.eyeLocation
        val playerWorld = playerLocation.world
        val blood = playerWorld.spawn(playerLocation, BlockDisplay::class.java)

        val rayResult = rayTraceDown(p) ?: return
        val sl = rayResult.hitPosition.toLocation(playerWorld)

        val bdt: BlockData = Material.RED_CONCRETE.createBlockData()
        blood.block = bdt

        val randomYaw = (0..359).random().toFloat()

        val minWidth = 0.1f
        val maxWidth = 0.25f

        val height = listOf(0.03125f, 0.0625f).random()

        val widthSize = minWidth + Random.nextFloat() * (maxWidth - minWidth)

        blood.transformation = Transformation(
            Vector3f(0f, 0f, 0f),
            Quaternionf(),
            Vector3f(widthSize, height, widthSize),
            Quaternionf()
        )

        blood.teleport(sl.apply {
            yaw = randomYaw
            pitch = 0f
        })
    }
}