package main;

import entity.Entity;
import entity.Player;
import entity.Portal;
import entity.MissionManager;
import entity.MapObject;
import entity.NPC_Weshmer;
import enemy.Musgo;
import enemy.HumanoInfectado;
import enemy.HumanoInfectado2;
import enemy.AsechadorDeLaPenumbra;
import enemy.Enemy;
import tile.TileManager;
import detector.CollisionChecker;
import detector.EnemyCollisionManager;
import detector.CollisionCheckerMap02;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class GamePanel extends JPanel implements Runnable {

    private final AlphaComposite transparent40 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
    private final AlphaComposite transparent15 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f);

    final int originalTileSize = 16;
    final int scale = 3;

    public final int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;

    public double renderScale = 1.0;
    public int offsetX = 0;
    public int offsetY = 0;

    public KeyHandler keyH;

    public int gameState;
    public final int menuState = 0;
    public final int transitionState = 1;
    public final int playState = 2;
    public final int settingsState = 3;
    public final int pauseState = 4;
    public final int pauseSettingsState = 5;
    public final int gameOverState = 6;

    public int playerMaxLife = 32;
    public int playerLife = 32;
    private Image heartImage;

    public int maxWorldCol = 50;
    public int maxWorldRow = 50;

    Thread gameThread;
    private long timerFPS = System.currentTimeMillis();
    private int frameCount = 0;
    private int currentFPS = 0;

    public int zonaSeguraTimer = 600;

    public CopyOnWriteArrayList<Entity> objetosDelJuego = new CopyOnWriteArrayList<>();
    public ArrayList<Entity> entityList = new ArrayList<>();

    public Player player;
    public TileManager tileM = new TileManager(this);

    public CollisionChecker cChecker = new CollisionChecker(this);
    public CollisionCheckerMap02 cCheckerMap02 = new CollisionCheckerMap02(this);
    public EnemyCollisionManager enemyCollisionManager = new EnemyCollisionManager(this);

    public Portal portalActual;
    public String mapaActual = "map01";

    private boolean weshmerSpawnedMap1 = false;
    private boolean weshmerSpawnedMap2 = false;

    private Rectangle pauseResumeButton = new Rectangle();
    private Rectangle pauseSettingsButton = new Rectangle();
    private Rectangle pauseMenuButton = new Rectangle();

    private Rectangle btnPausaArriba = new Rectangle();
    private Rectangle btnPausaAbajo = new Rectangle();
    private Rectangle btnPausaIzquierda = new Rectangle();
    private Rectangle btnPausaDerecha = new Rectangle();
    private Rectangle btnPausaHozEquip = new Rectangle();
    private Rectangle btnPausaHozAttack = new Rectangle();
    private Rectangle btnPausaShield = new Rectangle();
    private Rectangle btnPausaPatada = new Rectangle();
    private Rectangle btnPausaPuno = new Rectangle();
    private Rectangle btnPausaExpI = new Rectangle();
    private Rectangle btnPausaExpO = new Rectangle();
    private Rectangle btnPausaExpP = new Rectangle();
    private Rectangle btnVolverPausa = new Rectangle();

    private Rectangle btnRevivir = new Rectangle();
    private Rectangle btnGameOverMenu = new Rectangle();

    private String reasignandoTeclaPausa = null;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);

        this.keyH = new KeyHandler(this);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        this.player = new Player(this, keyH);
        this.playerLife = this.playerMaxLife;
        this.zonaSeguraTimer = 600;

        portalActual = new Portal(this, tileSize * 13, tileSize * 0, "map02", "/portales/portal01.gif");
        this.objetosDelJuego.add(portalActual);

        spawnEnemigosPorMapa("map01");

        try {
            heartImage = new ImageIcon(getClass().getResource("/player/corazon/corazon.gif")).getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }

        gameState = pauseState;

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                revalidate();
                repaint();
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int logicalX = (int) ((e.getX() - offsetX) / renderScale);
                int logicalY = (int) ((e.getY() - offsetY) / renderScale);
                Point p = new Point(logicalX, logicalY);

                if (gameState == pauseState) {
                    if (pauseResumeButton.contains(p)) {
                        gameState = playState;
                    } else if (pauseSettingsButton.contains(p)) {
                        gameState = pauseSettingsState;
                    } else if (pauseMenuButton.contains(p)) {
                        regresarAlMenuPrincipal();
                    }
                } else if (gameState == pauseSettingsState) {
                    if (btnPausaArriba.contains(p)) reasignandoTeclaPausa = "ARRIBA";
                    else if (btnPausaAbajo.contains(p)) reasignandoTeclaPausa = "ABAJO";
                    else if (btnPausaIzquierda.contains(p)) reasignandoTeclaPausa = "IZQUIERDA";
                    else if (btnPausaDerecha.contains(p)) reasignandoTeclaPausa = "DERECHA";
                    else if (btnPausaHozEquip.contains(p)) reasignandoTeclaPausa = "HOZ_EQUIPAR";
                    else if (btnPausaHozAttack.contains(p)) reasignandoTeclaPausa = "HOZ_ATACAR";
                    else if (btnPausaShield.contains(p)) reasignandoTeclaPausa = "ESCUDO";
                    else if (btnPausaPatada.contains(p)) reasignandoTeclaPausa = "PATADA";
                    else if (btnPausaPuno.contains(p)) reasignandoTeclaPausa = "PUNO";
                    else if (btnPausaExpI.contains(p)) reasignandoTeclaPausa = "EXP_I";
                    else if (btnPausaExpO.contains(p)) reasignandoTeclaPausa = "EXP_O";
                    else if (btnPausaExpP.contains(p)) reasignandoTeclaPausa = "EXP_P";
                    else if (btnVolverPausa.contains(p)) {
                        reasignandoTeclaPausa = null;
                        gameState = pauseState;
                    }
                } else if (gameState == gameOverState) {
                    if (btnRevivir.contains(p)) {
                        playerLife = playerMaxLife;
                        zonaSeguraTimer = 600;
                        spawnEnemigosPorMapa(mapaActual);
                        gameState = playState;
                    } else if (btnGameOverMenu.contains(p)) {
                        regresarAlMenuPrincipal();
                    }
                }
            }
        });

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameState == pauseSettingsState && reasignandoTeclaPausa != null) {
                    int code = e.getKeyCode();
                    keyH.actualizarTecla(reasignandoTeclaPausa, code);
                    reasignandoTeclaPausa = null;
                } else if (gameState == pauseState && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    gameState = playState;
                }
            }
        });
    }

    public void spawnEnemigosPorMapa(String mapa) {
        objetosDelJuego.removeIf(obj -> obj instanceof Enemy || obj instanceof NPC_Weshmer);
        Random rand = new Random();

        int radioExclusionTiles = 15;
        int intentosMax = 3000;

        if (mapa.equals("map01")) {
            weshmerSpawnedMap1 = false;
            int spawnX = 18 * tileSize;
            int spawnY = 16 * tileSize;

            for (int i = 0; i < 4; i++) {
                int intentos = 0;
                int randomX, randomY;
                boolean posicionValida = false;

                do {
                    int randomCol = rand.nextInt(maxWorldCol - 4) + 2;
                    int randomRow = rand.nextInt(maxWorldRow - 4) + 2;
                    randomX = randomCol * tileSize;
                    randomY = randomRow * tileSize;

                    double distanciaSpawn = Math.sqrt(Math.pow(randomX - spawnX, 2) + Math.pow(randomY - spawnY, 2)) / tileSize;
                    double distanciaPlayer = (player != null) ? Math.sqrt(Math.pow(randomX - player.worldX, 2) + Math.pow(randomY - player.worldY, 2)) / tileSize : 30;

                    if (distanciaSpawn >= radioExclusionTiles && distanciaPlayer >= radioExclusionTiles) {
                        posicionValida = true;
                    }
                    intentos++;
                } while (!posicionValida && intentos < intentosMax);

                objetosDelJuego.add(new Musgo(this, randomX, randomY));
            }
        } else if (mapa.equals("map02")) {
            weshmerSpawnedMap2 = false;
            int spawnXMap2 = 5 * tileSize;
            int spawnYMap2 = 45 * tileSize;

            for (int i = 0; i < 4; i++) {
                int intentos = 0, randomX = 0, randomY = 0;
                boolean valida = false;
                do {
                    int col = rand.nextInt(maxWorldCol - 4) + 2;
                    int row = rand.nextInt(maxWorldRow - 4) + 2;
                    randomX = col * tileSize;
                    randomY = row * tileSize;
                    double distancia = Math.sqrt(Math.pow(randomX - spawnXMap2, 2) + Math.pow(randomY - spawnYMap2, 2)) / tileSize;
                    if (distancia >= radioExclusionTiles) valida = true;
                    intentos++;
                } while (!valida && intentos < intentosMax);
                objetosDelJuego.add(new Musgo(this, randomX, randomY));
            }

            for (int i = 0; i < 3; i++) {
                int intentos = 0, randomX = 0, randomY = 0;
                boolean valida = false;
                do {
                    int col = rand.nextInt(maxWorldCol - 4) + 2;
                    int row = rand.nextInt(maxWorldRow - 4) + 2;
                    randomX = col * tileSize;
                    randomY = row * tileSize;
                    double distancia = Math.sqrt(Math.pow(randomX - spawnXMap2, 2) + Math.pow(randomY - spawnYMap2, 2)) / tileSize;
                    if (distancia >= radioExclusionTiles) valida = true;
                    intentos++;
                } while (!valida && intentos < intentosMax);
                objetosDelJuego.add(new HumanoInfectado(this, randomX, randomY));
            }

            for (int i = 0; i < 3; i++) {
                int intentos = 0, randomX = 0, randomY = 0;
                boolean valida = false;
                do {
                    int col = rand.nextInt(maxWorldCol - 4) + 2;
                    int row = rand.nextInt(maxWorldRow - 4) + 2;
                    randomX = col * tileSize;
                    randomY = row * tileSize;
                    double distancia = Math.sqrt(Math.pow(randomX - spawnXMap2, 2) + Math.pow(randomY - spawnYMap2, 2)) / tileSize;
                    if (distancia >= radioExclusionTiles) valida = true;
                    intentos++;
                } while (!valida && intentos < intentosMax);
                objetosDelJuego.add(new HumanoInfectado2(this, randomX, randomY));
            }
        } else if (mapa.equals("map03")) {
            objetosDelJuego.add(new AsechadorDeLaPenumbra(this, 15 * tileSize, 10 * tileSize));
        }
    }

    private void regresarAlMenuPrincipal() {
        gameThread = null;
        JFrame window = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.getContentPane().removeAll();

            GamePanel newGamePanel = new GamePanel();
            MainMenuManager mainMenu = new MainMenuManager(window, newGamePanel);

            window.setContentPane(mainMenu);

            if ((window.getExtendedState() & JFrame.MAXIMIZED_BOTH) == 0) {
                window.pack();
                window.setLocationRelativeTo(null);
            }

            mainMenu.requestFocusInWindow();
            window.revalidate();
            window.repaint();
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / 60.0;
        double fpsInterval = 1000000000.0 / 120.0;
        double delta = 0;
        double deltaFPS = 0;
        long lastTime = System.nanoTime();

        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            deltaFPS += (currentTime - lastTime) / fpsInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                delta--;
            }

            if (deltaFPS >= 1) {
                repaint();
                frameCount++;
                deltaFPS--;
            }

            if (System.currentTimeMillis() - timerFPS >= 1000) {
                currentFPS = frameCount;
                frameCount = 0;
                timerFPS += 1000;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        if (gameState == playState) {
            if (zonaSeguraTimer > 0) {
                zonaSeguraTimer--;
            }

            if (player != null) {
                player.update();
            }

            for (Entity obj : objetosDelJuego) {
                if (obj != null) {
                    obj.update();
                }
            }

            // APARICIÓN DE WESHMER AL CUMPLIR LAS BAJAS
            if ("map01".equals(mapaActual) && MissionManager.portalAbierto && !weshmerSpawnedMap1) {
                objetosDelJuego.add(new NPC_Weshmer(this, player.worldX + (tileSize * 2), player.worldY));
                weshmerSpawnedMap1 = true;
            } else if ("map02".equals(mapaActual) && MissionManager.baldosaDesbloqueada && !weshmerSpawnedMap2) {
                objetosDelJuego.add(new NPC_Weshmer(this, player.worldX + (tileSize * 2), player.worldY));
                weshmerSpawnedMap2 = true;
            }

            // REVISIÓN DE PORTALES Y VALIDACIÓN DE LLAVES
            for (Entity obj : objetosDelJuego) {
                if (obj instanceof Portal) {
                    Portal portal = (Portal) obj;

                    int distanciaX = Math.abs(player.worldX - portal.worldX);
                    int distanciaY = Math.abs(player.worldY - portal.worldY);

                    if (distanciaX < tileSize && distanciaY < tileSize && player.teleportLock <= 0) {

                        if (mapaActual.equals("map01")) {
                            if (player.inventory.hasLlave1) {
                                tileM.cambiarMapa("map02");
                                mapaActual = "map02";

                                portal.targetMap = "map03";
                                portal.portalGif = new ImageIcon(getClass().getResource("/portales/portal02.gif")).getImage();

                                portal.worldX = 15 * tileSize;
                                portal.worldY = 1 * tileSize;

                                player.worldX = 15 * tileSize;
                                player.worldY = 45 * tileSize;

                                player.teleportLock = 60;
                                zonaSeguraTimer = 600;
                                spawnEnemigosPorMapa("map02");
                            } else {
                                player.teleportLock = 30;
                            }

                        } else if (mapaActual.equals("map02")) {
                            if (player.inventory.hasLlave2) {
                                // --- MÚSICA DEL JEFE ---
                                SoundManager.playBossMusic("/sounds/jefe.wav"); // AJUSTA ESTA RUTA A TU ARCHIVO REAL

                                tileM.cambiarMapa("map03");
                                mapaActual = "map03";

                                portal.targetMap = "map02";
                                portal.portalGif = new ImageIcon(getClass().getResource("/portales/portal01.gif")).getImage();

                                portal.worldX = 15 * tileSize;
                                portal.worldY = 8 * tileSize;

                                player.worldX = 15 * tileSize;
                                player.worldY = 10 * tileSize;

                                player.teleportLock = 60;
                                zonaSeguraTimer = 600;
                                spawnEnemigosPorMapa("map03");
                            } else {
                                player.teleportLock = 30;
                            }

                        } else if (mapaActual.equals("map03")) {
                            if (player.inventory.hasMedalla) {
                                // --- MÚSICA AMBIENTE AL SALIR ---
                                SoundManager.stopBossMusic();
                                SoundManager.playBackgroundMusic("/sounds/ambiente.wav"); // AJUSTA ESTA RUTA

                                tileM.cambiarMapa("map02");
                                mapaActual = "map02";

                                portal.targetMap = "map03";
                                portal.portalGif = new ImageIcon(getClass().getResource("/portales/portal02.gif")).getImage();

                                portal.worldX = 15 * tileSize;
                                portal.worldY = 1 * tileSize;

                                player.worldX = 15 * tileSize;
                                player.worldY = 3 * tileSize;

                                player.teleportLock = 60;
                                zonaSeguraTimer = 600;
                            } else {
                                player.teleportLock = 30;
                            }
                        }
                        break;
                    }
                }
            }

            // GESTIÓN DE MUERTE, CONTEO Y RESPAWN DE MUSGO
            for (Entity obj : objetosDelJuego) {
                if (obj instanceof Enemy) {
                    Enemy enemyObj = (Enemy) obj;
                    if (enemyObj.isDead) {
                        if (enemyObj.deathTimer == 0) {
                            enemyObj.deathTimer = 240;

                            if ("map02".equals(mapaActual) && !MissionManager.baldosaDesbloqueada) {
                                MissionManager.enemigosDerrotadosBaldosa++;
                                MissionManager.checkBaldosaCompletion(objetosDelJuego);
                            } else if ("map01".equals(mapaActual) && !MissionManager.portalAbierto) {
                                MissionManager.enemigosDerrotadosPortal++;
                                if (MissionManager.enemigosDerrotadosPortal >= MissionManager.META_PORTAL) {
                                    MissionManager.portalAbierto = true;
                                }
                            }

                        } else {
                            enemyObj.deathTimer--;
                            if (enemyObj.deathTimer <= 0) {
                                if (enemyObj instanceof Musgo) {
                                    Random rand = new Random();
                                    int randomCol = rand.nextInt(maxWorldCol - 4) + 2;
                                    int randomRow = rand.nextInt(maxWorldRow - 4) + 2;
                                    objetosDelJuego.add(new Musgo(this, randomCol * tileSize, randomRow * tileSize));
                                }
                                objetosDelJuego.remove(obj);
                            }
                        }
                    }
                }
            }

            if (playerLife <= 0) {
                gameState = gameOverState;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        double scaleX = (double) getWidth() / screenWidth;
        double scaleY = (double) getHeight() / screenHeight;
        renderScale = Math.min(scaleX, scaleY);

        offsetX = (int) ((getWidth() - (screenWidth * renderScale)) / 2);
        offsetY = (int) ((getHeight() - (screenHeight * renderScale)) / 2);

        g2.translate(offsetX, offsetY);
        g2.scale(renderScale, renderScale);
        g2.setClip(0, 0, screenWidth, screenHeight);

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

        if (gameState == playState || gameState == pauseState || gameState == pauseSettingsState) {
            tileM.draw(g2);

            if (player != null) {
                entityList.add(player);
            }

            for (Entity obj : objetosDelJuego) {
                if (obj != null) {
                    entityList.add(obj);
                }
            }

            Collections.sort(entityList, new Comparator<Entity>() {
                @Override
                public int compare(Entity e1, Entity e2) {
                    if (e1.zIndex != e2.zIndex) {
                        return Integer.compare(e1.zIndex, e2.zIndex);
                    }
                    return Integer.compare(e1.getBottomY(), e2.getBottomY());
                }
            });

            for (Entity entity : entityList) {
                if (entity != null) {
                    if (entity instanceof Enemy && ((Enemy) entity).isDead) {
                        int screenX = entity.worldX - player.worldX + player.screenX;
                        int screenY = entity.worldY - player.worldY + player.screenY;

                        g2.setColor(new Color(140, 0, 0, 190));
                        g2.fillOval(screenX + 6, screenY + tileSize - 16, tileSize - 12, 14);
                    }
                    entity.draw(g2);
                }
            }

            entityList.clear();

            drawPlayerLife(g2);
            drawHUD(g2);
            drawMiniMap(g2);

            if (gameState == playState) {
                g2.setFont(new Font("Serif", Font.BOLD, 22));

                if ("map02".equals(mapaActual) && !MissionManager.baldosaDesbloqueada) {
                    String textoBaldosa = "Enemigos abatidos: " + MissionManager.enemigosDerrotadosBaldosa + "/" + MissionManager.META_BALDOSA;
                    int x = getXCenteredText(textoBaldosa, g2);
                    g2.setColor(Color.BLACK); g2.drawString(textoBaldosa, x + 2, 42);
                    g2.setColor(Color.YELLOW); g2.drawString(textoBaldosa, x, 40);
                } else if ("map01".equals(mapaActual) && !MissionManager.portalAbierto) {
                    String textoPortal = "Enemigos abatidos: " + MissionManager.enemigosDerrotadosPortal + "/" + MissionManager.META_PORTAL;
                    int x = getXCenteredText(textoPortal, g2);
                    g2.setColor(Color.BLACK); g2.drawString(textoPortal, x + 2, 42);
                    g2.setColor(Color.CYAN); g2.drawString(textoPortal, x, 40);
                }
            }

            if (zonaSeguraTimer > 0) {
                g2.setFont(new Font("Serif", Font.BOLD, 18));
                g2.setColor(new Color(0, 255, 255, 200));
                String zonaMsg = "ZONA SEGURA: " + (zonaSeguraTimer / 60 + 1) + "s";
                g2.drawString(zonaMsg, screenWidth / 2 - 70, 70);
            }

            if (keyH != null && keyH.debugMode) {
                g2.setFont(new Font("Monospaced", Font.BOLD, 16));
                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRoundRect(10, 10, 260, 100, 10, 10);

                g2.setColor(Color.GREEN);
                g2.drawString("FPS: " + currentFPS, 20, 30);
                g2.setColor(Color.WHITE);
                g2.drawString("Píxeles  X: " + player.worldX + " Y: " + player.worldY, 20, 55);

                int col = player.worldX / tileSize;
                int row = player.worldY / tileSize;

                g2.setColor(Color.YELLOW);
                g2.drawString("Tile/Grid Col: " + col + " Row: " + row, 20, 80);
                g2.setColor(Color.CYAN);
                g2.drawString("Mapa Actual: " + mapaActual, 20, 105);
            }

            if (gameState == pauseState) {
                drawPauseScreen(g2);
            } else if (gameState == pauseSettingsState) {
                drawPauseSettingsScreen(g2);
            }
        } else if (gameState == gameOverState) {
            tileM.draw(g2);
            drawGameOverScreen(g2);
        }

        g2.dispose();
    }

    public void drawHUD(Graphics2D g2) {
        int x = 20;
        int y = screenHeight - 110;
        int size = 32;

        try {
            Image imgHoz = new ImageIcon(getClass().getResource("/objetos/hoz.png")).getImage();
            float alphaHoz = (player.hozEquipada) ? 1.0f : 0.3f;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaHoz));
            g2.drawImage(imgHoz, x, y, size, size, this);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            x += 40;
        } catch (Exception e) {}

        try {
            Image imgShield = new ImageIcon(getClass().getResource("/objetos/escudo.png")).getImage();
            float alphaShield = (player.escudoActivo) ? 1.0f : 0.3f;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaShield));
            g2.drawImage(imgShield, x, y, size, size, this);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            x += 40;
        } catch (Exception e) {}

        if (player.inventory.hasLlave1) {
            try {
                Image img = new ImageIcon(getClass().getResource("/llaves/llavemap01.png")).getImage();
                g2.drawImage(img, x, y, size, size, this);
                x += 40;
            } catch (Exception e) {}
        }
        if (player.inventory.hasLlave2) {
            try {
                Image img = new ImageIcon(getClass().getResource("/llaves/llavemap02.png")).getImage();
                g2.drawImage(img, x, y, size, size, this);
                x += 40;
            } catch (Exception e) {}
        }
        if (player.inventory.hasMedalla) {
            try {
                Image img = new ImageIcon(getClass().getResource("/llaves/medalla.png")).getImage();
                g2.drawImage(img, x, y, size, size, this);
            } catch (Exception e) {}
        }
    }

    public void drawMiniMap(Graphics2D g2) {
        if (keyH == null || !keyH.keyY) return;

        int mapWidth = 150;
        int mapHeight = 150;
        int mapX = screenWidth - mapWidth - 20;
        int mapY = 20;

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRoundRect(mapX, mapY, mapWidth, mapHeight, 10, 10);
        g2.setColor(new Color(180, 140, 80));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(mapX, mapY, mapWidth, mapHeight, 10, 10);

        double scaleX = (double) mapWidth / maxWorldCol;
        double scaleY = (double) mapHeight / maxWorldRow;

        if (tileM != null && tileM.mapTileNum != null) {
            for (int worldRow = 0; worldRow < maxWorldRow; worldRow++) {
                for (int worldCol = 0; worldCol < maxWorldCol; worldCol++) {
                    int tileNum = tileM.mapTileNum[worldCol][worldRow];

                    if (tileNum > 1) {
                        int tX = mapX + (int) (worldCol * scaleX);
                        int tY = mapY + (int) (worldRow * scaleY);

                        if (mapaActual.equals("map01")) {
                            if (tileNum == 37) g2.setColor(new Color(200, 162, 200));
                            else if (tileNum >= 40 && tileNum <= 55) g2.setColor(new Color(0, 0, 139));
                            else g2.setColor(new Color(100, 100, 100));
                        } else if (mapaActual.equals("map02")) {
                            if (tileNum == 13) g2.setColor(new Color(100, 50, 50));
                            else g2.setColor(new Color(150, 150, 150));
                        } else {
                            g2.setColor(Color.GRAY);
                        }

                        g2.fillRect(tX, tY, (int) Math.ceil(scaleX), (int) Math.ceil(scaleY));
                    }
                }
            }
        }

        for (Entity obj : objetosDelJuego) {
            if (obj instanceof Portal) {
                int portalMapX = mapX + (int) ((obj.worldX / tileSize) * scaleX);
                int portalMapY = mapY + (int) ((obj.worldY / tileSize) * scaleY);

                g2.setColor(Color.CYAN);
                g2.fillOval(portalMapX - 3, portalMapY - 3, 8, 8);
                g2.setColor(Color.WHITE);
                g2.drawOval(portalMapX - 3, portalMapY - 3, 8, 8);
            }
        }

        if (player != null) {
            int playerMapX = mapX + (int) ((player.worldX / tileSize) * scaleX);
            int playerMapY = mapY + (int) ((player.worldY / tileSize) * scaleY);
            g2.setColor(Color.YELLOW);
            g2.fillOval(playerMapX - 2, playerMapY - 2, 5, 5);
        }

        for (Entity obj : objetosDelJuego) {
            if (obj != null && obj instanceof Enemy && !((Enemy) obj).isDead) {
                int enemyMapX = mapX + (int) ((obj.worldX / tileSize) * scaleX);
                int enemyMapY = mapY + (int) ((obj.worldY / tileSize) * scaleY);
                g2.setColor(Color.RED);
                g2.fillRect(enemyMapX - 1, enemyMapY - 1, 4, 4);
            }
        }
    }

    public void drawPlayerLife(Graphics2D g2) {
        int x = 20;
        int y = screenHeight - 60;
        int heartSize = 40;
        int spacing = 45;

        for (int i = 0; i < 8; i++) {
            int heartValue = (i + 1) * 4;

            if (heartImage != null) {
                if (playerLife >= heartValue) {
                    g2.drawImage(heartImage, x + (i * spacing), y, heartSize, heartSize, this);
                } else if (heartValue - 2 <= playerLife) {
                    Graphics2D g2d = (Graphics2D) g2.create();
                    g2d.setComposite(transparent40);
                    g2d.drawImage(heartImage, x + (i * spacing), y, heartSize, heartSize, this);
                    g2d.dispose();
                } else {
                    Graphics2D g2d = (Graphics2D) g2.create();
                    g2d.setComposite(transparent15);
                    g2d.drawImage(heartImage, x + (i * spacing), y, heartSize, heartSize, this);
                    g2d.dispose();
                }
            }
        }
    }

    public void drawPauseScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        g2.setFont(new Font("Serif", Font.BOLD, 42));
        g2.setColor(new Color(245, 230, 200));
        String text = "JUEGO PAUSADO";
        int x = getXCenteredText(text, g2);
        int y = screenHeight / 2 - 80;

        g2.setColor(Color.BLACK);
        g2.drawString(text, x + 3, y + 3);
        g2.setColor(new Color(245, 230, 200));
        g2.drawString(text, x, y);

        int btnW = 220;
        int btnH = 38;
        int btnX = (screenWidth - btnW) / 2;
        int startY = screenHeight / 2 - 20;

        pauseResumeButton.setBounds(btnX, startY, btnW, btnH);
        pauseSettingsButton.setBounds(btnX, startY + 48, btnW, btnH);
        pauseMenuButton.setBounds(btnX, startY + 96, btnW, btnH);

        drawButton(g2, "SEGUIR JUGANDO", pauseResumeButton);
        drawButton(g2, "AJUSTES", pauseSettingsButton);
        drawButton(g2, "MENÚ PRINCIPAL", pauseMenuButton);
    }

    public void drawPauseSettingsScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        int cardW = 380;
        int cardH = 440;
        int cardX = (screenWidth - cardW) / 2;
        int cardY = (screenHeight - cardH) / 2;

        g2.setColor(new Color(45, 25, 10));
        g2.fillRoundRect(cardX, cardY, cardW, cardH, 12, 12);
        g2.setColor(new Color(180, 140, 80));
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawRoundRect(cardX, cardY, cardW, cardH, 12, 12);

        g2.setFont(new Font("Serif", Font.BOLD, 14));
        g2.setColor(new Color(245, 230, 200));
        String title = "CONFIGURACIÓN EN PAUSA";
        int titleX = cardX + (cardW - (int) g2.getFontMetrics().getStringBounds(title, g2).getWidth()) / 2;
        g2.drawString(title, titleX, cardY + 28);

        int bW = 320;
        int bH = 26;
        int bX = cardX + (cardW - bW) / 2;
        int startY = cardY + 42;
        int gap = 29;

        btnPausaArriba.setBounds(bX, startY, bW, bH);
        btnPausaIzquierda.setBounds(bX, startY + gap, bW / 2 - 4, bH);
        btnPausaDerecha.setBounds(bX + bW / 2 + 4, startY + gap, bW / 2 - 4, bH);
        btnPausaAbajo.setBounds(bX, startY + (gap * 2), bW, bH);
        btnPausaHozEquip.setBounds(bX, startY + (gap * 3), bW, bH);
        btnPausaHozAttack.setBounds(bX, startY + (gap * 4), bW, bH);
        btnPausaShield.setBounds(bX, startY + (gap * 5), bW, bH);

        btnPausaPatada.setBounds(bX, startY + (gap * 6), bW / 2 - 4, bH);
        btnPausaPuno.setBounds(bX + bW / 2 + 4, startY + (gap * 6), bW / 2 - 4, bH);

        btnPausaExpI.setBounds(bX, startY + (gap * 7), bW / 2 - 4, bH);
        btnPausaExpO.setBounds(bX + bW / 2 + 4, startY + (gap * 7), bW / 2 - 4, bH);
        btnPausaExpP.setBounds(bX, startY + (gap * 8), bW, bH);

        btnVolverPausa.setBounds(bX, startY + (gap * 9) + 4, bW, bH);

        String txtArriba = (reasignandoTeclaPausa != null && reasignandoTeclaPausa.equals("ARRIBA")) ? "Presiona tecla..." : "Arriba: " + KeyEvent.getKeyText(keyH.keyUp);
        String txtAbajo = (reasignandoTeclaPausa != null && reasignandoTeclaPausa.equals("ABAJO")) ? "Presiona tecla..." : "Abajo: " + KeyEvent.getKeyText(keyH.keyDown);
        String txtIzq = (reasignandoTeclaPausa != null && reasignandoTeclaPausa.equals("IZQUIERDA")) ? "Presiona tecla..." : "Izquierda: " + KeyEvent.getKeyText(keyH.keyLeft);
        String txtDer = (reasignandoTeclaPausa != null && reasignandoTeclaPausa.equals("DERECHA")) ? "Presiona tecla..." : "Derecha: " + KeyEvent.getKeyText(keyH.keyRight);
        String txtHozEq = (reasignandoTeclaPausa != null && reasignandoTeclaPausa.equals("HOZ_EQUIPAR")) ? "Presiona tecla..." : "Equipar Hoz: " + KeyEvent.getKeyText(keyH.keyHozEquip);
        String txtHozAt = (reasignandoTeclaPausa != null && reasignandoTeclaPausa.equals("HOZ_ATACAR")) ? "Presiona tecla..." : "Ataque Os: " + KeyEvent.getKeyText(keyH.keyHozAttack);
        String txtShield = (reasignandoTeclaPausa != null && reasignandoTeclaPausa.equals("ESCUDO")) ? "Presiona tecla..." : "Escudo: " + KeyEvent.getKeyText(keyH.keyShieldBind);
        String txtPatada = (reasignandoTeclaPausa != null && reasignandoTeclaPausa.equals("PATADA")) ? "Presiona tecla..." : "Patada: " + KeyEvent.getKeyText(keyH.keyPatada);
        String txtPuno = (reasignandoTeclaPausa != null && reasignandoTeclaPausa.equals("PUNO")) ? "Presiona tecla..." : "Puño: " + KeyEvent.getKeyText(keyH.keyPuno);
        String txtExpI = (reasignandoTeclaPausa != null && reasignandoTeclaPausa.equals("EXP_I")) ? "Presiona tecla..." : "Exp I: " + KeyEvent.getKeyText(keyH.expI);
        String txtExpO = (reasignandoTeclaPausa != null && reasignandoTeclaPausa.equals("EXP_O")) ? "Presiona tecla..." : "Exp O: " + KeyEvent.getKeyText(keyH.expO);
        String txtExpP = (reasignandoTeclaPausa != null && reasignandoTeclaPausa.equals("EXP_P")) ? "Presiona tecla..." : "Exp P: " + KeyEvent.getKeyText(keyH.expP);

        drawButton(g2, txtArriba, btnPausaArriba);
        drawButton(g2, txtIzq, btnPausaIzquierda);
        drawButton(g2, txtDer, btnPausaDerecha);
        drawButton(g2, txtAbajo, btnPausaAbajo);
        drawButton(g2, txtHozEq, btnPausaHozEquip);
        drawButton(g2, txtHozAt, btnPausaHozAttack);
        drawButton(g2, txtShield, btnPausaShield);
        drawButton(g2, txtPatada, btnPausaPatada);
        drawButton(g2, txtPuno, btnPausaPuno);
        drawButton(g2, txtExpI, btnPausaExpI);
        drawButton(g2, txtExpO, btnPausaExpO);
        drawButton(g2, txtExpP, btnPausaExpP);
        drawButton(g2, "VOLVER", btnVolverPausa);
    }

    public void drawGameOverScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        int cardW = 340;
        int cardH = 240;
        int cardX = (screenWidth - cardW) / 2;
        int cardY = (screenHeight - cardH) / 2;

        g2.setColor(new Color(45, 25, 10));
        g2.fillRoundRect(cardX, cardY, cardW, cardH, 12, 12);
        g2.setColor(new Color(180, 140, 80));
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawRoundRect(cardX, cardY, cardW, cardH, 12, 12);

        g2.setFont(new Font("Serif", Font.BOLD, 36));
        g2.setColor(new Color(220, 50, 50));
        String text = "HAS MUERTO";
        int textX = cardX + (cardW - (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth()) / 2;
        int textY = cardY + 55;

        g2.drawString(text, textX + 2, textY + 2);
        g2.setColor(new Color(245, 230, 200));
        g2.drawString(text, textX, textY);

        int btnW = 240;
        int btnH = 38;
        int btnX = cardX + (cardW - btnW) / 2;

        btnRevivir.setBounds(btnX, cardY + 95, btnW, btnH);
        btnGameOverMenu.setBounds(btnX, cardY + 150, btnW, btnH);

        drawButton(g2, "REVIVIR", btnRevivir);
        drawButton(g2, "MENÚ DE INICIO", btnGameOverMenu);
    }

    private void drawButton(Graphics2D g2, String text, Rectangle bounds) {
        g2.setColor(new Color(65, 40, 18));
        g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 6, 6);
        g2.setColor(new Color(180, 140, 80));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 6, 6);

        g2.setFont(new Font("Serif", Font.BOLD, 11));
        g2.setColor(new Color(245, 230, 195));
        int tx = bounds.x + (bounds.width - (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth()) / 2;
        int ty = bounds.y + (bounds.height + 6) / 2;
        g2.drawString(text, tx, ty);
    }

    public int getXCenteredText(String text, Graphics2D g2) {
        int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return screenWidth / 2 - length / 2;
    }
}