package entity;

public class Inventory {
    // Objetos clave y coleccionables
    public boolean hasLlave1 = false;
    public boolean hasLlave2 = false;
    public boolean hasMedalla = false;

    // Equipamiento del jugador (hoz y escudo)
    public boolean hasHoz = false;
    public boolean hasShield = false;

    // Métodos opcionales para reiniciar o verificar el inventario
    public void resetInventory() {
        hasLlave1 = false;
        hasLlave2 = false;
        hasMedalla = false;
        hasHoz = false;
        hasShield = false;
    }
}