package net.ccbluex.liquidbounce.features.module.modules.combat;

import net.ccbluex.liquidbounce.api.minecraft.block.state.IIBlockState;
import net.ccbluex.liquidbounce.api.minecraft.item.IItemStack;
import net.ccbluex.liquidbounce.api.minecraft.util.IMovingObjectPosition;
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;

import java.lang.reflect.Field;

/**
 * @author Alan (made good code)
 * @since 24/06/2023
 */

@ModuleInfo(name = "AutoTool2", description = "Rise", category = ModuleCategory.PLAYER)
public class AutoTool2 extends Module {

    private int slot, lastSlot = -1;
    private int blockBreak;
    private WBlockPos blockPos;
    private static boolean render;
    private IMovingObjectPosition objectPosition;

    @EventTarget
    public void onBlockDamage() {
        blockBreak = 3;
        blockPos = objectPosition.getBlockPos();
    }

    ;

    public static void setSlot(final int slot) {
        setSlot(slot, true);
    }

    public static void setSlot(final int slot, final boolean render) {
        if (slot < 0 || slot > 8) {
            return;
        }
        PlayerControllerMP wrapped = null;

        mc.getThePlayer().getInventory().setCurrentItem(slot);
        wrapped.syncCurrentPlayItem();
    }

    @EventTarget
    public void onPreUpdate() throws NoSuchFieldException, IllegalAccessException {
        switch (mc.getObjectMouseOver().getTypeOfHit()) {
            case BLOCK:
                if (blockPos != null && blockBreak > 0) {
                    slot = this.findTool(blockPos);
                } else {
                    slot = -1;
                }
                break;

            case ENTITY:
                slot = this.findSword();
                break;

            default:
                slot = -1;
                break;
        }

        if (lastSlot != -1) {
            this.setSlot(lastSlot, render);
        } else if (slot != -1) {
            this.setSlot(slot, render);
        }

        lastSlot = slot;
        blockBreak--;
    }

    ;

    public int findSword() throws IllegalAccessException, NoSuchFieldException {
        int bestDurability = -1;
        float bestDamage = -1;
        int bestSlot = -1;

        for (int i = 0; i < 9; i++) {
            final IItemStack itemStack = mc.getThePlayer().getInventory().getStackInSlot(i);
            final IItemStack itemStack2 = mc.getThePlayer().getInventory().getStackInSlot(i);

            if (itemStack == null) {
                continue;
            }

            if (itemStack.getItem() instanceof ItemSword) {
                final ItemSword sword = (ItemSword) itemStack.getItem();
                Field internalField = itemStack2.getClass().getDeclaredField("internal");
                internalField.setAccessible(true);
                ItemStack vanillaStack = (ItemStack) internalField.get(itemStack2);
                int sharpnessLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, vanillaStack);
                int damage = (int) ((int) Minecraft.getMinecraft().player.getHeldItemMainhand().getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(SharedMonsterAttributes.ATTACK_DAMAGE.getName()).iterator().next().getAmount()+ sharpnessLevel * 1.25F);
                final int durability = sword.getMaxDamage();

                if (bestDamage < damage) {
                    bestDamage = damage;
                    bestDurability = durability;
                    bestSlot = i;
                }

                if (damage == bestDamage && durability > bestDurability) {
                    bestDurability = durability;
                    bestSlot = i;
                }
            }
        }

        return bestSlot;
    }

    public int findTool(final WBlockPos blockPos) {
        float bestSpeed = -1;
        int bestSlot = -1;
        final IBlockState blockState = (IBlockState) mc.getTheWorld().getBlockState(blockPos);

        for (int i = 0; i < 9; i++) {
            final IItemStack itemStack = mc.getThePlayer().getInventory().getStackInSlot(i);

            if (itemStack != null) {
                final float speed = itemStack.getStrVsBlock((IIBlockState) blockState);

                if (speed > bestSpeed) {
                    bestSpeed = speed;
                    bestSlot = i;
                }
            }
        }

        return bestSlot;
    }
}
