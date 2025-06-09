package chess.piezas;

import chess.logica.Color;
import chess.gui.Tablero;

public class Alfil extends Pieza {
    public Alfil(Color color) {
        super(color);
    }

    @Override
    public String getSimbolo() {
        return (color == Color.BLANCO) ? "\u2657" : "\u265D";
    }

    @Override
    // Movimiento en diagonal
    public boolean esMovimientoValido(int filaOrigen, int columnaOrigen,
                                      int filaDestino, int columnaDestino,
                                      Pieza[][] tablero) {
        int diffFila = Math.abs(filaDestino - filaOrigen);
        int diffCol = Math.abs(columnaDestino - columnaOrigen);
        if (diffFila == diffCol) {
            return Tablero.caminoLibre(filaOrigen, columnaOrigen, filaDestino, columnaDestino, tablero);
        }
        return false;
    }
}
