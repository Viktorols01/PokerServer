package client;

import java.awt.Color;
import java.awt.Graphics;

import poker.PokerModel;
import tools.GUI;

public class PokerGUI extends GUI {
    private PokerClient client;

    public PokerGUI(PokerClient client) {
        super(1200, 800, 60);
        this.client = client;
    }

    @Override
    protected void setup() {
    }

    @Override
    protected void update() {
    }

    @Override
    protected void render(Graphics g) {
        PokerModel model = client.getModel();
        if (model == null) {
            g.setColor(new Color(255, 0, 0));
            g.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g.setColor(new Color(0, 255, 0));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(0, 0, 0));
            g.drawString(model.getPlayers().toString(), getWidth() / 2, getHeight() / 2);
        }
    }
}
