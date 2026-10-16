package main;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

public class MainMenuManager extends JPanel {
    private MediaPlayer mediaPlayer;
    private JFXPanel fxPanel;
    private JPanel overlayContainer;

    private JPanel mainButtonsPanel;
    private JPanel settingsPanel;
    private GamePanel gamePanelRef;

    private String reasignandoTecla = null;
    private JButton btnKeyArriba, btnKeyAbajo, btnKeyIzquierda, btnKeyDerecha;
    private JButton btnKeyHozEquip, btnKeyHozAttack, btnKeyShield, btnKeyPatada, btnKeyPuno;
    private JButton btnKeyExpI, btnKeyExpO, btnKeyExpP;

    private JButton btnPlay, btnSettings, btnExit;
    private Timer animTimer;

    private float animAlphaPlay = 0.0f, animAlphaSettings = 0.0f, animAlphaExit = 0.0f;
    private float animScalePlay = 0.4f, animScaleSettings = 0.4f, animScaleExit = 0.4f;

    public MainMenuManager(JFrame window, GamePanel gamePanel) {
        this.gamePanelRef = gamePanel;
        setPreferredSize(new Dimension(gamePanel.screenWidth, gamePanel.screenHeight));
        setLayout(null);

        fxPanel = new JFXPanel();
        fxPanel.setBounds(0, 0, gamePanel.screenWidth, gamePanel.screenHeight);
        add(fxPanel);

        overlayContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(0, 0, 0, 0), 0, getHeight(), new Color(15, 8, 3, 190));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlayContainer.setOpaque(false);
        overlayContainer.setLayout(new CardLayout());
        overlayContainer.setBounds(0, 0, gamePanel.screenWidth, gamePanel.screenHeight);

        mainButtonsPanel = crearPanelPrincipal(window, gamePanel);
        settingsPanel = crearPanelAjustes();

        overlayContainer.add(mainButtonsPanel, "MAIN");
        overlayContainer.add(settingsPanel, "SETTINGS");

        add(overlayContainer);
        setComponentZOrder(overlayContainer, 0);
        setComponentZOrder(fxPanel, 1);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();
                if (fxPanel != null) fxPanel.setBounds(0, 0, w, h);
                if (overlayContainer != null) overlayContainer.setBounds(0, 0, w, h);
                reposicionarBotones(w, h);
                if (mainButtonsPanel != null) {
                    mainButtonsPanel.revalidate();
                    mainButtonsPanel.repaint();
                }
            }
        });

        playVideo("res/Fondos/FondoMain.mp4", true, null);
    }

    public void mostrarPanelPrincipal() {
        CardLayout cl = (CardLayout) overlayContainer.getLayout();
        cl.show(overlayContainer, "MAIN");
        playVideo("res/Fondos/FondoMain.mp4", true, null);
    }

    private void reposicionarBotones(int screenW, int screenH) {
        if (btnPlay == null || btnSettings == null || btnExit == null) return;
        boolean isMaximized = screenW > 900;
        int btnW = isMaximized ? 240 : 175;
        int btnH = isMaximized ? 48 : 34;
        int gap = 25;
        int totalWidth = (btnW * 3) + (gap * 2);
        int startX = (screenW - totalWidth) / 2;
        int startY = (int) (screenH * 0.75);

        btnPlay.setBounds(startX, startY, btnW, btnH);
        btnSettings.setBounds(startX + btnW + gap, startY, btnW, btnH);
        btnExit.setBounds(startX + (btnW + gap) * 2, startY, btnW, btnH);
    }

    private JPanel crearPanelPrincipal(JFrame window, GamePanel gamePanel) {
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // CORRECCIÓN: Solo dibuja los botones si el panel está visible y activo
                if (isVisible()) {
                    if (btnPlay.isVisible() && animAlphaPlay > 0.0f) dibujarBotonAnimado(g2, btnPlay, animAlphaPlay, animScalePlay);
                    if (btnSettings.isVisible() && animAlphaSettings > 0.0f) dibujarBotonAnimado(g2, btnSettings, animAlphaSettings, animScaleSettings);
                    if (btnExit.isVisible() && animAlphaExit > 0.0f) dibujarBotonAnimado(g2, btnExit, animAlphaExit, animScaleExit);
                }
            }
        };
        panel.setOpaque(false);

        btnPlay = createStyledButton("JUGAR");
        btnSettings = createStyledButton("AJUSTES");
        btnExit = createStyledButton("SALIR");

        btnPlay.setVisible(false);
        btnSettings.setVisible(false);
        btnExit.setVisible(false);

        reposicionarBotones(768, 576);

        btnPlay.addActionListener(e -> {
            stopTimer();

            btnPlay.setVisible(false);
            btnSettings.setVisible(false);
            btnExit.setVisible(false);

            // Ocultamos y removemos el panel por completo para evitar que se sigan pintando
            panel.setVisible(false);
            panel.removeAll();
            panel.revalidate();
            panel.repaint();

            stopVideo();

            SoundManager.playSoundThen("/musica/audioSuspenso2.wav", () -> {
                SoundManager.playSound("/musica/audioSuspenso2.wav", true);
            });

            playVideo("res/Fondos/MapaInicioJuego.mp4", false, () -> {
                SwingUtilities.invokeLater(() -> {
                    window.getContentPane().removeAll();
                    gamePanel.zonaSeguraTimer = 600;
                    window.setContentPane(gamePanel);

                    if ((window.getExtendedState() & JFrame.MAXIMIZED_BOTH) == 0) {
                        window.pack();
                        window.setLocationRelativeTo(null);
                    }

                    gamePanel.gameState = gamePanel.playState;
                    gamePanel.startGameThread();
                    gamePanel.requestFocusInWindow();
                    window.revalidate();
                    window.repaint();
                });
            });
        });

        btnSettings.addActionListener(e -> {
            CardLayout cl = (CardLayout) overlayContainer.getLayout();
            cl.show(overlayContainer, "SETTINGS");
        });

        btnExit.addActionListener(e -> System.exit(0));

        panel.add(btnPlay);
        panel.add(btnSettings);
        panel.add(btnExit);

        return panel;
    }

    private void dibujarBotonAnimado(Graphics2D g2, JButton btn, float alpha, float scale) {
        if (alpha <= 0f) return;
        Graphics2D gCopy = (Graphics2D) g2.create();
        gCopy.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(1.0f, alpha)));
        Rectangle bounds = btn.getBounds();
        double w = bounds.width * scale;
        double h = bounds.height * scale;
        double x = bounds.x + (bounds.width - w) / 2.0;
        double y = bounds.y + (bounds.height - h) / 2.0;
        gCopy.translate(x, y);
        gCopy.scale(scale, scale);
        btn.paint(gCopy);
        gCopy.dispose();
    }

    private void iniciarAnimacionBotones() {
        stopTimer();
        animTimer = new Timer(20, e -> {
            if (mediaPlayer != null) {
                double currentTime = mediaPlayer.getCurrentTime().toSeconds();
                if (currentTime >= 9.0) {
                    if (!btnPlay.isVisible()) btnPlay.setVisible(true);
                    if (animAlphaPlay < 1.0f) {
                        animAlphaPlay += 0.12f;
                        animScalePlay += 0.06f;
                        if (animScalePlay > 1.0f) animScalePlay = 1.0f;
                        mainButtonsPanel.repaint();
                    }
                }
                if (currentTime >= 9.1) {
                    if (!btnSettings.isVisible()) btnSettings.setVisible(true);
                    if (animAlphaSettings < 1.0f) {
                        animAlphaSettings += 0.12f;
                        animScaleSettings += 0.06f;
                        if (animScaleSettings > 1.0f) animScaleSettings = 1.0f;
                        mainButtonsPanel.repaint();
                    }
                }
                if (currentTime >= 9.2) {
                    if (!btnExit.isVisible()) btnExit.setVisible(true);
                    if (animAlphaExit < 1.0f) {
                        animAlphaExit += 0.12f;
                        animScaleExit += 0.06f;
                        if (animScaleExit > 1.0f) animScaleExit = 1.0f;
                        mainButtonsPanel.repaint();
                    }
                    if (animAlphaExit >= 1.0f) stopTimer();
                }
            }
        });
        animTimer.start();
    }

    private void stopTimer() {
        if (animTimer != null && animTimer.isRunning()) animTimer.stop();
    }

    private JPanel crearPanelAjustes() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        JLabel lblTitle = new JLabel("CONFIGURACIÓN DE CONTROLES Y AUDIO");
        lblTitle.setForeground(new Color(255, 235, 190));
        lblTitle.setFont(new Font("Serif", Font.BOLD, 15));

        JLabel lblVolume = new JLabel("Volumen:");
        lblVolume.setForeground(new Color(245, 230, 200));
        lblVolume.setFont(new Font("Serif", Font.BOLD, 12));

        JSlider volumeSlider = new JSlider(0, 100, 70);
        volumeSlider.setOpaque(false);
        volumeSlider.setForeground(new Color(245, 230, 200));
        volumeSlider.addChangeListener(e -> {
            if (mediaPlayer != null) mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
        });

        btnKeyArriba = createStyledButton("Arriba: W");
        btnKeyAbajo = createStyledButton("Abajo: S");
        btnKeyIzquierda = createStyledButton("Izquierda: A");
        btnKeyDerecha = createStyledButton("Derecha: D");
        btnKeyHozEquip = createStyledButton("Equipar Hoz: F");
        btnKeyHozAttack = createStyledButton("Ataque Os: M");
        btnKeyShield = createStyledButton("Escudo: C");
        btnKeyPatada = createStyledButton("Patada: K");
        btnKeyPuno = createStyledButton("Puño: L");
        btnKeyExpI = createStyledButton("Explosión I: I");
        btnKeyExpO = createStyledButton("Explosión O: O");
        btnKeyExpP = createStyledButton("Explosión P: P");

        btnKeyArriba.addActionListener(e -> iniciarReasignacion("ARRIBA", btnKeyArriba));
        btnKeyAbajo.addActionListener(e -> iniciarReasignacion("ABAJO", btnKeyAbajo));
        btnKeyIzquierda.addActionListener(e -> iniciarReasignacion("IZQUIERDA", btnKeyIzquierda));
        btnKeyDerecha.addActionListener(e -> iniciarReasignacion("DERECHA", btnKeyDerecha));
        btnKeyHozEquip.addActionListener(e -> iniciarReasignacion("HOZ_EQUIPAR", btnKeyHozEquip));
        btnKeyHozAttack.addActionListener(e -> iniciarReasignacion("HOZ_ATACAR", btnKeyHozAttack));
        btnKeyShield.addActionListener(e -> iniciarReasignacion("ESCUDO", btnKeyShield));
        btnKeyPatada.addActionListener(e -> iniciarReasignacion("PATADA", btnKeyPatada));
        btnKeyPuno.addActionListener(e -> iniciarReasignacion("PUNO", btnKeyPuno));
        btnKeyExpI.addActionListener(e -> iniciarReasignacion("EXP_I", btnKeyExpI));
        btnKeyExpO.addActionListener(e -> iniciarReasignacion("EXP_O", btnKeyExpO));
        btnKeyExpP.addActionListener(e -> iniciarReasignacion("EXP_P", btnKeyExpP));

        JButton btnVolver = createStyledButton("VOLVER");
        btnVolver.addActionListener(e -> ((CardLayout) overlayContainer.getLayout()).show(overlayContainer, "MAIN"));

        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 3, 2, 3);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; menuPanel.add(lblTitle, gbc);
        gbc.gridy = 1; gbc.gridwidth = 1; menuPanel.add(lblVolume, gbc);
        gbc.gridx = 1; menuPanel.add(volumeSlider, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; menuPanel.add(btnKeyArriba, gbc);
        gbc.gridy = 3; gbc.gridwidth = 1;
        gbc.gridx = 0; menuPanel.add(btnKeyIzquierda, gbc);
        gbc.gridx = 1; menuPanel.add(btnKeyDerecha, gbc);

        gbc.gridy = 4; gbc.gridwidth = 2; gbc.gridx = 0; menuPanel.add(btnKeyAbajo, gbc);
        gbc.gridy = 5; menuPanel.add(btnKeyHozEquip, gbc);
        gbc.gridy = 6; menuPanel.add(btnKeyHozAttack, gbc);
        gbc.gridy = 7; menuPanel.add(btnKeyShield, gbc);

        gbc.gridy = 8; gbc.gridwidth = 1;
        gbc.gridx = 0; menuPanel.add(btnKeyPatada, gbc);
        gbc.gridx = 1; menuPanel.add(btnKeyPuno, gbc);

        gbc.gridy = 9; gbc.gridwidth = 1;
        gbc.gridx = 0; menuPanel.add(btnKeyExpI, gbc);
        gbc.gridx = 1; menuPanel.add(btnKeyExpO, gbc);

        gbc.gridy = 10; gbc.gridwidth = 2; gbc.gridx = 0; menuPanel.add(btnKeyExpP, gbc);
        gbc.gridy = 11; gbc.insets = new Insets(8, 3, 2, 3); menuPanel.add(btnVolver, gbc);

        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.gridx = 0; gbcMain.gridy = 0; gbcMain.anchor = GridBagConstraints.CENTER;
        panel.add(menuPanel, gbcMain);

        panel.setFocusable(true);
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (reasignandoTecla != null) {
                    int keyCode = e.getKeyCode();
                    String keyText = KeyEvent.getKeyText(keyCode);
                    if (gamePanelRef != null && gamePanelRef.keyH != null) {
                        gamePanelRef.keyH.actualizarTecla(reasignandoTecla, keyCode);
                    }
                    if (reasignandoTecla.equals("ARRIBA")) btnKeyArriba.setText("Arriba: " + keyText);
                    if (reasignandoTecla.equals("ABAJO")) btnKeyAbajo.setText("Abajo: " + keyText);
                    if (reasignandoTecla.equals("IZQUIERDA")) btnKeyIzquierda.setText("Izquierda: " + keyText);
                    if (reasignandoTecla.equals("DERECHA")) btnKeyDerecha.setText("Derecha: " + keyText);
                    if (reasignandoTecla.equals("HOZ_EQUIPAR")) btnKeyHozEquip.setText("Equipar Hoz: " + keyText);
                    if (reasignandoTecla.equals("HOZ_ATACAR")) btnKeyHozAttack.setText("Ataque Os: " + keyText);
                    if (reasignandoTecla.equals("ESCUDO")) btnKeyShield.setText("Escudo: " + keyText);
                    if (reasignandoTecla.equals("PATADA")) btnKeyPatada.setText("Patada: " + keyText);
                    if (reasignandoTecla.equals("PUNO")) btnKeyPuno.setText("Puño: " + keyText);
                    if (reasignandoTecla.equals("EXP_I")) btnKeyExpI.setText("Explosión I: " + keyText);
                    if (reasignandoTecla.equals("EXP_O")) btnKeyExpO.setText("Explosión O: " + keyText);
                    if (reasignandoTecla.equals("EXP_P")) btnKeyExpP.setText("Explosión P: " + keyText);
                    reasignandoTecla = null;
                }
            }
        });

        return panel;
    }

    private void iniciarReasignacion(String control, JButton boton) {
        reasignandoTecla = control;
        boton.setText("Presiona tecla...");
        settingsPanel.requestFocusInWindow();
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                Color topColor, bottomColor, borderColor;
                if (getModel().isPressed()) {
                    topColor = new Color(55, 32, 14); bottomColor = new Color(30, 16, 6); borderColor = new Color(130, 95, 35);
                } else if (getModel().isRollover()) {
                    topColor = new Color(105, 70, 30); bottomColor = new Color(60, 35, 14); borderColor = new Color(240, 200, 110);
                } else {
                    topColor = new Color(75, 45, 20); bottomColor = new Color(40, 22, 9); borderColor = new Color(185, 140, 65);
                }
                GradientPaint gp = new GradientPaint(0, 0, topColor, 0, h, bottomColor);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, w, h, 8, 8);
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawRoundRect(1, 1, w - 3, h - 3, 7, 7);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setPreferredSize(new Dimension(268, 30));
        button.setForeground(new Color(255, 240, 210));
        button.setFont(new Font("Serif", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    public void playVideo(String path, boolean loop, Runnable onFinished) {
        javafx.application.Platform.runLater(() -> {
            try {
                File file = new File(path);
                if (!file.exists()) return;
                Media media = new Media(file.toURI().toString());
                if (mediaPlayer != null) mediaPlayer.dispose();
                mediaPlayer = new MediaPlayer(media);
                MediaView mediaView = new MediaView(mediaPlayer);

                StackPane root = new StackPane();
                root.getChildren().add(mediaView);
                StackPane.setAlignment(mediaView, Pos.CENTER);

                Scene scene = new Scene(root, 768, 576);
                fxPanel.setScene(scene);

                mediaView.fitWidthProperty().bind(scene.widthProperty());
                mediaView.fitHeightProperty().bind(scene.heightProperty());
                mediaView.setPreserveRatio(false);

                mediaPlayer.setVolume(0.7);

                if (loop) {
                    mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                } else {
                    mediaPlayer.setCycleCount(1);
                    if (onFinished != null) mediaPlayer.setOnEndOfMedia(onFinished);
                }
                mediaPlayer.play();

                animAlphaPlay = 0.0f; animScalePlay = 0.4f;
                animAlphaSettings = 0.0f; animScaleSettings = 0.4f;
                animAlphaExit = 0.0f; animScaleExit = 0.4f;

                SwingUtilities.invokeLater(this::iniciarAnimacionBotones);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void stopVideo() {
        stopTimer();
        if (mediaPlayer != null) {
            javafx.application.Platform.runLater(() -> mediaPlayer.stop());
        }
    }
}