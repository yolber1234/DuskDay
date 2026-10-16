package entity;

import main.GamePanel;
import main.KeyHandler;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Player extends Entity {

    GamePanel gp;
    KeyHandler keyH;

    public final int screenX;
    public final int screenY;

    // Instancia del inventario para centralizar llaves, medalla, hoz y escudo
    public Inventory inventory = new Inventory();

    public boolean isAttacking = false;
    public boolean isEquippedHoz = false;
    private boolean fKeyProcessed = false;

    // Propiedades requeridas por el HUD de GamePanel para la opacidad visual
    public boolean hozEquipada = false;
    public boolean escudoActivo = false;

    public HozEffect hozEffect;
    public ExplosionEffect currentExplosion;
    public ShieldEffect shieldEffect;
    public PatadaEffect patadaEffect;
    public PunoEffect punoEffect;
    public BufferedImage hozMiniImage;

    public boolean invincible = false;
    public int invincibleCounter = 0;
    public int teleportLock = 0;

    private int regeneracionCounter = 0;

    public Player(GamePanel gp, KeyHandler keyH) {
        this.gp = gp;
        this.keyH = keyH;

        screenX = gp.screenWidth / 2 - (gp.tileSize / 2);
        screenY = gp.screenHeight / 2 - (gp.tileSize / 2);

        setDefaultValues();
        getPlayerImage();
        getHozImage();

        hozEffect = new HozEffect(gp);
        currentExplosion = new ExplosionEffect(gp);
        shieldEffect = new ShieldEffect(gp);
        patadaEffect = new PatadaEffect(gp);
        punoEffect = new PunoEffect(gp);

        solidArea = new Rectangle(8, 16, 32, 32);
    }

    public void setDefaultValues() {
        worldX = gp.tileSize * 18;
        worldY = gp.tileSize * 16;
        speed = 4;
        direction = "down";
    }

    public void getPlayerImage() {
        try {
            up1 = ImageIO.read(getClass().getResourceAsStream("/player/jugadorDetras.png"));
            up2 = ImageIO.read(getClass().getResourceAsStream("/player/jugadorDetras2.png"));
            up3 = ImageIO.read(getClass().getResourceAsStream("/player/jugadorDetras3.png"));
            down1 = ImageIO.read(getClass().getResourceAsStream("/player/jugadorFrontal2.png"));
            down2 = ImageIO.read(getClass().getResourceAsStream("/player/jugadorFrontal3.png"));
            down3 = ImageIO.read(getClass().getResourceAsStream("/player/jugadorFrontal4.png"));
            left1 = ImageIO.read(getClass().getResourceAsStream("/player/jugadorLateralIzq.png"));
            left2 = ImageIO.read(getClass().getResourceAsStream("/player/jugadorLateralIzq2.png"));
            left3 = ImageIO.read(getClass().getResourceAsStream("/player/jugadorLateralIzq3.png"));
            right1 = ImageIO.read(getClass().getResourceAsStream("/player/jugadorLateralDer.png"));
            right2 = ImageIO.read(getClass().getResourceAsStream("/player/jugadorLateralDer2.png"));
            right3 = ImageIO.read(getClass().getResourceAsStream("/player/jugadorLateralDer3.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getHozImage() {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/efectos/EfectoHoz/hoz/hoz.png"));
            hozMiniImage = new BufferedImage(gp.tileSize, gp.tileSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = hozMiniImage.createGraphics();
            g2.drawImage(icon.getImage(), 0, 0, gp.tileSize, gp.tileSize, null);
            g2.dispose();
        } catch (Exception e) {
            hozMiniImage = null;
        }
    }

    public void damagePlayer(int damage, String tipoAtaque) {
        if (keyH.keyShield || (gp != null && gp.zonaSeguraTimer > 0)) return;

        if (!invincible) {
            gp.playerLife -= damage;
            invincible = true;
            if (gp.playerLife < 0) gp.playerLife = 0;
            if (gp.objetosDelJuego != null) gp.objetosDelJuego.add(new BloodEffect(gp, worldX, worldY, tipoAtaque));
        }
    }

    public void update() {
        if (gp.playerLife > 0 && gp.playerLife < gp.playerMaxLife) {
            regeneracionCounter++;
            if (regeneracionCounter >= 300) {
                gp.playerLife += 4;
                if (gp.playerLife > gp.playerMaxLife) gp.playerLife = gp.playerMaxLife;
                regeneracionCounter = 0;
            }
        } else { regeneracionCounter = 0; }

        if (invincible) {
            invincibleCounter++;
            if (invincibleCounter > 45) { invincible = false; invincibleCounter = 0; }
        }

        if (teleportLock > 0) teleportLock--;

        if (hozEffect != null) hozEffect.update();
        if (currentExplosion != null) currentExplosion.update();
        if (patadaEffect != null) patadaEffect.update();
        if (punoEffect != null) punoEffect.update();
        if (shieldEffect != null) shieldEffect.update(worldX, worldY, keyH.keyShield);

        // Control de la Hoz y actualización de su estado en el HUD
        if (keyH.keyF) {
            if (!fKeyProcessed) {
                isEquippedHoz = !isEquippedHoz;
                inventory.hasHoz = isEquippedHoz;
                fKeyProcessed = true;
            }
        } else {
            fKeyProcessed = false;
        }

        // Sincronizamos las variables del HUD con las acciones reales del jugador
        hozEquipada = isEquippedHoz;
        escudoActivo = keyH.keyShield;

        // Control del Escudo y sincronización en el inventario
        inventory.hasShield = keyH.keyShield;

        if (isEquippedHoz && keyH.keyM && !hozEffect.active) {
            hozEffect.startHozAttack(worldX, worldY, direction);
            hozEffect.verificarDañoEnemigos(new ArrayList<>(gp.objetosDelJuego));
        }

        if (!hozEffect.active) {
            if (keyH.keyK && !patadaEffect.active) {
                patadaEffect.startPatada(worldX, worldY, direction);
                patadaEffect.verificarDañoEnemigos(new ArrayList<>(gp.objetosDelJuego));
                keyH.keyK = false;
            } else if (keyH.keyL && !punoEffect.active) {
                punoEffect.startPuno(worldX, worldY, direction);
                punoEffect.verificarDañoEnemigos(new ArrayList<>(gp.objetosDelJuego));
                keyH.keyL = false;
            }
        }

        if (!hozEffect.active) {
            int tipo = -1;
            if (keyH.keyI) { tipo = 2; keyH.keyI = false; }
            else if (keyH.keyO) { tipo = 6; keyH.keyO = false; }
            else if (keyH.keyP) { tipo = 9; keyH.keyP = false; }

            if (tipo != -1 && currentExplosion != null) {
                currentExplosion.startExplosion(worldX, worldY, tipo, direction);
                currentExplosion.verificarDañoEnemigos(new ArrayList<>(gp.objetosDelJuego));
            }
        }

        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {
            if (keyH.upPressed) direction = "up";
            else if (keyH.downPressed) direction = "down";
            else if (keyH.leftPressed) direction = "left";
            else if (keyH.rightPressed) direction = "right";

            collisionOn = false;

            if (gp.mapaActual.equals("map02")) {
                if (gp.cCheckerMap02 != null) gp.cCheckerMap02.checkTileForPlayer(this);
            } else {
                if (gp.cChecker != null) gp.cChecker.checkTile(this);
            }

            if (!collisionOn) {
                switch(direction) {
                    case "up": worldY -= speed; break;
                    case "down": worldY += speed; break;
                    case "left": worldX -= speed; break;
                    case "right": worldX += speed; break;
                }
            }
        }
    }

    public void draw(Graphics2D g2) {
        BufferedImage image = null;
        switch (direction) {
            case "up": image = (spriteNum == 1) ? up1 : (spriteNum == 2) ? up2 : up3; break;
            case "down": image = (spriteNum == 1) ? down1 : (spriteNum == 2) ? down2 : down3; break;
            case "left": image = (spriteNum == 1) ? left1 : (spriteNum == 2) ? left2 : left3; break;
            case "right": image = (spriteNum == 1) ? right1 : (spriteNum == 2) ? right2 : right3; break;
        }
        if (image == null) image = (down1 != null) ? down1 : up1;
        if (invincible) g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
        g2.drawImage(image, screenX, screenY, gp.tileSize, gp.tileSize, null);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        if (isEquippedHoz && hozMiniImage != null && !hozEffect.active) {
            g2.drawImage(hozMiniImage, (direction.equals("down")) ? screenX - (gp.tileSize/3) : screenX + gp.tileSize - (gp.tileSize/3),
                    (direction.equals("left") || direction.equals("right")) ? screenY + gp.tileSize - (gp.tileSize/3) : screenY + (gp.tileSize/4), gp.tileSize, gp.tileSize, null);
        }
        if (hozEffect != null) hozEffect.draw(g2);
        if (currentExplosion != null) currentExplosion.draw(g2);
        if (patadaEffect != null) patadaEffect.draw(g2);
        if (punoEffect != null) punoEffect.draw(g2);
        if (shieldEffect != null && keyH.keyShield) shieldEffect.draw(g2, direction);
    }
}