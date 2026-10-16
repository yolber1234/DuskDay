package enemy;

import main.GamePanel;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Musgo extends Enemy {

    public Musgo(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, 1);
        speed = 1;
        maxLife = 12; // 3 corazones completos equivalentes en la barra general (o vida interna)
        life = maxLife;
        attack = 2;
        getMusgoImage();
    }

    public void getMusgoImage() {
        try {
            down1 = ImageIO.read(getClass().getResourceAsStream("/enemy/Musgo.png"));
            down2 = down1; // Reutiliza la imagen para el segundo frame de animación
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
        gestionarIA();

        // Control de colisiones según el mapa actual (hereda la regla de ignorar colisiones en la clase Enemy)
        if (gp.mapaActual != null && gp.mapaActual.equals("map02")) {
            if (gp.enemyCollisionManager != null) {
                gp.enemyCollisionManager.checkEnemyCustomCollision(this);
            }
        } else {
            if (gp.cChecker != null) {
                gp.cChecker.checkTile(this);
            }
        }

        // Animación de sprites
        spriteCounter++;
        if (spriteCounter > 12) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }
    }
}