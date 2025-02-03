package com.mucheng.mucute.client.game.module.misc

import com.mucheng.mucute.client.game.Module
import com.mucheng.mucute.client.game.ModuleCategory
//import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket
import kotlin.math.cos
import kotlin.math.sin
import com.mucheng.mucute.relay.MuCuteRelaySession
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket
import org.cloudburstmc.protocol.bedrock.packet.StartGamePacket
import java.util.UUID
import org.cloudburstmc.math.vector.Vector2f
import org.cloudburstmc.math.vector.Vector3f
import org.cloudburstmc.math.vector.Vector3i
import org.cloudburstmc.protocol.bedrock.data.*
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventoryActionData
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventorySource
import org.cloudburstmc.protocol.bedrock.data.inventory.transaction.InventoryTransactionType
import org.cloudburstmc.protocol.bedrock.packet.*
import java.util.*
import kotlin.math.atan2
import kotlin.math.floor
//import com.mucheng.mucute.relay.MuCuteRelaySession

class ClipModule : Module("Clip", ModuleCategory.Misc) {

    override fun onReceived(packet: BedrockPacket): Boolean {
        val player = localPlayer// ?: return false // Ensure localPlayer is not null
        //val session = session ?: return false // Ensure session is not null

        val yaw = Math.toRadians(player.rotationYaw.toDouble()).toFloat()

        teleport(player.posX - sin(yaw) * 3f, player.posY + 3f, player.posZ + cos(yaw) * 3f)

        return false // Ensure a Boolean is returned
    }

    fun teleport(x: Float, y: Float, z: Float) {
        localPlayer.move(x, y, z)
        session.clientBound(MovePlayerPacket().apply {
            runtimeEntityId = localPlayer.runtimeEntityId
            position = Vector3f.from(x, y, z)
            rotation = Vector3f.from(localPlayer.rotationPitch, localPlayer.rotationYaw, 0f)
            //if (rideEntity != null) {
			//	ridingRuntimeEntityId = rideEntity!!
	        mode = MovePlayerPacket.Mode.HEAD_ROTATION
			/*} else {
				mode = MovePlayerPacket.Mode.NORMAL
			}*/
        })
    }

	fun teleport(vec3: Vector3f) {
		teleport(vec3.x, vec3.y, vec3.z)
    }
}
