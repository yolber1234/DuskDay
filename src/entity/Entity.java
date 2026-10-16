package entity;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Entity {

    public int worldX, worldY;
    public int speed;

    // --- VARIABLE PARA IDENTIFICAR OBJETOS DE TILED ---
    public String name = "";

    // --- NUEVO: CAJA DE COLISIÓN (HITBOX) Y ESTADO ---
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48); // Área por defecto de colisión (ajustable por entidad)
    public boolean collisionOn = false;

    // --- SISTEMA DE CAPAS (Z-INDEX) ---
    // 0 = Suelo (charcos, alfombras, marcas en el piso)
    // 1 = Entidades (Jugador, árboles, rocas, enemigos) - Orden dinámico
    // 2 = Techos (Copas de árboles gigantes, nubes, techos)
    public int zIndex = 1;

    public BufferedImage down1, down2, down3, left1, left2, left3, right1, right2, right3, up1, up2, up3;
    public String direction = "down";

    public int spriteCounter = 0;
    public int spriteNum = 1;

    public void draw(Graphics2D g2) {
        // Lógica de dibujado específica de cada entidad
    }

    // --- MÉTODO PARA OBTENER LA BASE DEL SPRITE ---
    public int getBottomY() {
        return worldY + 48; // Ajustado por defecto a la base del tile/sprite
    }

    public void update() {
    }
}