package entity;

import main.GamePanel;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class MapObject extends Entity {

    GamePanel gp;
    public BufferedImage image;
    public int width, height;
    public String type; // Variable para almacenar el tipo (ej: "refugio", "var")

    // Constructor actualizado con 'String type'
    public MapObject(GamePanel gp, int x, int y, int width, int height, BufferedImage image, String name, String type) {
        this.gp = gp;
        this.worldX = x;
        this.worldY = y;
        this.width = width;
        this.height = height;
        this.image = image;
        this.type = (type != null) ? type : "";

        if (name != null) {
            this.name = name;
        } else {
            this.name = "";
        }

        this.solidArea = new Rectangle(0, 0, width, height);

        if (this.name.toLowerCase().equals("techo") || this.name.toLowerCase().equals("techos")) {
            this.zIndex = 2;
        } else {
            this.zIndex = 1;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (worldX + width > gp.player.worldX - gp.player.screenX &&
                worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                worldY + height > gp.player.worldY - gp.player.screenY &&
                worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {

            if (image != null) {
                g2.drawImage(image, screenX, screenY, width, height, null);
            }
        }
    }
}