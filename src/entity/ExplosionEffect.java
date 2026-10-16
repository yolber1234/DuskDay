package entity;

import main.GamePanel;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import enemy.Enemy;

public class ExplosionEffect {
    GamePanel gp;
    public int worldX, worldY;
    public boolean active = false;
    private Image explosionImage;
    private int counter = 0;
    private int explosionType = 2; // Tipos válidos: 2 (I), 6 (O) o 9 (P)

    public ExplosionEffect(GamePanel gp) {
        this.gp = gp;
    }

    public void startExplosion(int playerWorldX, int playerWorldY, int type, String dir) {
        this.explosionType = type;
        String direction = (dir != null) ? dir : "down";
        this.active = true;
        this.counter = 0;

        this.worldX = playerWorldX;
        this.worldY = playerWorldY;

        int distanciaFrente = (int)(gp.tileSize * 1.2);
        switch (direction) {
            case "up": this.worldY -= distanciaFrente; break;
            case "down": this.worldY += distanciaFrente; break;
            case "left": this.worldX -= distanciaFrente; break;
            case "right": this.worldX += distanciaFrente; break;
        }

        loadExplosionGif();
    }

    private void loadExplosionGif() {
        try {
            // Carga directa del GIF según la tecla presionada (I=2, O=6, P=9)
            String path = "/efectos/explosion/explosion_" + explosionType + ".gif";
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            explosionImage = icon.getImage();
        } catch (Exception e) {
            explosionImage = null;
            e.printStackTrace();
        }
    }

    public void update() {
        if (!active) return;
        counter++;
        if (counter > 40) {
            active = false;
            counter = 0;
        }
    }

    public void verificarDañoEnemigos(ArrayList<Entity> listaEntidades) {
        if (!active) return;
        int radioImpacto = gp.tileSize * 2;
        for (Entity entity : listaEntidades) {
            if (entity instanceof Enemy) {
                Enemy enemy = (Enemy) entity;
                if (enemy.isDead) continue;
                int distanciaX = Math.abs(worldX - enemy.worldX);
                int distanciaY = Math.abs(worldY - enemy.worldY);
                if (distanciaX < radioImpacto && distanciaY < radioImpacto) {
                    enemy.life -= explosionType;
                    if (enemy.life <= 0) enemy.isDead = true;
                }
            }
        }
    }

    public void draw(Graphics2D g2) {
        if (!active || explosionImage == null) return;
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;
        int size = gp.tileSize * 3;

        g2.drawImage(explosionImage, screenX - gp.tileSize, screenY - gp.tileSize, size, size, gp);
    }
}