package com.mucheng.mucute.client.game.module.misc

import com.mucheng.mucute.client.game.Module
import com.mucheng.mucute.client.game.ModuleCategory
import org.cloudburstmc.protocol.bedrock.data.Ability
import org.cloudburstmc.protocol.bedrock.data.AbilityLayer
import org.cloudburstmc.protocol.bedrock.data.PlayerPermission
import org.cloudburstmc.protocol.bedrock.data.command.CommandPermission
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket
import org.cloudburstmc.protocol.bedrock.packet.UpdateAbilitiesPacket
import kotlin.math.cos
import kotlin.math.sin

class ClipModule : Module("clip", ModuleCategory.Misc) {

    override fun onReceived(packet: BedrockPacket): Boolean {
        //if (isInGame)

    		val player = localPlayer
    		val yaw = Math.toRadians(player.rotationYaw.toDouble()).toFloat()
    
    		player.teleport(session, player.posX - sin(yaw) * 3f, player.posY + 3f, player.posZ + cos(yaw) * 3f)
    	}
        return false
    }

}
