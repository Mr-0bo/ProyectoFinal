package chess.logica;

import chess.motor.Stockfish;
import chess.gui.DialogoDificultad;
import chess.gui.DialogoPromocion;
import chess.gui.MenuPrincipal;
import chess.gui.Tablero;
import chess.piezas.Peon;
import chess.piezas.Pieza;
import java.time.LocalTime;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Paths;

public class JuegoAjedrez extends JFrame {
    private Tablero panelTablero;
    private MotorAjedrez motor;

    // Área para el historial de jugadas
    private JTextArea areaHistorialLateral;
    // Etiquetas para turno y selección
    private JLabel labelTurno;
    private JLabel labelSeleccion;

    // Capturas negras
    private JTextArea areaCapturasNegras;
    // Blancas
    private JTextArea areaCapturasBlancas;

    private boolean vsIA;
    private Stockfish stockfish;
    private int tiempoIA;  // Tiempo en ms para Stockfish

    // Variables para la selección
    private int filaSeleccionada = -1;
    private int columnaSeleccionada = -1;

    public JuegoAjedrez(boolean vsIA) {
        this.vsIA = vsIA;
        setTitle("Juego de Ajedrez - " + (vsIA ? "vs IA" : "Local"));
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponentes();
    }

    private void initComponentes() {
        panelTablero = new Tablero();
        motor = new MotorAjedrez(panelTablero);

        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        System.out.println("Sistema operativo detectado: " + osName + " (" + osVersion + ")");

        String stockfishPath;
        if (osName.toLowerCase().contains("win")) {
            stockfishPath = Paths.get("src", "libs", "stockfish-windows.exe").toString();
        } else if (osName.toLowerCase().contains("mac")) {
            stockfishPath = Paths.get("src", "libs", "stockfish-macos-x86-64-bmi2").toString();
        } else {
            throw new UnsupportedOperationException("Sistema operativo no compatible: " + osName + " (" + osVersion + ")");
        }

        if (vsIA) {
            tiempoIA = DialogoDificultad.mostrarDialogoDificultad();
            stockfish = new Stockfish(stockfishPath);
        }


        // Panel norte
        JPanel panelNorte = new JPanel(new BorderLayout());
        areaCapturasNegras = new JTextArea(1, 30);
        areaCapturasNegras.setEditable(false);
        areaCapturasNegras.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollCapturasNegras = new JScrollPane(areaCapturasNegras);
        scrollCapturasNegras.setPreferredSize(new Dimension(0, 30));
        panelNorte.add(scrollCapturasNegras, BorderLayout.CENTER);

        // Panel sur
        JPanel panelSur = new JPanel(new BorderLayout());
        areaCapturasBlancas = new JTextArea(1, 30);
        areaCapturasBlancas.setEditable(false);
        areaCapturasBlancas.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollCapturasBlancas = new JScrollPane(areaCapturasBlancas);
        scrollCapturasBlancas.setPreferredSize(new Dimension(0, 30));
        panelSur.add(scrollCapturasBlancas, BorderLayout.CENTER);

        // Panel este
        JPanel panelInfoHistorial = new JPanel();
        panelInfoHistorial.setLayout(new BoxLayout(panelInfoHistorial, BoxLayout.Y_AXIS));
        labelTurno = new JLabel("Turno: " + (motor.getTurnoActual() == Color.BLANCO ? "Blancas" : "Negras"));
        labelTurno.setFont(new Font("Arial", Font.PLAIN, 12));
        labelSeleccion = new JLabel("Selección: Ninguna");
        labelSeleccion.setFont(new Font("Arial", Font.PLAIN, 12));
        panelInfoHistorial.add(labelTurno);
        panelInfoHistorial.add(labelSeleccion);
        panelInfoHistorial.add(Box.createVerticalStrut(5));
        areaHistorialLateral = new JTextArea();
        areaHistorialLateral.setEditable(false);
        areaHistorialLateral.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollHistorial = new JScrollPane(areaHistorialLateral);
        scrollHistorial.setPreferredSize(new Dimension(250, 0));
        panelInfoHistorial.add(scrollHistorial);

        // Panel central
        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.add(panelTablero, BorderLayout.CENTER);

        //Panel oeste
        JPanel panelOeste = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton botonMenu = new JButton("Menú Principal");
        botonMenu.setFont(new Font("Arial", Font.PLAIN, 12));
        botonMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MenuPrincipal().setVisible(true);
                dispose();
            }
        });
        panelOeste.add(botonMenu);

        // Organizacion
        setLayout(new BorderLayout());
        add(panelNorte, BorderLayout.NORTH);
        add(panelSur, BorderLayout.SOUTH);
        add(panelCentro, BorderLayout.CENTER);
        add(panelInfoHistorial, BorderLayout.EAST);
        add(panelOeste, BorderLayout.WEST);

        // ActionListener en cada casilla
        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {
                JButton boton = panelTablero.getCelda(fila, col);
                final int f = fila, c = col;
                boton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        manejarClicCelda(f, c);
                    }
                });
            }
        }
    }

    private void manejarClicCelda(int fila, int col) {
        Pieza p = panelTablero.getPieza(fila, col);
        // Sentinela de seleccion
        if (filaSeleccionada == -1 && columnaSeleccionada == -1) {
            // Seleccion para jugador en turno
            if (p != null && p.getColor() == motor.getTurnoActual()) {
                filaSeleccionada = fila;
                columnaSeleccionada = col;
                // Resaltar la celda
                panelTablero.getCelda(fila, col).setBorder(BorderFactory.createLineBorder(java.awt.Color.YELLOW, 3));
                labelSeleccion.setText("Selección: " + p.getSimbolo());
            } else {
                labelSeleccion.setText("Selección: Ninguna");
            }
        } else {
            // TRUE: Se ejecutó
            // FALSE: Ilegal
            boolean movRealizado = motor.hacerMovimiento(filaSeleccionada, columnaSeleccionada, fila, col);
            limpiarBordes();
            actualizarHistorial();
            actualizarCapturas();
            filaSeleccionada = -1;
            columnaSeleccionada = -1;
            labelSeleccion.setText("Selección: Ninguna");
            labelTurno.setText("Turno: " + (motor.getTurnoActual() == Color.BLANCO ? "Blancas" : "Negras"));
            // Promociones
            Pieza movida = panelTablero.getPieza(fila, col);
            // Es peon? Esta en la fila del rival?
            if (movida instanceof Peon) {
                if ((movida.getColor() == Color.BLANCO && fila == 0) ||
                        (movida.getColor() == Color.NEGRO && fila == 7)) {
                    Pieza promocion = DialogoPromocion.mostrarDialogoPromocion(movida.getColor());
                    panelTablero.tablero[fila][col] = promocion;
                    panelTablero.actualizarTablero();
                    // Se actualiza el ultimo movimiento
                    java.util.List<String> hist = motor.getHistorialMovimientos();
                    int index = hist.size() - 1;
                    hist.set(index, hist.get(index) + " (Promoción a " + promocion.getSimbolo() + ")");
                }
            }
            //Verificacion de Mate
            if (motor.estaElReyEnJaque(motor.getTurnoActual()) &&
                    !motor.tieneMovimientoLegal(motor.getTurnoActual())) {
                String ganador = (motor.getTurnoActual() == Color.BLANCO) ? "Negras" : "Blancas";
                JOptionPane.showMessageDialog(this, "¡Jaque mate! Ganan " + ganador, "Fin de partida", JOptionPane.INFORMATION_MESSAGE);
                guardarHistorial(ganador, motor.getHistorialMovimientos());
                labelTurno.setText("Turno: " + (motor.getTurnoActual() == Color.BLANCO ? "Blancas" : "Negras"));
            } else {
                // Si no hay mate, se sigue
                labelTurno.setText("Turno: " + (motor.getTurnoActual() == Color.BLANCO ? "Blancas" : "Negras"));
            }

            if (vsIA && motor.getTurnoActual() == Color.NEGRO) {
                realizarMovimientoIA();
            }
        }
    }

    private void limpiarBordes() {
        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {
                panelTablero.getCelda(fila, col).setBorder(UIManager.getBorder("Button.border"));
            }
        }
    }

    private void actualizarHistorial() {
        areaHistorialLateral.setText(motor.getHistorialFormateado());
    }

    private void actualizarCapturas() {
        StringBuilder sbNegras = new StringBuilder();
        // Capturas
        for (Pieza p : motor.getCapturasNegras()) {
            sbNegras.append(p.getSimbolo()).append(" ");
        }
        // Total + (puntos) + pts
        sbNegras.append(" (Total: ").append(motor.getPuntosCapturas(motor.getCapturasNegras())).append(" pts)");
        areaCapturasNegras.setText(sbNegras.toString());

        StringBuilder sbBlancas = new StringBuilder();
        for (Pieza p : motor.getCapturasBlancas()) {
            sbBlancas.append(p.getSimbolo()).append(" ");
        }
        sbBlancas.append(" (Total: ").append(motor.getPuntosCapturas(motor.getCapturasBlancas())).append(" pts)");
        areaCapturasBlancas.setText(sbBlancas.toString());
    }

    // Debes mejorarlo :p
    private void guardarHistorial(String ganador, java.util.List<String> movimientos) {
        try (java.io.PrintWriter out = new java.io.PrintWriter(new java.io.FileWriter("historialJuegos.txt", true))) {
            LocalTime hora = LocalTime.now();
            out.println("Hora:: " + hora);
            out.println("Ganador: " + ganador);
            out.println(motor.getHistorialFormateado());
            out.println("------");
        // Crashes
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private void realizarMovimientoIA() {
        new Thread(() -> {
            try {
                String fen = panelTablero.getFEN(Color.NEGRO);
                String mejorMov = stockfish.obtenerMejorMovimiento(fen, tiempoIA);
                // Movimiento valido, notacion algebraica
                if (mejorMov != null && mejorMov.length() >= 4) {
                    // Extraer coords origen y destino
                    String origen = mejorMov.substring(0, 2);
                    String destino = mejorMov.substring(2, 4);
                    // Traductor
                    int[] coordOrigen = Tablero.parsearCoordenada(origen);
                    int[] coordDestino = Tablero.parsearCoordenada(destino);
                    SwingUtilities.invokeLater(() -> {
                        // Realizar movimiento
                        motor.hacerMovimiento(coordOrigen[0], coordOrigen[1], coordDestino[0], coordDestino[1]);
                        actualizarHistorial();
                        actualizarCapturas();
                        // Verificacion de mate
                        if (motor.estaElReyEnJaque(motor.getTurnoActual()) &&
                                !motor.tieneMovimientoLegal(motor.getTurnoActual())) {
                            String ganador = (motor.getTurnoActual() == Color.BLANCO) ? "Negras" : "Blancas";
                            JOptionPane.showMessageDialog(this, "¡Jaque mate! Ganan " + ganador, "Fin de partida", JOptionPane.INFORMATION_MESSAGE);
                            guardarHistorial(ganador, motor.getHistorialMovimientos());
                        }
                        labelTurno.setText("Turno: " + (motor.getTurnoActual() == Color.BLANCO ? "Blancas" : "Negras"));
                    });
                }
            // Crashes
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
}
