package entity;

import java.util.List;

public class MissionManager {
    // Misión 1 (Map02): Desbloquear baldosa
    public static int enemigosDerrotadosBaldosa = 0;
    public static final int META_BALDOSA = 15;
    public static boolean baldosaDesbloqueada = false;

    // Misión 2 (Map01): Abrir portal
    public static int enemigosDerrotadosPortal = 0;
    public static final int META_PORTAL = 8;
    public static boolean portalAbierto = false;

    public static void checkBaldosaCompletion(List<?> objetos) {
        if (!baldosaDesbloqueada && enemigosDerrotadosBaldosa >= META_BALDOSA) {
            baldosaDesbloqueada = true;
            for (Object obj : objetos) {
                if (obj instanceof MapObject) {
                    MapObject m = (MapObject) obj;
                    if ("var".equals(m.type)) m.name = "suelo";
                }
            }
        }
    }
}