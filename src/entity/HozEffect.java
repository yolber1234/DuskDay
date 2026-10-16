package entity;

import main.GamePanel;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import enemy.Enemy;

public class HozEffect {
    GamePanel gp;
    public int worldX, worldY;
    public boolean active = false;
    private Image hozAttackImage;
    private int counter = 0;
    private String direction = "down";

    public HozEffect(GamePanel gp) {
        this.gp = gp;
        loadHozGif();
    }

    private void loadHozGif() {
        try {
            String path = "/efectos/EfectoHoz/naranja ultra.gif";
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            hozAttackImage = icon.getImage();
        } catch (Exception e) {
            hozAttackImage = null;
            e.printStackTrace();
        }
    }

    public void startHozAttack(int playerWorldX, int playerWorldY, String dir) {
        this.direction = (dir != null) ? dir : "down";
        this.active = true;
        this.counter = 0;

        this.worldX = playerWorldX;
        this.worldY = playerWorldY;

        int distanciaFrente = (int)(gp.tileSize * 1.0);
        switch (direction) {
            case "up": this.worldY -= distanciaFrente; break;
            case "down": this.worldY += distanciaFrente; break;
            case "left": this.worldX -= distanciaFrente; break;
            case "right": this.worldX += distanciaFrente; break;
        }
    }

    public void update() {
        if (!active) return;
        counter++;
        if (counter > 30) {
            active = false;
            counter = 0;
        }
    }

    public void verificarDañoEnemigos(ArrayList<Entity> listaEntidades) {
        if (!active) return;
        int radioImpacto = (int) (gp.tileSize * 1.5); // Solucionado con casteo a int
        for (Entity entity : listaEntidades) {
            if (entity instanceof Enemy) {
                Enemy enemy = (Enemy) entity;
                if (enemy.isDead) continue;
                int distanciaX = Math.abs(worldX - enemy.worldX);
                int distanciaY = Math.abs(worldY - enemy.worldY);
                if (distanciaX < radioImpacto && distanciaY < radioImpacto) {
                    enemy.life -= 4;
                    if (enemy.life <= 0) enemy.isDead = true;
                }
            }
        }
    }

    public void draw(Graphics2D g2) {
        if (!active || hozAttackImage == null) return;
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;
        int size = gp.tileSize * 2;

        g2.drawImage(hozAttackImage, screenX, screenY, size, size, gp);
    }
}
