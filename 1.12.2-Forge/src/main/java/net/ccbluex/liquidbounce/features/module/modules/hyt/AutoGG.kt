package net.ccbluex.liquidbounce.features.module.modules.hyt

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.module.modules.player.InventoryCleaner
import net.ccbluex.liquidbounce.features.module.modules.world.ChestStealer
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.ListValue
import net.ccbluex.liquidbounce.value.TextValue
import net.minecraft.network.play.server.SPacketChat
import java.util.regex.Pattern
@ModuleInfo(name = "HytAutoGG", category = ModuleCategory.VULGAR, description = "Auto GG",chinesename = "花雨庭自动发送结束消息")
class AutoGG : Module() {

    private val modeValue = ListValue("Server", arrayOf( "花雨庭起床","花雨庭空岛","花雨庭十六人起床"), "花雨庭起床")
    private val send = BoolValue("SendMessage",false)
    private val prefix = BoolValue("@",true)
    private val ggtext = TextValue("Text", "[瑞雪]GG")
    private val starttext = TextValue("Text2", "@我正在使用瑞雪")
    var totalPlayed = 0
    var win = 0
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet.unwrap()
        if (packet is SPacketChat && this.state.takeIf { this.state } == true) {
            val text = packet.chatComponent.unformattedText
            when (modeValue.get().toLowerCase()) {
                "花雨庭起床" -> {
                    if (text.contains("      喜欢      一般      不喜欢", true)) {
                        mc.thePlayer!!.sendChatMessage((if (prefix.get()) "@" else "") + ggtext.get())
                        win += 1
                        LiquidBounce.hud.addNotification(Notification("花雨庭自动发送结束消息", "恭喜胜利！", NotifyType.INFO))
                    }
                    if (text.contains("起床战争>> 游戏开始 ...", true)) {
                        totalPlayed++
                        LiquidBounce.hud.addNotification(Notification("花雨庭自动发送消息", "游戏开始！！", NotifyType.INFO))
                        if(send.get())
                        mc.thePlayer!!.sendChatMessage(starttext.get())
                    }
                }

                "花雨庭十六人起床" -> {
                    if (text.contains("[起床战争] Game 结束！感谢您的参与！", true)) {
                        LiquidBounce.hud.addNotification(Notification("花雨庭自动发送结束消息", "游戏结束", NotifyType.INFO))
                        mc.thePlayer!!.sendChatMessage((if (prefix.get()) "@" else "") + ggtext.get())
                    }
                }

                "花雨庭空岛" -> {
                    val invcancel = LiquidBounce.moduleManager.getModule(InventoryCleaner::class.java) as InventoryCleaner
                    val stealer = LiquidBounce.moduleManager.getModule(ChestStealer::class.java) as ChestStealer
                    val killaura = LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura
                    if (text.contains("你现在是观察者状态. 按E打开菜单.", true)) {
                        LiquidBounce.hud.addNotification(Notification("花雨庭自动发送结束消息", "游戏结束", NotifyType.INFO))
                        mc.thePlayer!!.sendChatMessage((if (prefix.get()) "@" else "") + ggtext.get())
                        if(invcancel.state.takeIf { invcancel.state } == true)
                            invcancel.state = false
                        if(stealer.state.takeIf { stealer.state } == true)
                            stealer.state = false
                        if(killaura.state.takeIf { killaura.state } == true)
                            killaura.state = false
                        LiquidBounce.hud.addNotification(Notification("自动关闭", "自动关闭功能", NotifyType.INFO))
                    }
                }
            }
        }
    }
    override fun handleEvents() = true
    override val tag: String
        get() = modeValue.get()
}
