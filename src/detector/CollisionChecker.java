package detector;

import entity.Entity;
import main.GamePanel;
import java.awt.Rectangle;

public class CollisionChecker {
    GamePanel gp;

    public CollisionChecker(GamePanel gp) {
        this.gp = gp;
    }

    public void checkTile(Entity entity) {
        int nextX = entity.worldX;
        int nextY = entity.worldY;

        // Calculamos cuál será el próximo paso del jugador
        switch (entity.direction) {
            case "up":    nextY -= entity.speed; break;
            case "down":  nextY += entity.speed; break;
            case "left":  nextX -= entity.speed; break;
            case "right": nextX += entity.speed; break;
        }

        // ------------------------------------------------------------------
        // 1. COLISIÓN ABSOLUTA DEL PERÍMETRO (Muro invisible impenetrable)
        // Fijado a 30 de ancho y 20 de alto para el mapa 1.
        // ------------------------------------------------------------------
        int mapMaxWidth = 30 * gp.tileSize;
        int mapMaxHeight = 20 * gp.tileSize;

        if (nextX + entity.solidArea.x < 0 ||
                nextX + entity.solidArea.x + entity.solidArea.width > mapMaxWidth ||
                nextY + entity.solidArea.y < 0 ||
                nextY + entity.solidArea.y + entity.solidArea.height > mapMaxHeight) {

            entity.collisionOn = true;
            return; // Bloqueo instantáneo y absoluto, no analiza nada más.
        }

        // ------------------------------------------------------------------
        // 2. Verificación de colisión contra los TILES fijos del mapa
        // ------------------------------------------------------------------
        int entityLeftCol = (nextX + entity.solidArea.x) / gp.tileSize;
        int entityRightCol = (nextX + entity.solidArea.x + entity.solidArea.width) / gp.tileSize;
        int entityTopRow = (nextY + entity.solidArea.y) / gp.tileSize;
        int entityBottomRow = (nextY + entity.solidArea.y + entity.solidArea.height) / gp.tileSize;

        if (entityLeftCol >= 0 && entityRightCol < gp.maxWorldCol && entityTopRow >= 0 && entityBottomRow < gp.maxWorldRow) {
            int tileNum1, tileNum2;
            try {
                switch (entity.direction) {
                    case "up":
                        tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                        tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                        if (gp.tileM.tile[tileNum1].colission || gp.tileM.tile[tileNum2].colission) entity.collisionOn = true;
                        break;
                    case "down":
                        tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                        tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                        if (gp.tileM.tile[tileNum1].colission || gp.tileM.tile[tileNum2].colission) entity.collisionOn = true;
                        break;
                    case "left":
                        tileNum1 = gp.tileM.mapTileNum[entityLeftCol][entityTopRow];
                        tileNum2 = gp.tileM.mapTileNum[entityLeftCol][entityBottomRow];
                        if (gp.tileM.tile[tileNum1].colission || gp.tileM.tile[tileNum2].colission) entity.collisionOn = true;
                        break;
                    case "right":
                        tileNum1 = gp.tileM.mapTileNum[entityRightCol][entityTopRow];
                        tileNum2 = gp.tileM.mapTileNum[entityRightCol][entityBottomRow];
                        if (gp.tileM.tile[tileNum1].colission || gp.tileM.tile[tileNum2].colission) entity.collisionOn = true;
                        break;
                }
            } catch (Exception e) {
                entity.collisionOn = false; // Prevención de caídas por índices
            }
        } else {
            entity.collisionOn = true;
        }

        // ------------------------------------------------------------------
        // 3. Verificación de colisión contra OBJETOS de Tiled ("arbolCol" y "col")
        // ------------------------------------------------------------------
        if (!entity.collisionOn) {
            Rectangle playerRect = new Rectangle(
                    nextX + entity.solidArea.x,
                    nextY + entity.solidArea.y,
                    entity.solidArea.width,
                    entity.solidArea.height
            );

            for (Entity obj : gp.objetosDelJuego) {
                // Evaluamos solo los objetos que tienen nombre en Tiled
                if (obj != null && obj.name != null) {
                    String nombreObjeto = obj.name.toLowerCase();

                    // Bloquea el paso SOLAMENTE si se llama "arbolcol" o "col"
                    if (nombreObjeto.equals("arbolcol") || nombreObjeto.equals("col")) {

                        Rectangle objRect = new Rectangle(
                                obj.worldX + obj.solidArea.x,
                                obj.worldY + obj.solidArea.y,
                                obj.solidArea.width,
                                obj.solidArea.height
                        );

                        // Si los rectángulos chocan, activa la colisión
                        if (playerRect.intersects(objRect)) {
                            entity.collisionOn = true;
                            break;
                        }
                    }
                }
            }
        }
    }
}