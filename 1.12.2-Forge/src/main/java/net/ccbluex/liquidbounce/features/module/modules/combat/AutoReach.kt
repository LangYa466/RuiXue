package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.FloatValue

@ModuleInfo(name = "AutoReach", description = "Auto Changer your Range", category = ModuleCategory.COMBAT)
class AutoReach : Module(){
    private val customrange = FloatValue("CustomRange",2.9f,1.0f,10.0f)
    val killaura = LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura
    @EventTarget
     fun onUpdate(event : UpdateEvent){
         if (!mc.thePlayer!!.onGround){
             killaura.rangeValue.set(customrange.get())

         }
     }
    override val tag: String?
        get() = "${customrange.get()}"
}