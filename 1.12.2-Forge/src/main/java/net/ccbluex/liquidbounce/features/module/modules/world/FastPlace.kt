/*
 * LiquidBounce Base
 * God SkidBounce
 * Conding
 */
package net.ccbluex.liquidbounce.features.module.modules.world

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.item.ItemBlock

@ModuleInfo(name = "FastPlaceFix", description = "Allows you to place blocks faster.",chinesename = "右键无延时", category = ModuleCategory.WORLD)
class FastPlace : Module() {
    val speedValue = IntegerValue("Speed", 0, 0, 4)
    val onlyBlock = BoolValue("OnlyBlock",false)
    private val blockonlyValue = BoolValue("BlocksOnly", false)
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!blockonlyValue.get() || mc.thePlayer!!.heldItem!!.item is ItemBlock) {
            mc.rightClickDelayTimer = speedValue.get()
        }
    }
}
