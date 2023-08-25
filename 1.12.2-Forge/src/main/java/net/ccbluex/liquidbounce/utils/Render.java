package net.ccbluex.liquidbounce.utils;


import net.ccbluex.liquidbounce.api.minecraft.util.WVec3;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.glColor4d;

public class Render {
    public float alpha;
    public WVec3 vec3;
    public long time;
    public float d;
    public Color color;
    public Render(double x,double y,double z,long time,Color color){
        this.alpha=255f;
        this.vec3=new WVec3(x,y,z);
        this.time=time;
        this.color=color;
    }
    public static void setColor(int colorHex) {
        float alpha = (float) (colorHex >> 24 & 255) / 255.0F;
        float red = (float) (colorHex >> 16 & 255) / 255.0F;
        float green = (float) (colorHex >> 8 & 255) / 255.0F;
        float blue = (float) (colorHex & 255) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }
    public void draw(){
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        GL11.glDisable(3553);
        GlStateManager.disableDepth();
        GL11.glEnable(2848);
        GL11.glDisable(2896);
        GL11.glDepthMask(false);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(3.0F);
        GL11.glBegin(3);
        double renderPosX = Minecraft.getMinecraft().getRenderManager().viewerPosX;
        double renderPosY = Minecraft.getMinecraft().getRenderManager().viewerPosY;
        double renderPosZ = Minecraft.getMinecraft().getRenderManager().viewerPosZ;
        //  RenderUtils.glColor(ColorUtils.rainbow().getRGB(),alpha);
        this.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)alpha).getRGB());
        for (int i=0;i<=360;i++) {
            //   this.alpha = MathHelper.clamp_float(this.alpha = (float)((double)this.alpha - 1.0D * i), 0.0F, 255.0F);
            GL11.glVertex3d(
                    vec3.getXCoord() - renderPosX + Math.cos(i * Math.PI / 180) * 0.6*d,
                    vec3.getYCoord() - renderPosY,
                    vec3.getZCoord() - renderPosZ + Math.sin(i * Math.PI / 180) * 0.6*d);
        }
        GL11.glEnd();
        GL11.glBegin(5);
        for (int i=0;i<=360;i+=10) {
            for (int var11=0;var11<=3;var11++) {
                GL11.glVertex3d(vec3.getXCoord() - renderPosX + -Math.sin(Math.toRadians(i)) * (double) this.d, vec3.getYCoord() - renderPosY, vec3.getZCoord() - renderPosZ + Math.cos(Math.toRadians(i)) * (double) this.d);
                GL11.glVertex3d(vec3.getXCoord() - renderPosX + -Math.sin(Math.toRadians(i)) * ((double) this.d - var11 / 10.0D), vec3.getYCoord() - renderPosY, vec3.getZCoord() - renderPosZ + Math.cos(Math.toRadians(i)) * ((double) this.d - var11 / 10.0D));
            }
        }
        double var14 = 0.0D;
        if(var14 < 361.0D) {
            var14 = var14 + 5;
        }
        double var15 = 0.0D;
        if(var15 < 255) {

            var15 = var15 + 3;
        }
        GL11.glEnd();
        GL11.glDepthMask(true);
        GlStateManager.enableDepth();
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        if (d==1.5F) {
            this.alpha = MathHelper.clamp(this.alpha = (float) ((double) this.alpha  - 1.0D * var15), 0.0F, 255.0F);
        }
        this.d = MathHelper.clamp(this.d = (float)((double)this.d + 0.005D * var14), 0.0F, 1.5F);
    }
    public float alpha(){
        return alpha;
    }
}
