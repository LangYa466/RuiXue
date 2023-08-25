/*
 * LiquidBounce Base
 * God SkidBounce
 * Conding
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.entity;

import com.mojang.authlib.GameProfile;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.modules.combat.KeepSprint;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.FoodStats;
import net.minecraft.util.SoundEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.ccbluex.liquidbounce.utils.MinecraftInstance.mc;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends MixinEntityLivingBase {

    @Shadow
    @Final
    protected static DataParameter<Byte> MAIN_HAND;
    @Shadow
    public PlayerCapabilities capabilities;
    @Shadow
    protected int flyToggleTimer;

    @Shadow
    public abstract ItemStack getItemStackFromSlot(EntityEquipmentSlot p_getItemStackFromSlot_1_);

    @Shadow
    public abstract GameProfile getGameProfile();

    @Shadow
    protected abstract boolean canTriggerWalking();

    @Shadow
    protected abstract SoundEvent getSwimSound();

    @Shadow
    public abstract FoodStats getFoodStats();
    @Inject(method = "attackTargetEntityWithCurrentItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;setSprinting(Z)V", shift = At.Shift.AFTER))
    public void onAttackTargetEntityWithCurrentItem(CallbackInfo callbackInfo) {
        final KeepSprint ks = (KeepSprint) LiquidBounce.moduleManager.getModule(KeepSprint.class);
        if (ks.getState()) {
            final float s = 0.6f + 0.4f * ks.getS().getValue();
            this.motionX = this.motionX / 0.6 * s;
            this.motionZ = this.motionZ / 0.6 * s;
            if (mc.getThePlayer().getMoveForward() > 0) {
                this.setSprinting(ks.getAws().getValue());
            }
        }
    }
}