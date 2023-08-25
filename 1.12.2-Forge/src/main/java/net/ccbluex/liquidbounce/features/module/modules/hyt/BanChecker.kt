package net.ccbluex.liquidbounce.features.module.modules.hyt

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType
import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.network.play.server.SPacketChat
import java.util.regex.Pattern

@ModuleInfo(name = "HytBanChecker", description = "Hyt",chinesename = "花雨庭封禁提示", category = ModuleCategory.VULGAR)
class BanChecker : Module(){
    private val AutoL = BoolValue ("BanAutoL-封禁自动嘲讽", true)
    private val antikef = BoolValue ("AntiKeFu-防客服", true)
    var ban = 0
    @EventTarget
    fun onPacket(event : PacketEvent){
        val packet = event.packet.unwrap()
        if(packet is SPacketChat){
            val matcher = Pattern.compile("玩家(.*?)在本局游戏中行为异常").matcher(packet.chatComponent.unformattedText)
            if(matcher.find()){
                ban ++
                val banname = matcher.group(1)
                LiquidBounce.hud.addNotification(Notification("封禁提示","$banname 被封禁了 现已封禁$ban 人",NotifyType.INFO))
                if(AutoL.get())
                    mc.thePlayer!!.sendChatMessage("@您好 我是瑞雪的用户 | L $banname 您已被封禁")
            }
            if(matcher.find() && ban > 3){
                if(antikef.get())
                LiquidBounce.hud.addNotification(Notification("封禁提示$ban 人","已封禁3人以上.可能是客服！自动HUB!",NotifyType.INFO))
                ban = 0
            }
            }
    }
}