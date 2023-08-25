/*
 * LiquidBounce Base
 * God SkidBounce
 * Conding
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render3DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.misc.AntiBot
import net.ccbluex.liquidbounce.injection.backend.Backend
//import net.ccbluex.liquidbounce.ui.font.AWTFontRenderer
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils.quickDrawRect
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.FontValue
import net.minecraft.client.renderer.GlStateManager.*
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import kotlin.math.roundToInt

@ModuleInfo(name = "NameTags", description = "Changes the scale of the nametags so you can always read them.",chinesename = "名称栏", category = ModuleCategory.RENDER)
class NameTags : Module() {
    private val armorValue = BoolValue("Armor", true)
    private val clearNamesValue = BoolValue("ClearNames", false)
    private val Healthbar=BoolValue("HealthBar",true)
    private val distanceValue=BoolValue("distance",true)
    private val health=BoolValue("Health",true)
    private val scaleValue = FloatValue("Scale", 1F, 0.7F, 4F)

    private val posYValue = FloatValue("PosY", 0F, 0F, 100F)
    private val fontValue = FontValue("Font", Fonts.minecraftFont)

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        glPushAttrib(GL_ENABLE_BIT)
        glPushMatrix()

        // Disable lightning and depth test
        glDisable(GL_LIGHTING)
        glDisable(GL_DEPTH_TEST)

        glEnable(GL_LINE_SMOOTH)

        // Enable blend
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        for (entity in mc.theWorld!!.loadedEntityList) {
            if (!EntityUtils.isSelected(entity, false))
                continue

            renderNameTag(entity.asEntityLivingBase(),
                if (clearNamesValue.get())
                    ColorUtils.stripColor(entity.displayName?.unformattedText) ?: continue
                else
                    (entity.displayName ?: continue).unformattedText
            )
        }

        glPopMatrix()
        glPopAttrib()

        // Reset color
        glColor4f(1F, 1F, 1F, 1F)
    }

    private fun renderNameTag(entity: IEntityLivingBase, tag: String) {
        val thePlayer = mc.thePlayer ?: return
        val fontRenderer = fontValue.get()
        // Modify tag
        val bot = AntiBot.isBot(entity)
        val nameColor = "§7§c"
        val healthText = if (distanceValue.get()) " §7${thePlayer.getDistanceToEntity(entity).roundToInt()}m " else ""
        val disText = if (entity.health <entity.maxHealth/4) "§4 " + entity.health.toInt() + " HP" else if (entity.health <entity.maxHealth/2) "§6 " + entity.health.toInt() + " HP" else "§2 " + entity.health.toInt() + " HP"
        val botText = if (bot) " §c§lBot" else ""

        val text = if (health.get()) "$nameColor$tag$healthText$disText$botText" else "$nameColor$tag$healthText$botText"

        // Push
        glPushMatrix()

        // Translate to player position
        val timer = mc.timer
        val renderManager = mc.renderManager


        glTranslated( // Translate to player position with render pos and interpolate it
            entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * timer.renderPartialTicks - renderManager.renderPosX,
            entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * timer.renderPartialTicks - renderManager.renderPosY + entity.eyeHeight.toDouble() + 0.55,
            entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * timer.renderPartialTicks - renderManager.renderPosZ
        )

        glRotatef(-mc.renderManager.playerViewY, 0F, 1F, 0F)
        glRotatef(mc.renderManager.playerViewX, 1F, 0F, 0F)


        // Scale
        var distance = thePlayer.getDistanceToEntity(entity) * 0.25f

        if (distance < 1F)
            distance = 1F

        val scale = distance / 100f * scaleValue.get()

        glScalef(-scale, -scale, scale)

        //AWTFontRenderer.assumeNonVolatile = true

        // Draw NameTag
        val width = fontRenderer.getStringWidth(text) * 0.5f
        RenderUtils.drawShadowWithCustomAlpha(
            (-width - 2F).toInt().toFloat(), (-2F - posYValue.get()).toInt().toFloat(), (width + 4F+width + 2F).toInt().toFloat(),
            (fontRenderer.fontHeight + 2F).toInt().toFloat(),255f
        )
        val color = if (entity.health<=entity.maxHealth)Color.GREEN else if (entity.health<entity.maxHealth/2)Color.YELLOW else if(entity.health<entity.maxHealth/4)Color.RED else Color.RED
        if(Healthbar.get()){
            RenderUtils.drawRect(-width - 2F, fontRenderer.fontHeight + 0F, entity.health/entity.maxHealth*(width + 4F), fontRenderer.fontHeight + 2F, color)
        }


        glDisable(GL_TEXTURE_2D)
        glEnable(GL_BLEND)


        quickDrawRect(-width - 2F, -2F - posYValue.get(), width + 4F, fontRenderer.fontHeight + 4F, Color(0,0,0,70).rgb)


        glEnable(GL_TEXTURE_2D)

        fontRenderer.drawString(text, 1F + -width, if (fontRenderer == Fonts.minecraftFont) 0F - posYValue.get() else 0.5F - posYValue.get(),
            0xFFFFFF, true)

        //AWTFontRenderer.assumeNonVolatile = false

        if (armorValue.get() && classProvider.isEntityPlayer(entity)) {
            mc.renderItem.zLevel = -147F

            val indices: IntArray = if (Backend.MINECRAFT_VERSION_MINOR == 8) (0..4).toList().toIntArray() else intArrayOf(0, 1, 2, 3, 5, 4)

            for (index in indices) {
                val equipmentInSlot = entity.getEquipmentInSlot(index) ?: continue

                mc.renderItem.renderItemAndEffectIntoGUI(equipmentInSlot, -50 + index * 20, -22)
            }

            enableAlpha()
            disableBlend()
            enableTexture2D()
        }

        // Pop
        glPopMatrix()
    }
}
