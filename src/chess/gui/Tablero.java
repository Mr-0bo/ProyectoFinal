package chess.gui;

import chess.logica.Color;
import chess.piezas.Pieza;
import chess.piezas.*;

import javax.swing.*;
import java.awt.*;

public class Tablero extends JPanel {
    private JButton[][] celdas;
    public Pieza[][] tablero;  // Representación interna del tablero

    public Tablero() {
        // Para que se vean las casillas
        this.setLayout(new GridLayout(8, 8, 2, 2));
        celdas = new JButton[8][8];
        tablero = new Pieza[8][8];
        inicializarTablero();
        inicializarCeldas();
        actualizarTablero();
    }

    public void inicializarTablero() {
        // Acomodo Negras
        tablero[0][0] = new Torre(Color.NEGRO);
        tablero[0][1] = new Caballo(Color.NEGRO);
        tablero[0][2] = new Alfil(Color.NEGRO);
        tablero[0][3] = new Reina(Color.NEGRO);
        tablero[0][4] = new Rey(Color.NEGRO);
        tablero[0][5] = new Alfil(Color.NEGRO);
        tablero[0][6] = new Caballo(Color.NEGRO);
        tablero[0][7] = new Torre(Color.NEGRO);
        for (int i = 0; i < 8; i++) {
            tablero[1][i] = new Peon(Color.NEGRO);
        }
        // Acomodo Blancas
        tablero[7][0] = new Torre(Color.BLANCO);
        tablero[7][1] = new Caballo(Color.BLANCO);
        tablero[7][2] = new Alfil(Color.BLANCO);
        tablero[7][3] = new Reina(Color.BLANCO);
        tablero[7][4] = new Rey(Color.BLANCO);
        tablero[7][5] = new Alfil(Color.BLANCO);
        tablero[7][6] = new Caballo(Color.BLANCO);
        tablero[7][7] = new Torre(Color.BLANCO);
        for (int i = 0; i < 8; i++) {
            tablero[6][i] = new Peon(Color.BLANCO);
        }
        // Casillas vacías
        for (int fila = 2; fila <= 5; fila++) {
            for (int col = 0; col < 8; col++) {
                tablero[fila][col] = null;
            }
        }
    }

    public void inicializarCeldas() {
        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {
                JButton boton = new JButton();
                boton.setFont(new Font("Arial", Font.PLAIN, 32));
                if ((fila + col) % 2 == 0) {
                    boton.setBackground(new java.awt.Color(245, 222, 179)); // blancas
                } else {
                    boton.setBackground(new java.awt.Color(139, 69, 19)); // negras
                }
                boton.setOpaque(true);
                boton.setBorderPainted(false);
                celdas[fila][col] = boton;
                this.add(boton);
            }
        }
    }

    public void actualizarTablero() {
        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {
                JButton boton = celdas[fila][col];
                Pieza p = tablero[fila][col];
                boton.setText(p != null ? p.getSimbolo() : "");
            }
        }
    }

    // Muestra la casilla en la posición indicada.
    public JButton getCelda(int fila, int col) {
        return celdas[fila][col];
    }

    // Muestra la pieza en la posición indicada.
    public Pieza getPieza(int fila, int col) {
        return tablero[fila][col];
    }

    // Actualiza tras movimiento
    public void moverPieza(int filaOrigen, int colOrigen, int filaDestino, int colDestino) {
        tablero[filaDestino][colDestino] = tablero[filaOrigen][colOrigen];
        tablero[filaOrigen][colOrigen] = null;
        actualizarTablero();
    }

    // Notación a arreglo
    public static int[] parsearCoordenada(String coord) {
        if (coord.length() != 2) return null;
        char archivo = coord.charAt(0);
        char filaChar = coord.charAt(1);
        // 97: a
        // 98: b
        // 99: c
        // 100: d
        // 101: e
        // 102: f
        // 103: g
        // 104: h
        int col = archivo - 'a';
        // 8 - (filaChar) = Casilla valida
        int fila = 8 - Character.getNumericValue(filaChar);
        // Validacion de rango
        if (fila < 0 || fila > 7 || col < 0 || col > 7) return null;
        return new int[]{fila, col};
    }

    // FEN para Stockfish
    public String getFEN(Color colorActivo) {
        StringBuilder fen = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int vacio = 0;
            for (int j = 0; j < 8; j++) {
                Pieza p = tablero[i][j];
                // +1 por cada casilla vacia
                if (p == null) {
                    vacio++;
                } else {
                    if (vacio > 0) {
                        fen.append(vacio);
                        // Reinicio del contador
                        vacio = 0;
                    }
                    fen.append(getSimboloFEN(p));
                }
            }
            if (vacio > 0)
                fen.append(vacio);
            if (i < 7)
                // Separador entre filas
                fen.append("/");
        }
        fen.append(" ");
        fen.append(colorActivo == Color.BLANCO ? "w" : "b");
        // Flancos De Enroque - Casilla De En Passant - Medios Movimientos - Numero De jugada
        fen.append(" - - 0 1");
        return fen.toString();
    }

    private String getSimboloFEN(Pieza p) {
        if (p instanceof Rey)
            return p.getColor() == Color.BLANCO ? "K" : "k";
        if (p instanceof Reina)
            return p.getColor() == Color.BLANCO ? "Q" : "q";
        if (p instanceof Torre)
            return p.getColor() == Color.BLANCO ? "R" : "r";
        if (p instanceof Alfil)
            return p.getColor() == Color.BLANCO ? "B" : "b";
        if (p instanceof Caballo)
            return p.getColor() == Color.BLANCO ? "N" : "n";
        if (p instanceof Peon)
            return p.getColor() == Color.BLANCO ? "P" : "p";
        return "";
    }

    public static boolean caminoLibre(int filaOrigen, int colOrigen, int filaDestino, int colDestino, Pieza[][] tablero) {
        // Destino > Origen: 1, Hacia abajo/derecha
        // Destino < filaOrigen: -1, Hacia arriba/izquierda
        // Destino = Origen: Sin movimiento en esa direccion
        int pasoFila = Integer.compare(filaDestino, filaOrigen);
        int pasoCol = Integer.compare(colDestino, colOrigen);
        // Se ignora casilla de origen
        int filaActual = filaOrigen + pasoFila;
        int colActual = colOrigen + pasoCol;
        while (filaActual != filaDestino || colActual != colDestino) {
            if (tablero[filaActual][colActual] != null)
                //Casilla ocupada
                return false;
            filaActual += pasoFila;
            colActual += pasoCol;
        }
        return true;
    }
}
