package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue

@ModuleInfo(name = "HelpKillRange", description = "智能控制杀戮range",chinesename = "智能杀戮距离", category = ModuleCategory.COMBAT)
class HelpKillRange : Module() {
    private val hurttime: IntegerValue =  IntegerValue("hurttime-受伤时间", 10, 1, 10)
    private val hurttime2: IntegerValue = IntegerValue("hurttime2-受伤时间2", 10, 1, 10)
    private val AirRange: FloatValue = FloatValue("AirRange-空中杀戮距离", 3f, 0f, 5f)
    private val GroundRange: FloatValue = FloatValue("GroundRange-平地杀戮距离", 3.5f, 0f, 5f)
    var ticks=0
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer!!.isAirBorne){
            val killAura = LiquidBounce.moduleManager[KillAura::class.java] as KillAura
            killAura.rangeValue.set(AirRange.get())
        }
        if (mc.thePlayer!!.onGround){
            val killAura = LiquidBounce.moduleManager[KillAura::class.java] as KillAura
            killAura.rangeValue.set(GroundRange.get())
        }
        ticks++
        if (ticks ==1){
            val killAura = LiquidBounce.moduleManager[KillAura::class.java] as KillAura
            killAura.hurtTimeValue.set(hurttime.get())
        }
        if (ticks ==2){
            val killAura = LiquidBounce.moduleManager[KillAura::class.java] as KillAura
            killAura.hurtTimeValue.set(hurttime2.get())
        }
        if (ticks ==3){
            ticks=0
        }
    }




}
