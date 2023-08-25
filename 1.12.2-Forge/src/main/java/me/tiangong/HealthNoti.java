package me.tiangong;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.text.TextComponentString;

@ModuleInfo(name = "HealthNoti", description = "HealthNoti",chinesename = "血量提示",category = ModuleCategory.VULGAR)
public class HealthNoti extends Module {
    private float lastHealth = Minecraft.getMinecraft().player.getHealth();

    @EventTarget
    public void onUpdate() {
        float health = Minecraft.getMinecraft().player.getHealth();
        float maxHealth = Minecraft.getMinecraft().player.getMaxHealth();
        int attackDamage = (int) Minecraft.getMinecraft().player.getHeldItemMainhand().getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(SharedMonsterAttributes.ATTACK_DAMAGE.getName()).iterator().next().getAmount();
        String message = String.format("[TG]❤当前血量：%d/%d，攻击伤害：%d", (int) health, (int) maxHealth, attackDamage);
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(message));

        if (health > lastHealth) {
            float recover = health - lastHealth;
            String hint = String.format("[TG]回复了 %d 点生命值", (int) recover);
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(hint));
        }
        lastHealth = health;
    }
}
