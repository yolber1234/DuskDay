package entity;

import main.GamePanel;
import javax.swing.ImageIcon;
import java.awt.*;

public class Portal extends Entity {
    GamePanel gp;
    public String targetMap;
    public Image portalGif;

    public Portal(GamePanel gp, int worldX, int worldY, String targetMap, String gifPath) {
        this.gp = gp;
        this.worldX = worldX;
        this.worldY = worldY;
        this.targetMap = targetMap;

        try {
            portalGif = new ImageIcon(getClass().getResource(gifPath)).getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        int margen = gp.tileSize * 2;
        int portalSize = gp.tileSize * 2;

        if (worldX + portalSize > gp.player.worldX - gp.player.screenX - margen &&
                worldX < gp.player.worldX + gp.player.screenX + margen &&
                worldY + portalSize > gp.player.worldY - gp.player.screenY - margen &&
                worldY < gp.player.worldY + gp.player.screenY + margen) {

            if (portalGif != null) {
                // AQUÍ ESTÁ LA MAGIA: Comentamos esta línea para que sea INVISIBLE
                // g2.drawImage(portalGif, screenX, screenY, portalSize, portalSize, null);
            }
        }
    }
}