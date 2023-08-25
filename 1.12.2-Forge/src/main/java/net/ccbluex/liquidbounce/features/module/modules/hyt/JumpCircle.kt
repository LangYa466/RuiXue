package net.ccbluex.liquidbounce.features.module.modules.hyt


import me.tiangong.CustomColor
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render3DEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ColorUtil
import net.ccbluex.liquidbounce.utils.Render
import net.ccbluex.liquidbounce.utils.render.ColorUtils.LiquidSlowly
import net.ccbluex.liquidbounce.utils.render.ColorUtils.fade
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.lang.Math.cos

//import kotlin.math.cos
////import kotlin.math.sin

@ModuleInfo(name = "JumpCircle", description =  "JumpCircle", chinesename = "跳跃光环",category = ModuleCategory.RENDER)
class JumpCircle : Module() {
    val typeValue = ListValue("Mode", arrayOf("OldCircle", "NewCircle"), "OldCircle")

    //NewCircle
    val disappearTime = IntegerValue("Time", 1000, 1000, 3000)
    val radius = FloatValue("Radius", 2f, 1f, 5f)
    private val colorModeValue =
        ListValue("Color", arrayOf("Custom", "Rainbow", "Sky", "LiquidSlowly", "Fade", "Client"), "Custom")
    private val saturationValue = FloatValue("Saturation", 1f, 0f, 1f)
    private val brightnessValue = FloatValue("Brightness", 1f, 0f, 1f)
    private val mixerSecondsValue = IntegerValue("Seconds", 2, 1, 10)

    //
    private val colorRedValue: IntegerValue = IntegerValue("Red", 255, 0, 255)
    private val colorGreenValue = IntegerValue("Green", 255, 0, 255)
    private val colorBlueValue = IntegerValue("Blue", 255, 0, 255)
    private val astolfoRainbowOffset = IntegerValue("AstolfoOffset", 5, 1, 20)
    private val astolfoRainbowIndex = IntegerValue("AstolfoIndex", 109, 1, 300)

    private val points = mutableMapOf<Int, MutableList<net.ccbluex.liquidbounce.utils.Render>>()
    var jump = false;
    var entityjump = false;
    val circles = mutableListOf<Circle>()
    var red = colorRedValue.get()
    var green = colorGreenValue.get()
    var blue = colorBlueValue.get()

    @EventTarget
    fun onRender3D(event: Render3DEvent?) {
        when (typeValue.get().toLowerCase()) {
            "oldcircle" -> {
                points.forEach {
                    for (point in it.value) {
                        point.draw()
                        if (point.alpha < 0F) {
                            it.value.remove(point)
                        }
                    }
                }
            }

            "newcircle" -> {
                circles.removeIf { System.currentTimeMillis() > it.time + disappearTime.get() }

                GL11.glPushMatrix()

                GL11.glEnable(GL11.GL_BLEND)
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
                GL11.glDisable(GL11.GL_CULL_FACE)
                GL11.glDisable(GL11.GL_TEXTURE_2D)
                GL11.glDisable(GL11.GL_DEPTH_TEST)
                GL11.glDepthMask(false)
                GL11.glDisable(GL11.GL_ALPHA_TEST)
                GL11.glShadeModel(GL11.GL_SMOOTH)

                circles.forEach { it.draw() }

                GL11.glDisable(GL11.GL_BLEND)
                GL11.glEnable(GL11.GL_CULL_FACE)
                GL11.glEnable(GL11.GL_TEXTURE_2D)
                GL11.glEnable(GL11.GL_DEPTH_TEST)
                GL11.glDepthMask(true)
                GL11.glEnable(GL11.GL_ALPHA_TEST)
                GL11.glShadeModel(GL11.GL_FLAT)

                GL11.glPopMatrix()
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!mc.thePlayer!!.onGround && !jump) {
            jump = true
        }
        if (mc.thePlayer!!.onGround && jump) {
            updatePoints(mc.thePlayer!!);
            jump = false
        }
        /* for(entity in mc.theWorld.playerEntities) {
             if (!entity.onGround && !entityjump) {
                 entityjump = true
             }
             if (entity.onGround && entityjump) {
                 updatePoints(entity);
                 entityjump = false
             }
         }*/
    }

    fun updatePoints(entity: IEntityLivingBase) {
        when (typeValue.get().toLowerCase()) {
            "oldcircle" -> {
                val counter = intArrayOf(0)
                (points[entity.entityId] ?: mutableListOf<Render>().also { points[entity.entityId] = it }).add(
                    Render(
                        entity.posX, entity.entityBoundingBox.minY, entity.posZ, System.currentTimeMillis(),
                        ColorUtil.astolfoRainbow(
                            counter[0] * 100,
                            astolfoRainbowOffset.get(),
                            astolfoRainbowIndex.get()
                        )
                    )
                )
                counter[0] = counter[0] + 1
            }

            "newcircle" -> {
                circles.add(
                    Circle(
                        System.currentTimeMillis(),
                        mc.thePlayer!!.posX,
                        mc.thePlayer!!.posY,
                        mc.thePlayer!!.posZ
                    )
                )
            }
        }
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        points.clear()
    }

    override fun onDisable() {
        points.clear()
    }

    class Circle(val time: Long, val x: Double, val y: Double, val z: Double) {
        var entity: IEntityLivingBase = mc.thePlayer!!
        val jumpModule = LiquidBounce.moduleManager.getModule(JumpCircle::class.java) as JumpCircle
        var colorModeValue = jumpModule.colorModeValue.get()
        var colorRedValue = jumpModule.colorRedValue.get()
        var colorGreenValue = jumpModule.colorGreenValue.get()
        var colorBlueValue = jumpModule.colorBlueValue.get()
        var mixerSecondsValue = jumpModule.mixerSecondsValue.get()
        var saturationValue = jumpModule.saturationValue.get()
        var brightnessValue = jumpModule.brightnessValue.get()

        fun draw() {
            if (jumpModule == null) {
                return
            }

            val dif = (System.currentTimeMillis() - time)
            val c = 255 - (dif / jumpModule.disappearTime.get().toFloat()) * 255

            GL11.glPushMatrix()

            GL11.glTranslated(
                x - mc.renderManager.viewerPosX,
                y - mc.renderManager.viewerPosY,
                z - mc.renderManager.viewerPosZ
            )

            GL11.glBegin(GL11.GL_TRIANGLE_STRIP)
            for (i in 0..360) {
                val color = getColor(entity, 0);

                val x = (dif * jumpModule.radius.get() * 0.001 * Math.sin(Math.toRadians(i.toDouble())))
                val z = (dif * jumpModule.radius.get() * 0.001 * cos(Math.toRadians(i.toDouble())))

                RenderUtils.glColor(color.red, color.green, color.blue, 0)
                GL11.glVertex3d(x / 2, 0.0, z / 2)

                RenderUtils.glColor(color.red, color.green, color.blue, c.toInt())
                GL11.glVertex3d(x, 0.0, z)
            }
            GL11.glEnd()

            GL11.glPopMatrix()
        }

        fun getColor(ent: IEntityLivingBase?, index: Int): Color {
            return when (colorModeValue) {
                "Custom" -> {
                    Color(colorRedValue, colorGreenValue, colorBlueValue)
                }

                "Client" -> {
                    net.ccbluex.liquidbounce.utils.tenacity.ColorUtil.applyOpacity(
                        Color(
                            CustomColor.r2.get(),
                            CustomColor.g2.get(),
                            CustomColor.b2.get(),
                            CustomColor.a2.get()
                        ), .85f
                    )
                }

                Color(
                    CustomColor.r.get(),
                    CustomColor.g.get(),
                    CustomColor.b.get(),
                    CustomColor.a.get()
                ).toString(), Color(
                    CustomColor.r2.get(),
                    CustomColor.g2.get(),
                    CustomColor.b2.get(),
                    CustomColor.a2.get()
                ).toString(), Color(
                    CustomColor.r.get(),
                    CustomColor.g.get(),
                    CustomColor.b.get(),
                    CustomColor.a.get()
                ).toString(),
                "Rainbow" -> {
                    Color(
                        RenderUtils.getRainbowOpaque(
                            mixerSecondsValue,
                            saturationValue,
                            brightnessValue,
                            index
                        )
                    )
                }

                "Sky" -> {
                    RenderUtils.skyRainbow(index, saturationValue, brightnessValue)
                }

                "LiquidSlowly" -> {
                    LiquidSlowly(System.nanoTime(), index, saturationValue, brightnessValue)!!
                }

                else -> fade(Color(colorRedValue, colorGreenValue, colorBlueValue), index, 100)
            }
        }
    }
}
