package tile;

import com.google.gson.Gson;
import main.GamePanel;
import entity.MapObject;
import entity.SafeZoneManager;
import java.util.List;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStreamReader;
import java.io.Reader;

public class TileManager {
    GamePanel gp;

    public Tile[] tile;
    public int mapTileNum[][];

    public TileManager(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[1200];

        // Inicializamos con un tamaño temporal, se ajustará al leer el JSON
        mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];

        // Cargamos las imágenes por defecto del mapa 1 al iniciar
        getTileImage("/tiles/WorldMap01/mapTile01 (2).png");
        loadMap();
    }

    public void getTileImage(String tileSetPath) {
        try {
            BufferedImage tileSet = ImageIO.read(getClass().getResourceAsStream(tileSetPath));

            int originalTileSize = 16;
            int columnas = tileSet.getWidth() / originalTileSize;
            int filas = tileSet.getHeight() / originalTileSize;
            int id = 1;

            for(int y = 0; y < filas; y++) {
                for(int x = 0; x < columnas; x++) {
                    if (id < tile.length) {
                        tile[id] = new Tile();
                        tile[id].image = tileSet.getSubimage(x * originalTileSize, y * originalTileSize, originalTileSize, originalTileSize);
                        tile[id].colission = false;
                    }
                    id++;
                }
            }

            // --- LÓGICA SEPARADA DE COLISIONES SEGÚN EL MAPA ---
            if (tileSetPath.contains("mapTile01")) {
                setupTileCollision(37, true); // Muros
                setupTileCollision(61, true); // Piedras

                for (int i = 40; i <= 55; i++) {
                    setupTileCollision(i, true); // Agua y otros obstáculos
                }
            } else if (tileSetPath.contains("MapTile02")) {
                // Configuración para mapa 02
            } else if (tileSetPath.contains("MapTile03")) {
                // Configuración para mapa 03
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setupTileCollision(int index, boolean collision) {
        if (index > 0 && index < tile.length && tile[index] != null) {
            tile[index].colission = collision;
        }
    }

    public void loadMap() {
        try {
            Reader reader = new InputStreamReader(getClass().getResourceAsStream("/maps/map01.json"));
            Gson gson = new Gson();
            TiledMapData mapData = gson.fromJson(reader, TiledMapData.class);

            // REDIMENSIÓN AUTOMÁTICA BASADA EN EL JSON
            if (mapData.width > 0 && mapData.height > 0) {
                gp.maxWorldCol = mapData.width;
                gp.maxWorldRow = mapData.height;
            }

            mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];

            procesarDatosDelMapa(mapData);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void procesarDatosDelMapa(TiledMapData mapData) {
        for (TiledLayer layer : mapData.layers) {
            // 1. CARGAR EL FONDO
            if ("tilelayer".equals(layer.type) && layer.data != null) {
                int col = 0;
                int row = 0;
                for (int tileID : layer.data) {
                    if (col < gp.maxWorldCol && row < gp.maxWorldRow) {
                        mapTileNum[col][row] = tileID;
                    }
                    col++;
                    if (col == gp.maxWorldCol) {
                        col = 0;
                        row++;
                    }
                }
            }
            // 2. CARGAR OBJETOS ESTÁTICOS
            else if ("objectgroup".equals(layer.type) && layer.objects != null) {
                int originalTileSize = 16;
                double escalaRatio = (double) gp.tileSize / originalTileSize;

                for (TiledObject obj : layer.objects) {
                    int objX = (int) Math.round(obj.x * escalaRatio);
                    int objY = (int) Math.round(obj.y * escalaRatio);
                    int objWidth = (int) Math.round(obj.width * escalaRatio);
                    int objHeight = (int) Math.round(obj.height * escalaRatio);

                    BufferedImage objImage = null;

                    if (obj.gid > 0) {
                        objY = objY - objHeight;
                        int realGid = obj.gid & 0x1FFFFFFF;
                        if (realGid < tile.length && tile[realGid] != null) {
                            objImage = tile[realGid].image;
                        }
                    }

                    MapObject nuevoObjeto = new MapObject(gp, objX, objY, objWidth, objHeight, objImage, obj.name, obj.type);
                    nuevoObjeto.type = obj.type;

                    gp.objetosDelJuego.add(nuevoObjeto);
                }

                SafeZoneManager.cargarRefugios(gp.objetosDelJuego);
            }
        }
    }

    public void cambiarMapa(String nombreArchivoJson) {
        try {
            // SELECCIÓN DE TILESET SEGÚN EL MAPA (SOPORTE PARA MAPA 3)
            String tileSetPath;
            if (nombreArchivoJson.equals("map03")) {
                tileSetPath = "/tiles/WorldMap03/MapTile03.png";
            } else if (nombreArchivoJson.equals("map02")) {
                tileSetPath = "/tiles/WorldMap02/MapTile02.png";
            } else {
                tileSetPath = "/tiles/WorldMap01/mapTile01 (2).png";
            }

            getTileImage(tileSetPath);

            String ruta = "/maps/" + nombreArchivoJson + ".json";
            Reader reader = new InputStreamReader(getClass().getResourceAsStream(ruta));
            Gson gson = new Gson();
            TiledMapData mapData = gson.fromJson(reader, TiledMapData.class);

            if (mapData.width > 0 && mapData.height > 0) {
                gp.maxWorldCol = mapData.width;
                gp.maxWorldRow = mapData.height;
            }
            mapTileNum = new int[gp.maxWorldCol][gp.maxWorldRow];

            gp.objetosDelJuego.clear();

            procesarDatosDelMapa(mapData);
            reader.close();

            if (gp.portalActual != null) {
                gp.objetosDelJuego.add(gp.portalActual);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        int worldCol = 0;
        int worldRow = 0;

        while (worldCol < gp.maxWorldCol && worldRow < gp.maxWorldRow) {
            int tileNum = mapTileNum[worldCol][worldRow];
            int worldX = worldCol * gp.tileSize;
            int worldY = worldRow * gp.tileSize;
            int screenX = worldX - gp.player.worldX + gp.player.screenX;
            int screenY = worldY - gp.player.worldY + gp.player.screenY;

            if (worldX + gp.tileSize > gp.player.worldX - gp.player.screenX &&
                    worldX - gp.tileSize < gp.player.worldX + gp.player.screenX &&
                    worldY + gp.tileSize > gp.player.worldY - gp.player.screenY &&
                    worldY - gp.tileSize < gp.player.worldY + gp.player.screenY) {

                if (tileNum > 0 && tileNum < tile.length && tile[tileNum] != null && tile[tileNum].image != null) {
                    g2.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
                }
                else if (tile[1] != null && tile[1].image != null) {
                    g2.drawImage(tile[1].image, screenX, screenY, gp.tileSize, gp.tileSize, null);
                }
            }

            worldCol++;
            if(worldCol == gp.maxWorldCol) {
                worldCol = 0;
                worldRow++;
            }
        }
    }
}