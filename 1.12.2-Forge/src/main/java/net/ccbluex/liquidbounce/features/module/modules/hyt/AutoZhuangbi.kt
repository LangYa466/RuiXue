package net.ccbluex.liquidbounce.features.module.modules.hyt

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.TextValue
@ModuleInfo(name = "AutoZhuangbi", description = "Test",chinesename = "花雨庭死亡自动发送消息", category = ModuleCategory.VULGAR)
class AutoZhuangbi : Module() {
    private val message = BoolValue("AutoSendMessage", true)
    private val messages = TextValue("Messages", "[瑞雪]我决不能输!")
    private var gg = 0

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer!!.health <= 0.5) {
            sendmessage()
            gg++
        }
    }

    fun sendmessage() {
        if (message.get()) {
            mc.thePlayer!!.sendChatMessage(messages.get())
        }
    }


    override val tag: String
        get() = "死亡:$gg"
}