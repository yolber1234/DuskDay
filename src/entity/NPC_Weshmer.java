package entity;

import main.GamePanel;
import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.image.BufferedImage;

public class NPC_Weshmer extends Entity {
    GamePanel gp;
    public BufferedImage image;

    public NPC_Weshmer(GamePanel gp, int x, int y) {
        super(); // Llama al constructor por defecto de Entity
        this.gp = gp;
        this.worldX = x;
        this.worldY = y;
        this.solidArea = new Rectangle(0, 0, gp.tileSize * 2, gp.tileSize * 2);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/npc/Weshmer.png"));
            // Se duplica el tamaño para que sea más grande que el player (2x2 casillas)
            this.image = new BufferedImage(gp.tileSize * 2, gp.tileSize * 2, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = this.image.createGraphics();
            g2.drawImage(icon.getImage(), 0, 0, gp.tileSize * 2, gp.tileSize * 2, null);
            g2.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        int distanciaX = Math.abs(gp.player.worldX - worldX);
        int distanciaY = Math.abs(gp.player.worldY - worldY);

        // Rango de proximidad adaptado al nuevo tamaño
        if (distanciaX < gp.tileSize * 1.5 && distanciaY < gp.tileSize * 1.5) {
            if (gp.mapaActual.equals("map01") && !gp.player.inventory.hasLlave1) {
                gp.player.inventory.hasLlave1 = true;
                MissionManager.portalAbierto = true;
            } else if (gp.mapaActual.equals("map02") && !gp.player.inventory.hasLlave2) {
                gp.player.inventory.hasLlave2 = true;
                MissionManager.baldosaDesbloqueada = true;
            }
        }
    }

    public void draw(Graphics2D g2) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (image != null) {
            // Se dibuja ocupando el doble de espacio (tileSize * 2)
            g2.drawImage(image, screenX, screenY, gp.tileSize * 2, gp.tileSize * 2, null);
        }
    }
}