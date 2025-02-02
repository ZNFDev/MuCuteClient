package com.mucheng.mucute.client.game.module.misc

import com.mucheng.mucute.client.game.Module
import com.mucheng.mucute.client.game.ModuleCategory
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket
import kotlin.math.cos
import kotlin.math.sin

class ClipModule : Module("clip", ModuleCategory.Misc) {

    override fun onReceived(packet: BedrockPacket): Boolean {
        val player = localPlayer// ?: return false // Ensure localPlayer is not null
        //val session = session ?: return false // Ensure session is not null

        val yaw = Math.toRadians(player.rotationYaw.toDouble()).toFloat()

        player.teleport(session, player.posX - sin(yaw) * 3f, player.posY + 3f, player.posZ + cos(yaw) * 3f)

        return false // Ensure a Boolean is returned
    }
}
