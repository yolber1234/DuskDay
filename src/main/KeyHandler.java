package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    GamePanel gp;

    // Movimiento básico
    public boolean upPressed, downPressed, leftPressed, rightPressed;

    // Controles de Hoz, Escudo, Patada, Puño, Minimapa, Debug
    public boolean keyF, keyM, keyY, keyShield, keyK, keyL, debugMode;

    // Explosiones exclusivas (I, O, P)
    public boolean keyI, keyO, keyP;

    // Variables de teclas configurables (remapeables)
    public int keyUp = KeyEvent.VK_W;
    public int keyDown = KeyEvent.VK_S;
    public int keyLeft = KeyEvent.VK_A;
    public int keyRight = KeyEvent.VK_D;
    public int keyHozEquip = KeyEvent.VK_F;
    public int keyHozAttack = KeyEvent.VK_M;
    public int keyShieldBind = KeyEvent.VK_C; // Tecla C por defecto para el escudo
    public int keyPatada = KeyEvent.VK_K;    // Tecla K por defecto para la patada
    public int keyPuno = KeyEvent.VK_L;      // Tecla L por defecto para el puño

    // Teclas configurables para las 3 explosiones
    public int expI = KeyEvent.VK_I;
    public int expO = KeyEvent.VK_O;
    public int expP = KeyEvent.VK_P;

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // Movimiento
        if (code == keyUp) upPressed = true;
        if (code == keyDown) downPressed = true;
        if (code == keyLeft) leftPressed = true;
        if (code == keyRight) rightPressed = true;

        // Acciones de Hoz, Escudo, Patada, Puño y utilidades
        if (code == keyHozEquip) keyF = true;
        if (code == keyHozAttack) keyM = true;
        if (code == keyShieldBind) keyShield = true;
        if (code == keyPatada) keyK = true;
        if (code == keyPuno) keyL = true;
        if (code == KeyEvent.VK_Y) keyY = !keyY;
        if (code == KeyEvent.VK_F2) debugMode = !debugMode;

        // Activación de Explosiones (I, O, P)
        if (code == expI) keyI = true;
        if (code == expO) keyO = true;
        if (code == expP) keyP = true;

        // Gestión de Pausa
        if (code == KeyEvent.VK_SPACE) {
            if (gp.gameState == gp.playState) gp.gameState = gp.pauseState;
            else if (gp.gameState == gp.pauseState) gp.gameState = gp.playState;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();

        if (code == keyUp) upPressed = false;
        if (code == keyDown) downPressed = false;
        if (code == keyLeft) leftPressed = false;
        if (code == keyRight) rightPressed = false;
        if (code == keyHozEquip) keyF = false;
        if (code == keyHozAttack) keyM = false;
        if (code == keyShieldBind) keyShield = false;
        if (code == keyPatada) keyK = false;
        if (code == keyPuno) keyL = false;

        if (code == expI) keyI = false;
        if (code == expO) keyO = false;
        if (code == expP) keyP = false;
    }

    public void actualizarTecla(String idBoton, int nuevoCodigo) {
        switch (idBoton) {
            case "ARRIBA": keyUp = nuevoCodigo; break;
            case "ABAJO": keyDown = nuevoCodigo; break;
            case "IZQUIERDA": keyLeft = nuevoCodigo; break;
            case "DERECHA": keyRight = nuevoCodigo; break;
            case "HOZ_EQUIPAR": keyHozEquip = nuevoCodigo; break;
            case "HOZ_ATACAR": keyHozAttack = nuevoCodigo; break;
            case "ESCUDO": keyShieldBind = nuevoCodigo; break;
            case "PATADA": keyPatada = nuevoCodigo; break;
            case "PUNO": keyPuno = nuevoCodigo; break;
            case "EXP_I": expI = nuevoCodigo; break;
            case "EXP_O": expO = nuevoCodigo; break;
            case "EXP_P": expP = nuevoCodigo; break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}