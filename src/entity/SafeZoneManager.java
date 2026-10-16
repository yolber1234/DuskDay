package entity;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class SafeZoneManager {

    public static ArrayList<Rectangle> safeZones = new ArrayList<>();

    private static final int RADIO_SEGURO_TILES = 5;
    private static final int TILE_SIZE = 16;

    public static void cargarRefugios(List<?> listaEntidades) {
        safeZones.clear();
        if (listaEntidades == null) return;

        for (Object obj : listaEntidades) {
            if (obj instanceof MapObject) {
                MapObject mapObj = (MapObject) obj;

                if ("refugio".equals(mapObj.type)) {
                    int padding = RADIO_SEGURO_TILES * TILE_SIZE;

                    Rectangle zonaSegura = new Rectangle(
                            mapObj.worldX - padding,
                            mapObj.worldY - padding,
                            mapObj.width + (padding * 2),
                            mapObj.height + (padding * 2)
                    );

                    safeZones.add(zonaSegura);
                }
            }
        }
    }

    public static boolean esJugadorEnZonaSegura(int playerX, int playerY) {
        for (Rectangle zona : safeZones) {
            if (zona.contains(playerX, playerY)) {
                return true;
            }
        }
        return false;
    }
}