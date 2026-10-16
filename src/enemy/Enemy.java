package enemy;

import main.GamePanel;
import entity.Entity;
import java.awt.*;
import entity.BloodEffect;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Enemy extends Entity {
    GamePanel gp;
    public int nivel;
    public boolean isAggressive = false;
    public boolean isAttacking = false;
    protected int actionLockCounter = 0;
    protected int attackDurationCounter = 0;

    public int maxLife;
    public int life;
    public int attack = 1;

    public boolean isDead = false;
    private int respawnTimer = 0;
    private final int respawnDelay = 4800;

    public int deathTimer = 0;

    public Enemy(GamePanel gp, int worldX, int worldY, int nivel) {
        this.gp = gp;
        this.worldX = worldX;
        this.worldY = worldY;
        this.nivel = nivel;
        direction = "down";
        solidArea = new Rectangle(8, 16, 32, 32);
    }

    public void respawnAleatorio() {
        Random random = new Random();
        boolean posicionEncontrada = false;
        int intentos = 0;

        while (!posicionEncontrada && intentos < 500) {
            int randomCol = random.nextInt(gp.maxWorldCol);
            int randomRow = random.nextInt(gp.maxWorldRow);

            if (gp.tileM.mapTileNum[randomCol][randomRow] <= 1) {
                int targetX = randomCol * gp.tileSize;
                int targetY = randomRow * gp.tileSize;

                double distancia = Math.sqrt(Math.pow(targetX - gp.player.worldX, 2) + Math.pow(targetY - gp.player.worldY, 2)) / gp.tileSize;

                if (distancia > 5.0) {
                    worldX = targetX;
                    worldY = targetY;
                    posicionEncontrada = true;
                }
            }
            intentos++;
        }

        if (!posicionEncontrada) {
            worldX = 25 * gp.tileSize;
            worldY = 20 * gp.tileSize;
        }

        this.life = this.maxLife;
        this.isDead = false;
        this.respawnTimer = 0;
        this.deathTimer = 0;
    }

    public void gestionarIA() {
        if (gp.player == null) return;

        if (life <= 0) {
            isDead = true;
            isAggressive = false;
            isAttacking = false;
            respawnTimer++;
            if (respawnTimer >= respawnDelay) {
                respawnAleatorio();
            }
            return;
        }

        int distanciaX = Math.abs(worldX - gp.player.worldX);
        int distanciaY = Math.abs(worldY - gp.player.worldY);
        double distanciaTiles = Math.sqrt(Math.pow(distanciaX, 2) + Math.pow(distanciaY, 2)) / gp.tileSize;

        if (distanciaTiles <= 15.0) {
            isAggressive = true;

            if (distanciaTiles <= 1.5) {
                isAttacking = true;
                attackDurationCounter++;
                if (attackDurationCounter > 60) {
                    isAttacking = false;
                    attackDurationCounter = 0;
                }
                // VERIFICACIÓN DE ZONA SEGURA: Solo hace daño si la zona segura ha terminado (timer == 0)
                if (isAttacking && attackDurationCounter == 30) {
                    if (gp.zonaSeguraTimer == 0) {
                        gp.player.damagePlayer(this.attack, "enemy");
                    }
                }
            } else {
                isAttacking = false;

                String primaryDir = "";
                String secondaryDir = "";

                if (distanciaX > distanciaY) {
                    primaryDir = (worldX > gp.player.worldX) ? "left" : "right";
                    secondaryDir = (worldY > gp.player.worldY) ? "up" : "down";
                } else {
                    primaryDir = (worldY > gp.player.worldY) ? "up" : "down";
                    secondaryDir = (worldX > gp.player.worldX) ? "left" : "right";
                }

                direction = primaryDir;
                collisionOn = false;
                verificarColisionSegunMapa();

                if (collisionOn) {
                    direction = secondaryDir;
                    collisionOn = false;
                    verificarColisionSegunMapa();

                    if (collisionOn) {
                        direction = primaryDir.equals("left") ? "right" : (primaryDir.equals("right") ? "left" : (primaryDir.equals("up") ? "down" : "up"));
                        collisionOn = false;
                        verificarColisionSegunMapa();
                    }
                }

                if (!collisionOn) {
                    if (direction.equals("up")) worldY -= speed;
                    else if (direction.equals("down")) worldY += speed;
                    else if (direction.equals("left")) worldX -= speed;
                    else if (direction.equals("right")) worldX += speed;
                }
            }
        } else {
            isAggressive = false;
            isAttacking = false;
            actionLockCounter++;
            if (actionLockCounter > 100) {
                int i = new Random().nextInt(4) + 1;
                if (i == 1) direction = "up"; else if (i == 2) direction = "down";
                else if (i == 3) direction = "left"; else direction = "right";
                actionLockCounter = 0;
            }
            collisionOn = false;
            verificarColisionSegunMapa();

            if (!collisionOn) {
                if (direction.equals("up")) worldY -= speed;
                else if (direction.equals("down")) worldY += speed;
                else if (direction.equals("left")) worldX -= speed;
                else if (direction.equals("right")) worldX += speed;
            }
        }
    }

    private void verificarColisionSegunMapa() {
        if (this instanceof enemy.Musgo) {
            collisionOn = false;
            return;
        }

        if (gp.mapaActual != null && gp.mapaActual.equals("map02")) {
            if (gp.enemyCollisionManager != null) {
                gp.enemyCollisionManager.checkEnemyCustomCollision(this);
            }
        } else {
            if (gp.cChecker != null) {
                gp.cChecker.checkTile(this);
            }
        }

        if (gp.objetosDelJuego != null) {
            for (Entity obj : gp.objetosDelJuego) {
                if (obj != null && !(obj instanceof Enemy) && !(obj instanceof BloodEffect)) {
                    Rectangle enemyBox = new Rectangle(worldX + solidArea.x, worldY + solidArea.y, solidArea.width, solidArea.height);
                    Rectangle objBox = new Rectangle(obj.worldX, obj.worldY, gp.tileSize, gp.tileSize);
                    if (enemyBox.intersects(objBox)) {
                        collisionOn = true;
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;

        if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
                worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {

            switch (direction) {
                case "up": image = (spriteNum == 1) ? up1 : up2; break;
                case "down": image = (spriteNum == 1) ? down1 : down2; break;
                case "left": image = (spriteNum == 1) ? left1 : left2; break;
                case "right": image = (spriteNum == 1) ? right1 : right2; break;
            }

            if (image == null) image = (down1 != null) ? down1 : up1;
            if (image != null) g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        }
    }
}