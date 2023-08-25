package net.ccbluex.liquidbounce.features.module.modules.hyt

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo

@ModuleInfo(name = "StopMove", category = ModuleCategory.VULGAR, description = "1", chinesename = "禁止移动")
class StopMove : Module() {

    @EventTarget
    fun onMove(event: MoveEvent) {
        event.zero()
    }

}