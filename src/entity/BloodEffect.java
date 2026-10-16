package entity;

import main.GamePanel;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public class BloodEffect extends Entity {
    GamePanel gp;
    private BufferedImage[] frames;
    private int spriteCounter = 0;
    private int spriteNum = 0;
    private int totalFrames = 5; // Ajusta según la cantidad de fotogramas de tu efecto de sangre
    public boolean active = true;

    public BloodEffect(GamePanel gp, int worldX, int worldY, String tipoAtaque) {
        this.gp = gp;
        this.worldX = worldX;
        this.worldY = worldY;
        loadFrames();
    }

    private void loadFrames() {
        try {
            frames = new BufferedImage[totalFrames];
            for (int i = 0; i < totalFrames; i++) {
                // Ruta estándar donde se encuentran los frames del efecto de sangre
                String path = "/efectos/sangre/sangre_" + (i + 1) + ".png";
                frames[i] = ImageIO.read(getClass().getResourceAsStream(path));
            }
        } catch (Exception e) {
            // Si no encuentra los archivos específicos, el arreglo maneja la excepción de forma segura
        }
    }


    public void update() {
        if (!active) return;

        spriteCounter++;
        if (spriteCounter > 6) { // Velocidad de la animación del efecto
            spriteNum++;
            if (frames == null || spriteNum >= totalFrames || (frames.length > 0 && frames[spriteNum] == null)) {
                active = false;
                // Se remueve automáticamente de la lista de objetos del juego al terminar
                if (gp.objetosDelJuego != null) {
                    gp.objetosDelJuego.remove(this);
                }
            }
            spriteCounter = 0;
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!active || frames == null || spriteNum >= totalFrames || frames[spriteNum] == null) return;

        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        g2.drawImage(frames[spriteNum], screenX, screenY, gp.tileSize, gp.tileSize, null);
    }
}
