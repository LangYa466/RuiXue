package net.ccbluex.liquidbounce.features.module.modules.hyt

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo

@ModuleInfo(name = "LoveRX", description = "LoveRx",chinesename = "爱来自瑞雪", category = ModuleCategory.VULGAR)
class LOVELB : Module() {

    init {
        state = true
    }

    override fun onDisable() {
        state = true
    }


    override val tag: String
        get() = "爱来自瑞雪"
}



