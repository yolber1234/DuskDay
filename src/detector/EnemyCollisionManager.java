package detector;

import entity.Entity;
import main.GamePanel;

public class EnemyCollisionManager {
    GamePanel gp;

    public EnemyCollisionManager(GamePanel gp) {
        this.gp = gp;
    }

    public void checkEnemyCustomCollision(Entity entity) {
        int nextX = entity.worldX;
        int nextY = entity.worldY;

        // Calculamos hacia dónde se va a mover el enemigo
        switch (entity.direction) {
            case "up":    nextY -= entity.speed; break;
            case "down":  nextY += entity.speed; break;
            case "left":  nextX -= entity.speed; break;
            case "right": nextX += entity.speed; break;
        }

        // ------------------------------------------------------------------
        // COLISIÓN ABSOLUTA DEL PERÍMETRO PARA ENEMIGOS (Adaptable a cualquier mapa)
        // ------------------------------------------------------------------
        int mapMaxWidth = gp.maxWorldCol * gp.tileSize;
        int mapMaxHeight = gp.maxWorldRow * gp.tileSize;

        // Evaluamos si el enemigo intenta salir del límite del mapa
        if (nextX + entity.solidArea.x < 0 ||
                nextX + entity.solidArea.x + entity.solidArea.width > mapMaxWidth ||
                nextY + entity.solidArea.y < 0 ||
                nextY + entity.solidArea.y + entity.solidArea.height > mapMaxHeight) {

            // Si intenta salirse del mapa, choca contra el muro invisible
            entity.collisionOn = true;
        } else {
            // Mientras esté dentro del marco del mapa, el enemigo tiene vía libre
            // (Ignorará las colisiones internas como árboles o techos para no quedarse atascado)
            entity.collisionOn = false;
        }
    }
}