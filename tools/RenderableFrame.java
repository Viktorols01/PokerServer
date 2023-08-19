package tools;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.function.BiConsumer;

import javax.swing.JFrame;

public abstract class RenderableFrame {
    private JFrame jframe;

    private RenderableGUI gui;

    public RenderableFrame(int width, int height) {
        this.jframe = new JFrame();
        this.gui = new RenderableGUI(width, height);
        initJFrame();
    }

    private void initJFrame() {
        jframe.setDefaultCloseOperation(3);
        addComponents(jframe);
        jframe.pack();
        jframe.setResizable(false);
        jframe.setVisible(true);
    }

    protected abstract void addComponents(JFrame jframe);

    protected abstract void updateRenderables(RenderableGUI gui);

    protected final void clearRenderables() {
        this.gui.clearRenderables();
    }

    protected final void addRenderable(double time, BiConsumer<Graphics, Float> consumer) {
        this.gui.addRenderable(new Renderable(time, gui.getFPS()) {
            @Override
            protected void drawThis(Graphics g, float q) {
                consumer.accept(g, q);
            }
        });
    }

    protected class RenderableGUI extends GUI {

        private ArrayList<Renderable> renderables;

        public RenderableGUI(int width, int height) {
            super(width, height, 60);
            this.renderables = new ArrayList<Renderable>();
        }

        @Override
        protected void setup() {
        }

        @Override
        protected void update() {
        }

        public void addRenderable(Renderable r) {
            this.renderables.add(r);
        }

        public void clearRenderables() {
            this.renderables = new ArrayList<Renderable>();
        }

        @Override
        protected void render(Graphics g) {
            synchronized (renderables) {
                for (Renderable r : renderables) {
                    r.render(g);
                }
            }
            ;
        }
    }

    public JFrame getJFrame() {
        return jframe;
    }

    public RenderableGUI getGUI() {
        return gui;
    }
}
