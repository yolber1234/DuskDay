package enemy;

import main.GamePanel;
import javax.imageio.ImageIO;
import java.io.IOException;

public class HumanoInfectado2 extends Enemy {

    public HumanoInfectado2(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, 3);
        speed = 3;
        maxLife = 20;
        life = maxLife;
        attack = 6;
        getInfectado2Image();
    }

    public void getInfectado2Image() {
        try {
            if (getClass().getResourceAsStream("/enemy/HumanoInfectado2.png") != null) {
                down1 = ImageIO.read(getClass().getResourceAsStream("/enemy/HumanoInfectado2.png"));
            } else if (getClass().getResourceAsStream("/enemy/Humanolnfectado2.png") != null) {
                down1 = ImageIO.read(getClass().getResourceAsStream("/enemy/Humanolnfectado2.png"));
            } else if (getClass().getResourceAsStream("/enemy/HumanoInfectado.png") != null) {
                down1 = ImageIO.read(getClass().getResourceAsStream("/enemy/HumanoInfectado.png"));
            } else if (getClass().getResourceAsStream("/enemy/Humanolnfectado.png") != null) {
                down1 = ImageIO.read(getClass().getResourceAsStream("/enemy/Humanolnfectado.png"));
            } else {
                down1 = ImageIO.read(getClass().getResourceAsStream("/enemy/Musgo.png"));
            }

            down2 = down1;
            up1 = down1;
            up2 = down2;
            left1 = down1;
            left2 = down2;
            right1 = down1;
            right2 = down2;
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        // --- PERSECUCIÓN IMPLACABLE (SIN LÍMITE DE DISTANCIA) ---
        if (gp.player != null) {
            int distanciaX = worldX - gp.player.worldX;
            int distanciaY = worldY - gp.player.worldY;

            // Al no haber rango de detección, siempre evalúa dónde está el jugador
            if (Math.abs(distanciaX) > Math.abs(distanciaY)) {
                if (distanciaX < 0) direction = "right";
                else direction = "left";
            } else {
                if (distanciaY < 0) direction = "down";
                else direction = "up";
            }
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
        if (spriteCounter > 8) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }
}