package net.ccbluex.liquidbounce.features.module.modules.hyt

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.FloatValue

@ModuleInfo(
    name = "PlayerSize",
    description = "Edit the player's size",chinesename = "玩家大小",
    category = ModuleCategory.VULGAR)
class PlayerSize : Module() {
    val playerSizeValue = FloatValue("PlayerSize", 0.5F, 0.01F, 5F)
}