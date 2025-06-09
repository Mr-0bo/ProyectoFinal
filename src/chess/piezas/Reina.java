package chess.piezas;

import chess.logica.Color;
import chess.gui.Tablero;

public class Reina extends Pieza {
    public Reina(Color color) {
        super(color);
    }

    @Override
    public String getSimbolo() {
        return (color == Color.BLANCO) ? "\u2655" : "\u265B";
    }

    @Override
    public boolean esMovimientoValido(int filaOrigen, int columnaOrigen,
                                      int filaDestino, int columnaDestino,
                                      Pieza[][] tablero) {
        int diffFila = Math.abs(filaDestino - filaOrigen);
        int diffCol = Math.abs(columnaDestino - columnaOrigen);
        // Misma fila o misma columna o diagonal
        if (filaOrigen == filaDestino || columnaOrigen == columnaDestino || diffFila == diffCol) {
            return Tablero.caminoLibre(filaOrigen, columnaOrigen, filaDestino, columnaDestino, tablero);
        }
        return false;
    }
}
