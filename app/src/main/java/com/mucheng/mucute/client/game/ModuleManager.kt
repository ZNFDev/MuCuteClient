package com.mucheng.mucute.client.game

import com.mucheng.mucute.client.application.AppContext
import com.mucheng.mucute.client.game.entity.LocalPlayer
import com.mucheng.mucute.client.game.module.combat.AntiKnockbackModule
import com.mucheng.mucute.client.game.module.effect.HasteModule
import com.mucheng.mucute.client.game.module.effect.LevitationModule
import com.mucheng.mucute.client.game.module.effect.PoseidonModule
import com.mucheng.mucute.client.game.module.effect.RegenModule
import com.mucheng.mucute.client.game.module.misc.NoClipModule
import com.mucheng.mucute.client.game.module.misc.ClipModule
import com.mucheng.mucute.client.game.module.motion.AirJumpModule
import com.mucheng.mucute.client.game.module.motion.AutoJumpModule
import com.mucheng.mucute.client.game.module.motion.AutoWalkModule
import com.mucheng.mucute.client.game.module.motion.DesyncModule
import com.mucheng.mucute.client.game.module.motion.FlyModule
import com.mucheng.mucute.client.game.module.motion.HighJumpModule
import com.mucheng.mucute.client.game.module.motion.JetPackModule
import com.mucheng.mucute.client.game.module.motion.RandomMoveModule
import com.mucheng.mucute.client.game.module.motion.SlowFallModule
import com.mucheng.mucute.client.game.module.motion.SpeedModule
import com.mucheng.mucute.client.game.module.motion.SprintModule
import com.mucheng.mucute.client.game.module.visual.NightVisionModule
import com.mucheng.mucute.client.game.module.visual.NoHurtCamModule
import com.mucheng.mucute.client.game.module.visual.ZoomModule
import com.mucheng.mucute.relay.MuCuteRelaySession
import com.mucheng.mucute.relay.listener.MuCuteRelayPacketListener
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket

object ModuleManager : MuCuteRelayPacketListener {

    private val _modules: MutableList<Module> = ArrayList()

    val modules: List<Module> = _modules

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }


    private val localPlayer = LocalPlayer()

    private var session: MuCuteRelaySession? = null

    init {
        with(_modules) {
            add(FlyModule())
            add(ZoomModule())
            add(AirJumpModule())
            add(NoClipModule())
            add(ClipModule())
            add(NightVisionModule())
            add(HasteModule())
            add(SpeedModule())
            add(JetPackModule())
            add(LevitationModule())
            add(HighJumpModule())
            add(SlowFallModule())
            add(PoseidonModule())
            add(AntiKnockbackModule())
            add(RegenModule())
            add(AutoJumpModule())
            add(SprintModule())
            add(NoHurtCamModule())
            add(AutoWalkModule())
            add(RandomMoveModule())
            add(DesyncModule())
        }
    }

    fun initModules(muCuteRelaySession: MuCuteRelaySession) {
        this.session = muCuteRelaySession

        for (module in _modules) {
            module.session = muCuteRelaySession
            module.localPlayer = localPlayer
        }
    }

    fun saveConfig() {
        val configsDir = AppContext.instance.filesDir.resolve("configs")
        configsDir.mkdirs()

        val config = configsDir.resolve("UserConfig.json")
        val jsonObject = buildJsonObject {
            put("modules", buildJsonObject {
                _modules.forEach {
                    put(it.name, it.toJson())
                }
            })
        }

        config.writeText(json.encodeToString(jsonObject))
    }

    fun loadConfig() {
        val configsDir = AppContext.instance.filesDir.resolve("configs")
        configsDir.mkdirs()

        val config = configsDir.resolve("UserConfig.json")
        if (!config.exists()) {
            return
        }

        val jsonString = config.readText()
        if (jsonString.isEmpty()) {
            return
        }

        val jsonObject = json.parseToJsonElement(jsonString).jsonObject
        val modules = jsonObject["modules"]!!.jsonObject
        _modules.forEach { module ->
            (modules[module.name] as? JsonObject)?.let {
                module.fromJson(it)
            }
        }
    }

    override fun beforeClientBound(packet: BedrockPacket): Boolean {
        localPlayer.onReceived(packet)

        for (module in _modules) {
            if (module.onReceived(packet)) {
                return true
            }
        }

        return false
    }

    override fun beforeServerBound(packet: BedrockPacket): Boolean {
        localPlayer.onReceived(packet)

        for (module in _modules) {
            if (module.onReceived(packet)) {
                return true
            }
        }

        return false
    }

}
