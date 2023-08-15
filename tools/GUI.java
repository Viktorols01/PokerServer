package tools;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public abstract class GUI {
    private JFrame jframe;
    private JPanel panel;
    private Input input;

    private Thread thread;

    private int width;
    private int height;
    private final int fps;
    private int frame;

    public GUI() {
        this.width = 1200;
        this.height = 800;
        this.fps = 100;
        start();
    }

    public GUI(int width, int height, int fps) {
        this.width = width;
        this.height = height;
        this.fps = fps;
        start();
    }

    protected abstract void setup();

    protected abstract void update();

    protected abstract void render(Graphics g);

    private void start() {
        this.jframe = new JFrame();
        this.panel = new Panel(this.width, this.height);
        this.input = new Input();

        this.jframe.setResizable(false);
        this.jframe.add(this.panel);
        this.jframe.pack();
        this.jframe.setDefaultCloseOperation(3);
        this.jframe.setVisible(true);
        this.jframe.setFocusable(true);

        this.frame = 0;

        this.setup();
        this.thread = new Thread(() -> {
            long nanos = System.nanoTime();
            long delay = (long) (1e9 / this.fps);
            while (true) {
                this.update();
                this.panel.repaint();
                while (System.nanoTime() - nanos < delay) {
                    Thread.yield();
                }
                nanos += delay;
                this.frame++;
            }
        });
        this.thread.start();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getFPS() {
        return this.fps;
    }

    public int getFrame() {
        return this.frame;
    }

    public Point getMousePosition() {
        return input.getMousePosition();
    }

    public Point getMouseHeldPosition() {
        return input.getMouseHeldPosition();
    }

    public boolean isMousePressed() {
        return input.isMousePressed();
    }

    public boolean isMouseClicked() {
        return input.isMouseClicked();
    }

    public boolean isMouseDragged() {
        return input.isMouseDragged();
    }

    public int getMouseButton() {
        return input.getMouseButton();
    }

    public boolean isKeyPressed(int keyCode) {
        return input.isKeyPressed(keyCode);
    }

    public boolean isKeyClicked(int keyCode) {
        return input.isKeyClicked(keyCode);
    }

    private class Panel extends JPanel implements MouseListener, MouseMotionListener, KeyListener {

        public Panel(int width, int height) {
            super(true);
            setPreferredSize(new Dimension(width, height));
            addMouseListener(this);
            addMouseMotionListener(this);
            addKeyListener(this);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            render(g);
            requestFocusInWindow();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            input.setMouseClicked(true);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (!input.isMousePressed()) {
                input.setMouseHeldPosition(getMousePosition());
            }
            input.setMousePressed(true);
            input.setMouseButton(e.getButton());
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            input.setMousePressed(false);
            input.setMouseClicked(false);
            input.setMouseDragged(false);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            input.setMousePosition(getMousePosition());
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            input.setMousePosition(getMousePosition());
            input.setMouseDragged(true);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            input.setMousePosition(getMousePosition());
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() > 255) {
                return;
            }
            input.setKeyPressed(e.getKeyCode(), true);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() > 255) {
                return;
            }
            input.setKeyPressed(e.getKeyCode(), false);
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

    }

    private class Input {

        private Point mousePosition = new Point(0, 0);
        private Point mouseHeldPosition = new Point(0, 0);

        private boolean mousePressed = false;
        private boolean mouseClicked = false;
        private boolean mouseDragged = false;

        private int mouseButton = 0;

        private final Key[] keys = new Key[256];

        private Input() {
            for (int i = 0; i < keys.length; i++) {
                keys[i] = new Key();
            }
        }

        private Point getMousePosition() {
            return mousePosition;
        }

        private void setMousePosition(Point mouse) {
            this.mousePosition = mouse;
        }

        private Point getMouseHeldPosition() {
            return mouseHeldPosition;
        }

        private void setMouseHeldPosition(Point mouseHeldPosition) {
            this.mouseHeldPosition = mouseHeldPosition;
        }

        private boolean isMousePressed() {
            return mousePressed;
        }

        private void setMousePressed(boolean mousepressed) {
            this.mousePressed = mousepressed;
        }

        private boolean isMouseClicked() {
            if (mouseClicked) {
                setMouseClicked(false);
                return true;
            }
            return false;
        }

        private void setMouseClicked(boolean mouseClicked) {
            this.mouseClicked = mouseClicked;
        }

        private boolean isMouseDragged() {
            return mouseDragged;
        }

        private void setMouseDragged(boolean mouseDragged) {
            this.mouseDragged = mouseDragged;
        }

        private int getMouseButton() {
            return mouseButton;
        }

        private void setMouseButton(int mouseButton) {
            this.mouseButton = mouseButton;
        }

        private void setKeyPressed(int keyCode, boolean bool) {
            keys[keyCode].pressed = bool;
            keys[keyCode].clicked = bool;
        }

        private boolean isKeyPressed(int keyCode) {
            return keys[keyCode].pressed;
        }

        private boolean isKeyClicked(int keyCode) {
            if (keys[keyCode].clicked) {
                keys[keyCode].clicked = false;
                return true;
            } else {
                return false;
            }
        }

        private class Key {

            public boolean pressed;
            public boolean clicked;

            public Key() {
                this.pressed = false;
                this.clicked = false;
            }

        }

    }

}