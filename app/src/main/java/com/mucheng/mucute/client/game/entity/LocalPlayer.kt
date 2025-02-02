package com.mucheng.mucute.client.game.entity

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
import com.mucheng.mucute.relay.MuCuteRelaySession

@Suppress("MemberVisibilityCanBePrivate")
class LocalPlayer : Player(0L, 0L, UUID.randomUUID(), "") {

    override var runtimeEntityId: Long = 0L
        private set

    override var uniqueEntityId: Long = 0L
        private set

    override var uuid: UUID = UUID.randomUUID()
        private set

    override fun onReceived(packet: BedrockPacket): Boolean {
        super.onReceived(packet)
        if (packet is StartGamePacket) {
            runtimeEntityId = packet.runtimeEntityId
            uniqueEntityId = packet.uniqueEntityId
        }
        return false
    }

    override fun onDisconnect(reason: String) {
        super.onDisconnect(reason)
        reset()
    }

    fun teleport(session: MuCuteRelaySession, x: Float, y: Float, z: Float) {
        move(x, y, z)
        session.clientBound(MovePlayerPacket().apply {
            runtimeEntityId = this@LocalPlayer.runtimeEntityId
            position = Vector3f.from(x, y, z)
            rotation = Vector3f.from(rotationPitch, rotationYaw, 0f)
            //if (rideEntity != null) {
			//	ridingRuntimeEntityId = rideEntity!!
	    mode = MovePlayerPacket.Mode.HEAD_ROTATION
			/*} else {
				mode = MovePlayerPacket.Mode.NORMAL
			}*/
        })
    }

	fun teleport(session: MuCuteRelaySession, vec3: Vector3f) {
		teleport(vec3.x, vec3.y, vec3.z)
    }

}
