package enemy;

import main.GamePanel;
import javax.imageio.ImageIO;
import java.io.IOException;

public class HumanoInfectado extends Enemy {

    public HumanoInfectado(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, 2);
        speed = 2;
        maxLife = 16;
        life = maxLife;
        attack = 4;
        getInfectadoImage();
    }

    public void getInfectadoImage() {
        try {
            down1 = ImageIO.read(getClass().getResourceAsStream("/enemy/HumanoInfectado.png"));
            down2 = down1;
            up1 = down1;
            up2 = down2;
            left1 = down1;
            left2 = down2;
            right1 = down1;
            right2 = down2;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        // --- LÓGICA DE PERSECUCIÓN (10 CASILLAS) ---
        if (gp.player != null) {
            int distanciaX = Math.abs(worldX - gp.player.worldX);
            int distanciaY = Math.abs(worldY - gp.player.worldY);

            // Rango de detección de 10 tiles
            int rangoDeteccion = gp.tileSize * 10;

            if (distanciaX <= rangoDeteccion && distanciaY <= rangoDeteccion) {
                // Persecución directa hacia el jugador
                if (Math.abs(distanciaX) > Math.abs(distanciaY)) {
                    if (worldX < gp.player.worldX) direction = "right";
                    else direction = "left";
                } else {
                    if (worldY < gp.player.worldY) direction = "down";
                    else direction = "up";
                }
            } else {
                // Comportamiento normal fuera de rango
                gestionarIA();
            }
        } else {
            gestionarIA();
        }

        // Control de colisiones según el mapa actual
        if (gp.mapaActual != null && gp.mapaActual.equals("map02")) {
            if (gp.enemyCollisionManager != null) {
                gp.enemyCollisionManager.checkEnemyCustomCollision(this);
            }
        } else {
            if (gp.cChecker != null) {
                gp.cChecker.checkTile(this);
            }
        }

        spriteCounter++;
        if (spriteCounter > 10) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }
}