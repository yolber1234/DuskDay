package entity;

import main.GamePanel;
import javax.swing.*;
import java.awt.*;

public class ShieldEffect {
    GamePanel gp;
    public int worldX, worldY;
    public boolean active = false;
    private Image shieldImage;

    public ShieldEffect(GamePanel gp) {
        this.gp = gp;
        loadShieldImage();
    }

    private void loadShieldImage() {
        try {
            // Ruta absoluta basada en la estructura de paquetes/recursos
            String path = "/player/otro/escudo.gif";
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            shieldImage = icon.getImage();
        } catch (Exception e) {
            shieldImage = null;
            e.printStackTrace();
        }
    }

    public void update(int playerWorldX, int playerWorldY, boolean isShieldPressed) {
        this.active = isShieldPressed;
        if (!active) return;

        this.worldX = playerWorldX;
        this.worldY = playerWorldY;
    }

    public void draw(Graphics2D g2, String direction) {
        if (!active || shieldImage == null) return;

        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        // Posicionamiento lateral según la dirección en la que mira el jugador
        int offset = gp.tileSize / 2;
        switch (direction) {
            case "up":    screenY -= offset; break;
            case "down":  screenY += offset; break;
            case "left":  screenX -= offset; break;
            case "right": screenX += offset; break;
        }

        g2.drawImage(shieldImage, screenX, screenY, gp.tileSize, gp.tileSize, gp);
    }
}