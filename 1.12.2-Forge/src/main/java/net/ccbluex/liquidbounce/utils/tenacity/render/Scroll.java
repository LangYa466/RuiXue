package net.ccbluex.liquidbounce.utils.tenacity.render;



import net.ccbluex.liquidbounce.utils.tenacity.animations.Animation;
import net.ccbluex.liquidbounce.utils.tenacity.animations.Direction;
import net.ccbluex.liquidbounce.utils.tenacity.animations.impl.SmoothStepAnimation;
import org.lwjgl.input.Mouse;

/**
 * @author cedo
 * @author Foggy
 */
public class Scroll {


    private float maxScroll = Float.MAX_VALUE, minScroll = 0, rawScroll;

    public void setMaxScroll(float maxScroll) {
        this.maxScroll = maxScroll;
    }

    public float getMaxScroll() {
        return maxScroll;
    }



    private float scroll;
    private Animation scrollAnimation = new SmoothStepAnimation(0, 0, Direction.BACKWARDS);

    public void onScroll(int ms) {
        scroll = (float) (rawScroll - scrollAnimation.getOutput());
        rawScroll += Mouse.getDWheel() / 4f;
        rawScroll = Math.max(Math.min(minScroll, rawScroll), -maxScroll);
        scrollAnimation = new SmoothStepAnimation(ms, rawScroll - scroll, Direction.BACKWARDS);
    }

    public boolean isScrollAnimationDone() {
        return scrollAnimation.isDone();
    }

    public float getScroll() {
        scroll = (float) (rawScroll - scrollAnimation.getOutput());
        return scroll;
    }



}
