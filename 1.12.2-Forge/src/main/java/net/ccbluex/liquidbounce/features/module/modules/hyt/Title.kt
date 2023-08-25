package net.ccbluex.liquidbounce.features.module.modules.hyt

import net.ccbluex.liquidbounce.LiquidBounce.CLIENT_VERSION
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.TextValue
import org.lwjgl.opengl.Display
//test
@ModuleInfo(name = "Title", description = "Custom title.",chinesename = "自定义标题", category = ModuleCategory.MISC)
class Title : Module() {
    private var ticks = 0
    private var seconds = 0
    private var minutes = 0
    private var hours = 0
    private var timeText = ""
    @EventTarget
    fun update(event: UpdateEvent?) {
        ticks++
        if (ticks == 20) {
            seconds++
            ticks = 0
        }
        if (seconds == 60) {
            minutes++
            seconds = 0
        }
        if (minutes == 60) {
            hours++
            minutes = 0
        }
        timeText = "$hours 时 $minutes 分 $seconds 秒 "
        Display.setTitle(defaultText.get() + if (time.get()) " $timeText" else "")
    }

    override fun onDisable() {
        try {
            Display.setTitle("瑞雪 $CLIENT_VERSION | 开发者:Nelly")
        } catch (e: Exception) {
            Display.setTitle("瑞雪 $CLIENT_VERSION | 开发者:Nelly")
        }
    }

    companion object {
        private val time = BoolValue("TimeDisplay", true)
        private val defaultText = TextValue("Title", "瑞雪 | ")
    }
}