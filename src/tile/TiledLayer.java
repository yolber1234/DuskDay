package tile;
public class TiledLayer {
    public String type; // Identifica si es "tilelayer" (fondo) o "objectgroup" (objetos)
    public int[] data; // Arreglo que contiene los IDs de tu fondo
    public TiledObject[] objects; // Arreglo que contiene los objetos
}