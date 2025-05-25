package chess.piezas;

import chess.logica.Color;

public class Caballo extends Pieza {
    public Caballo(Color color) {
        super(color);
    }

    @Override
    public String getSimbolo() {
        return (color == Color.BLANCO) ? "\u2658" : "\u265E";
    }

    @Override
    public boolean esMovimientoValido(int filaOrigen, int columnaOrigen,
                                      int filaDestino, int columnaDestino,
                                      Pieza[][] tablero) {
        int diffFila = Math.abs(filaDestino - filaOrigen);
        int diffCol = Math.abs(columnaDestino - columnaOrigen);
        return (diffFila == 2 && diffCol == 1) || (diffFila == 1 && diffCol == 2);
    }
}
