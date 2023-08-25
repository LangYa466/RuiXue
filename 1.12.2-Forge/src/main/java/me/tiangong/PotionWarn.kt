package me.tiangong

import net.ccbluex.liquidbounce.api.minecraft.potion.IPotion
import net.ccbluex.liquidbounce.api.minecraft.potion.PotionType
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue

@ModuleInfo(name = "PotionWarn", description = "Check Potion Warn", chinesename = "药水检测", category = ModuleCategory.MISC)
class PotionWarn : Module(){
    private val checkPotionNameValue = ListValue("CheckPotionName", arrayOf("DamageBoost","MoveSpeed","Jump","Regen"),"DamageBoost")
    private val checkDelayValue = IntegerValue("CheckDelay",2,2,100)
    private val messageValue = BoolValue("CheckMessage",true)
    private var checkPotionName: String = ""
    @EventTarget
    fun onUpdate(event: UpdateEvent){
        if (mc.thePlayer!!.ticksExisted % checkDelayValue.get() == 0) {
            for (entity in mc.theWorld!!.loadedEntityList) {
                if (entity != null && entity != mc.thePlayer && classProvider.isEntityPlayer(entity) && entity.asEntityLivingBase().isPotionActive(potionActiveName(checkPotionNameValue.get())!!) && EntityUtils.isSelected(entity,true)) {
                    if(messageValue.get()){
                        if(mc.thePlayer!!.getDistanceToEntity(entity) >= 4.3) {
                            ClientUtils.displayChatMessage("§l§8[§a§lPotionChecker§8]" + "§a§l" + "检测到" + entity.name + "§r§a§l拥有§c§l${checkPotionName}§a§l药水效果，并且距离为:[" + mc.thePlayer!!.getDistanceToEntity(entity) + "]!")
                        }else{
                            ClientUtils.displayChatMessage("§l§8[§a§lPotionChecker§8]" + "§a§l" + "检测到" + entity.name + "§r§a§l拥有§c§l${checkPotionName}§a§l药水效果，并且距离为:[" + mc.thePlayer!!.getDistanceToEntity(entity) + "]!,离你很近就在附近!")
                        }
                    }
                }
            }
        }
        when(checkPotionNameValue.get().toLowerCase()){
            "movespeed" -> checkPotionName = "速度"
            "jump‘" -> checkPotionName = "跳跃"
            "regen" -> checkPotionName = "恢复"
        }
    }
    fun potionActiveName(potionName: String): IPotion?{
        return when(potionName.toLowerCase()) {
            "movespeed" -> classProvider.getPotionEnum(PotionType.MOVE_SPEED)
            "jump" -> classProvider.getPotionEnum(PotionType.JUMP)
            "regen" -> classProvider.getPotionEnum(PotionType.REGENERATION)
            else -> null
        }
    }
}