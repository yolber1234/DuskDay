package entity;

import main.GamePanel;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import enemy.Enemy;

public class PatadaEffect {
    GamePanel gp;
    public int worldX, worldY;
    public boolean active = false;
    private Image patadaImage;
    private int counter = 0;
    private String direction = "down";

    public PatadaEffect(GamePanel gp) {
        this.gp = gp;
        loadPatadaImage();
    }

    private void loadPatadaImage() {
        try {
            String path = "/player/otro/patada.png";
            ImageIcon icon = new ImageIcon(getClass().getResource(path));
            patadaImage = icon.getImage();
        } catch (Exception e) {
            patadaImage = null;
            e.printStackTrace();
        }
    }

    public void startPatada(int playerWorldX, int playerWorldY, String dir) {
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
        if (counter > 20) {
            active = false;
            counter = 0;
        }
    }

    public void verificarDañoEnemigos(ArrayList<Entity> listaEntidades) {
        if (!active) return;
        int radioImpacto = (int) (gp.tileSize * 1.2);
        for (Entity entity : listaEntidades) {
            if (entity instanceof Enemy) {
                Enemy enemy = (Enemy) entity;
                if (enemy.isDead) continue;
                int distanciaX = Math.abs(worldX - enemy.worldX);
                int distanciaY = Math.abs(worldY - enemy.worldY);
                if (distanciaX < radioImpacto && distanciaY < radioImpacto) {
                    enemy.life -= 2;
                    if (enemy.life <= 0) enemy.isDead = true;
                }
            }
        }
    }

    public void draw(Graphics2D g2) {
        if (!active || patadaImage == null) return;
        int screenX = worldX - gp.player.worldX + gp.player.screenX;
        int screenY = worldY - gp.player.worldY + gp.player.screenY;
        int size = gp.tileSize;

        g2.drawImage(patadaImage, screenX, screenY, size, size, gp);
    }
}