/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.modules.render.HUD;
import net.ccbluex.liquidbounce.features.module.modules.render.OldHitting;
import net.ccbluex.liquidbounce.utils.render.AnimationUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.ccbluex.liquidbounce.utils.MinecraftInstance.mc2;
import static net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType.PLAYER_LIST;

@Mixin(GuiIngameForge.class)
public abstract class MixinGuiIngameForge extends MixinGuiInGame {

    @Shadow(remap = false)
    abstract boolean pre(ElementType type);

    @Shadow(remap = false)
    abstract void post(ElementType type);

    public float xScale = 0F;

    @Inject(
        method = "renderChat",
        slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/eventhandler/EventBus;post(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", ordinal = 0, remap = false)),
        at = @At(value = "RETURN", ordinal = 0),
        remap = false
    )
    private void fixProfilerSectionNotEnding(int width, int height, CallbackInfo ci) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.mcProfiler.getNameOfLastSection().endsWith("chat"))
            mc.mcProfiler.endSection();
    }

    @Inject(method = { "renderExperience"}, at = @At("HEAD"), remap = false)
    private void enableExperienceAlpha(int filled, int top, CallbackInfo ci) {
        GlStateManager.enableAlpha();
    }

    @Inject(method = { "renderExperience"}, at = @At("RETURN"), remap = false)
    private void disableExperienceAlpha(int filled, int top, CallbackInfo ci) {
        GlStateManager.disableAlpha();
    }

    @Inject(method = { "renderExperience"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void renderExperience(int width, int height, CallbackInfo ci) {
        HUD hud = (HUD) LiquidBounce.moduleManager.getModule(HUD.class);

        if (!((Boolean) hud.getHotbar().get())) {
            ci.cancel();
        }

    }

    @Inject(method = { "renderToolHighlight"}, at =@At("HEAD"), cancellable = true, remap = false)
    protected void renderToolHighlight(ScaledResolution res, CallbackInfo ci) {
        HUD hud = (HUD) LiquidBounce.moduleManager.getModule(HUD.class);
        if (!((Boolean) hud.getHotbar().get())) {
            ci.cancel();
        }
    }

    @Inject(method = { "renderFood"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void renderPlayerStats(int width, int height, CallbackInfo ci) {
        HUD hud = (HUD) LiquidBounce.moduleManager.getModule(HUD.class);
        if (!((Boolean) hud.getHotbar().get())) {
            ci.cancel();
        }
    }

    @Inject(method = { "renderHealth"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void renderHealth(int width, int height, CallbackInfo ci) {
        HUD hud = (HUD) LiquidBounce.moduleManager.getModule(HUD.class);
        if (!((Boolean) hud.getHotbar().get())) {
            ci.cancel();
        }
    }

    @Inject(method = { "renderArmor"}, at = @At("HEAD"), cancellable = true, remap = false)
    private void renderArmor(int width, int height, CallbackInfo ci) {
        HUD hud = (HUD) LiquidBounce.moduleManager.getModule(HUD.class);
        if (!((Boolean) hud.getHotbar().get())) {
            ci.cancel();
        }
    }

    
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    protected void renderPlayerList(int width, int height) {
        final Minecraft mc = Minecraft.getMinecraft();
        ScoreObjective scoreobjective = mc.world.getScoreboard().getObjectiveInDisplaySlot(0);
        NetHandlerPlayClient handler = mc2.player.connection;
        if (!mc.isIntegratedServerRunning() || handler.getPlayerInfoMap().size() > 1 || scoreobjective != null)
        {
            xScale = (float) AnimationUtils.animate((mc.gameSettings.keyBindPlayerList.isKeyDown() ? 100F : 0F), xScale, OldHitting.tabAnimations.get().equalsIgnoreCase("none") ? 1F : 0.0125F * RenderUtils.deltaTime);
            float rescaled = xScale / 100F;
            boolean displayable = rescaled > 0F;
            this.overlayPlayerList.updatePlayerList(displayable);
            if (!displayable || pre(PLAYER_LIST)) return;
            GlStateManager.pushMatrix();
            switch (OldHitting.tabAnimations.get().toLowerCase()) {
                case "zoom":
                    GlStateManager.translate(width / 2F * (1F - rescaled), 0F, 0F);
                    GlStateManager.scale(rescaled, rescaled, rescaled);
                    break;
                case "slide":
                    GlStateManager.scale(1F, rescaled, 1F);
                    break;
                case "none":
                    break;
            }
            
            this.overlayPlayerList.renderPlayerlist(width, mc.world.getScoreboard(), scoreobjective);
            GlStateManager.popMatrix();
            post(PLAYER_LIST);
        }
        else
        {
            this.overlayPlayerList.updatePlayerList(false);
        }
    }

}