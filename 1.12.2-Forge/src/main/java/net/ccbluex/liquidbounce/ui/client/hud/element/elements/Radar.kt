/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements


import me.tiangong.CustomColor
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.render.ESP
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.tenacity.ColorUtil
import net.ccbluex.liquidbounce.utils.tenacity.render.RoundedUtil
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import org.lwjgl.opengl.GL11.*
import org.lwjgl.util.vector.Vector2f
import java.awt.Color
import kotlin.math.*

@ElementInfo(name = "小地图")
class Radar(x: Double = 5.0, y: Double = 130.0) : Element(x, y) {

    private val sizeValue = FloatValue("Size", 90f, 30f, 500f)
    private val viewDistanceValue = FloatValue("View Distance", 4F, 0.5F, 32F)
    private val playerShapeValue = ListValue("Player Shape", arrayOf("Rectangle", "Circle"), "Circle")
    private val playerSizeValue = FloatValue("Player Size", 5.0F, 0.5f, 20F)
    private val fovSizeValue = FloatValue("FOV Size", 10F, 0F, 50F)
    private val shadowValue = BoolValue("Shadow", false)
    override val drawElement: Border?
        get() {
            var gradientColor1 =
                Color(CustomColor.r.get(), CustomColor.g.get(), CustomColor.b.get(), CustomColor.a.get())
            var gradientColor2 =
                Color(CustomColor.r.get(), CustomColor.g.get(), CustomColor.b.get(), CustomColor.a.get())
            var gradientColor3 =
                Color(CustomColor.r2.get(), CustomColor.g2.get(), CustomColor.b2.get(), CustomColor.a2.get())
            var gradientColor4 =
                Color(CustomColor.r2.get(), CustomColor.g2.get(), CustomColor.b2.get(), CustomColor.a2.get())

            val renderViewEntity = mc.renderViewEntity

            val size = sizeValue.get()

            val viewDistance = viewDistanceValue.get() * 16.0F

            val maxDisplayableDistanceSquare = ((viewDistance + fovSizeValue.get().toDouble()) *
                    (viewDistance + fovSizeValue.get().toDouble()))
            val halfSize = size / 2f

            RoundedUtil.drawGradientRound(
                0f,
                0f,
                size,
                size,
                CustomColor.ra.get(),
                ColorUtil.applyOpacity(gradientColor4, .85f),
                gradientColor1,
                gradientColor3,
                gradientColor2
            )

            if (shadowValue.get()) {
                RenderUtils.drawShadowWithCustomAlpha(0F, 0F, size, size, 255F)
            }


            // border section

            // end

            RenderUtils.makeScissorBox(x.toFloat(), y.toFloat(), x.toFloat() + ceil(size), y.toFloat() + ceil(size))

            glEnable(GL_SCISSOR_TEST)

            glPushMatrix()

            glTranslatef(halfSize, halfSize, 0f)
            glRotatef(renderViewEntity!!.rotationYaw, 0f, 0f, -1f)

            glEnable(GL_BLEND)
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

            glColor4f(1.0f, 1.0f, 1.0f, 1.0f)

            glDisable(GL_TEXTURE_2D)
            glEnable(GL_LINE_SMOOTH)

            val circleMode = playerShapeValue.get().equals("circle", true)

            val tessellator = Tessellator.getInstance()
            val worldRenderer = tessellator.buffer

            if (circleMode)
                glEnable(GL_POINT_SMOOTH)

            var playerSize = playerSizeValue.get()

            glEnable(GL_POLYGON_SMOOTH)

            worldRenderer.begin(GL_POINTS, DefaultVertexFormats.POSITION_COLOR)
            glPointSize(playerSize)

            for (entity in mc.theWorld!!.loadedEntityList) {
                if (entity != null && entity !== mc.thePlayer && EntityUtils.isSelected(entity, false)) {
                    val positionRelativeToPlayer = Vector2f(
                        (renderViewEntity.posX - entity.posX).toFloat(),
                        (renderViewEntity.posZ - entity.posZ).toFloat()
                    )

                    if (maxDisplayableDistanceSquare < positionRelativeToPlayer.lengthSquared())
                        continue

                    val transform = fovSizeValue.get() > 0F

                    if (transform) {
                        glPushMatrix()

                        glTranslatef(
                            (positionRelativeToPlayer.x / viewDistance) * size,
                            (positionRelativeToPlayer.y / viewDistance) * size, 0f
                        )
                        glRotatef(entity.rotationYaw, 0f, 0f, 1f)
                    }

                    val color = (LiquidBounce.moduleManager[ESP::class.java] as ESP).getColor(entity)

                    worldRenderer.pos(
                        ((positionRelativeToPlayer.x / viewDistance) * size).toDouble(),
                        ((positionRelativeToPlayer.y / viewDistance) * size).toDouble(), 0.0
                    )
                        .color(
                            color.red / 255.0f, color.green / 255.0f,
                            color.blue / 255.0f, 1.0f
                        ).endVertex()


                    if (transform)
                        glPopMatrix()
                }
            }

            tessellator.draw()

            if (circleMode) {
                glDisable(GL_POINT_SMOOTH)
            }

            glDisable(GL_POLYGON_SMOOTH)

            glEnable(GL_TEXTURE_2D)
            glDisable(GL_BLEND)
            glDisable(GL_LINE_SMOOTH)

            glDisable(GL_SCISSOR_TEST)

            glPopMatrix()

            return Border(0F, 0F, size, size)
        }

}