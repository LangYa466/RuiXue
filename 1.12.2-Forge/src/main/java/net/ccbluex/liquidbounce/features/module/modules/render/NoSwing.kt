/*
 * LiquidBounce Base
 * God SkidBounce
 * Conding
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue

@ModuleInfo(name = "NoSwing", description = "Disabled swing effect when hitting an entity/mining a block.",chinesename = "无挥手动作", category = ModuleCategory.RENDER)
class NoSwing : Module() {
    val serverSideValue = BoolValue("ServerSide", true)
}