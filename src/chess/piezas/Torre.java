package chess.piezas;

import chess.logica.Color;
import chess.gui.Tablero;

public class Torre extends Pieza {
    public Torre(Color color) {
        super(color);
    }

    @Override
    public String getSimbolo() {
        return (color == Color.BLANCO) ? "\u2656" : "\u265C";
    }

    @Override
    public boolean esMovimientoValido(int filaOrigen, int columnaOrigen,
                                      int filaDestino, int columnaDestino,
                                      Pieza[][] tablero) {
        // Misma fila o misma columna
        if (filaOrigen == filaDestino || columnaOrigen == columnaDestino) {
            return Tablero.caminoLibre(filaOrigen, columnaOrigen, filaDestino, columnaDestino, tablero);
        }
        return false;
    }
}
