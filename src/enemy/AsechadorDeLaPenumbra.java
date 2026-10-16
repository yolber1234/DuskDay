package enemy;

import main.GamePanel;
import entity.Portal;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class AsechadorDeLaPenumbra extends Enemy {

    public BufferedImage moveImage, attackImage1, attackImage2, attackImage3;
    private int attackCounter = 0;
    private boolean isAttackingAnim = false;

    public AsechadorDeLaPenumbra(GamePanel gp, int worldX, int worldY) {
        super(gp, worldX, worldY, 4); // Velocidad 4
        speed = 4;
        maxLife = 120; // 30 corazones * 4 puntos por corazón = 120 de vida total
        life = maxLife;
        attack = 8;

        getAsechadorImages();
        solidArea = new Rectangle(8, 16, 32, 32);
    }

    public void getAsechadorImages() {
        try {
            String path = "/enemy/asechador de la penumbra/";
            down1 = ImageIO.read(getClass().getResourceAsStream(path + "asechador de la penumbra defrente.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream(path + "asechador de la penumbra moverse.png"));
            moveImage = down2;
            attackImage1 = ImageIO.read(getClass().getResourceAsStream(path + "asechador de la penumbra ataque.png"));
            attackImage2 = ImageIO.read(getClass().getResourceAsStream(path + "asechador de la penumbra ataque 2.png"));
            attackImage3 = ImageIO.read(getClass().getResourceAsStream(path + "asechador de la penumbra ataque 3.png"));

            up1 = down1; up2 = down2; left1 = down1; left2 = down2; right1 = down1; right2 = down2;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        if (gp.player != null) {
            int distanciaX = worldX - gp.player.worldX;
            int distanciaY = worldY - gp.player.worldY;
            int distanciaAbsX = Math.abs(distanciaX);
            int distanciaAbsY = Math.abs(distanciaY);

            if (distanciaAbsX < gp.tileSize && distanciaAbsY < gp.tileSize) {
                isAttackingAnim = true;
                attackCounter++;
            } else {
                isAttackingAnim = false;
                if (distanciaAbsX > distanciaAbsY) {
                    if (distanciaX < 0) direction = "right";
                    else direction = "left";
                } else {
                    if (distanciaY < 0) direction = "down";
                    else direction = "up";
                }
            }
        }

        if (gp.cChecker != null) {
            gp.cChecker.checkTile(this);
        }

        spriteCounter++;
        if (spriteCounter > 12) {
            spriteNum = (spriteNum == 1) ? 2 : 1;
            spriteCounter = 0;
        }

        // Si es derrotado: otorga la medalla y despliega el portal de salida
        if (life <= 0 && !isDead) {
            isDead = true;
            gp.player.inventory.hasMedalla = true; // Se entrega la medalla para el inventario/HUD

            // Crea el portal de salida en el mapa 3 para regresar al mapa 2
            Portal portalSalida = new Portal(gp, worldX, worldY, "map02", "/portales/portal01.gif");
            gp.objetosDelJuego.add(portalSalida);
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        if (!isDead) {
            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY + gp.player.screenY;

            BufferedImage image = down1;
            if (isAttackingAnim) {
                if (attackCounter < 15) image = attackImage1;
                else if (attackCounter < 30) image = attackImage2;
                else { image = attackImage3; if (attackCounter > 45) attackCounter = 0; }
            } else if (spriteNum == 2) {
                image = moveImage;
            }

            g2.drawImage(image, screenX, screenY, gp.tileSize * 2, gp.tileSize * 2, null);

            // --- BARRA DE VIDA DEL JEFE (30 CORAZONES / 120 DE VIDA) ---
            int barWidth = 300;
            int barHeight = 16;
            int barX = (gp.screenWidth - barWidth) / 2;
            int barY = 40;

            g2.setColor(new Color(0, 0, 0, 200));
            g2.fillRoundRect(barX - 2, barY - 2, barWidth + 4, barHeight + 4, 8, 8);

            g2.setColor(Color.RED);
            g2.fillRect(barX, barY, barWidth, barHeight);

            g2.setColor(Color.GREEN);
            double vidaPorcentaje = (double) life / maxLife;
            g2.fillRect(barX, barY, (int)(barWidth * vidaPorcentaje), barHeight);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Serif", Font.BOLD, 12));
            g2.drawString("ASECHADOR DE LA PENUMBRA", barX + 60, barY - 5);
        }
    }
}