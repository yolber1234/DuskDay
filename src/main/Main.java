package main;

import javax.swing.JFrame;
import java.awt.BorderLayout;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 1. Cambiamos a true para permitir redimensionar y maximizar libremente
        window.setResizable(true);

        window.setTitle("DuskDays");

        // 2. Usamos BorderLayout para que los paneles se adapten al tamaño total de la ventana
        window.setLayout(new BorderLayout());

        GamePanel gamePanel = new GamePanel();

        // Instanciamos el menú principal pasándole la ventana y el panel del juego
        MainMenuManager menuManager = new MainMenuManager(window, gamePanel);

        // Añadimos el menú de inicio como vista inicial usando BorderLayout.CENTER
        window.add(menuManager, BorderLayout.CENTER);

        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // Iniciamos el hilo del juego en segundo plano para que esté listo cuando termine el video
        gamePanel.startGameThread();
    }
}