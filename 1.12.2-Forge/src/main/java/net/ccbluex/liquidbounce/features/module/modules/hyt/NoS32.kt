package net.ccbluex.liquidbounce.features.module.modules.hyt

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.minecraft.network.play.server.SPacketConfirmTransaction

@ModuleInfo(name = "NoS32", category = ModuleCategory.EXPLOIT, description = "CatBounce", chinesename = "无S32发包")
class NoS32 : Module() {
    @EventTarget
    fun  onPacket(event: PacketEvent){
       val  e = event.packet.unwrap()
        if (e is SPacketConfirmTransaction){
            event.cancelEvent()
        }
    }


}