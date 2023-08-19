package tools;

import java.awt.Graphics;

public abstract class Renderable {
    int frame;
    int frameCount;

    public Renderable(double time, int fps) {
        this.frame = 1;
        this.frameCount = (int) (time * fps);
    }

    public final void render(Graphics g) {
        float q;
        if (frameCount == 0) {
            q = 1;
        } else {
            q = ((float) frame / frameCount);
            q = Math.min(q, 1);
        }
        drawThis(g, q);
        if (frame < frameCount) {
            frame++;
        }
    }

    protected abstract void drawThis(Graphics g, float q);
}
