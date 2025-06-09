package chess.piezas;

import chess.logica.Color;

public class Rey extends Pieza {
    public Rey(Color color) {
        super(color);
    }

    @Override
    public String getSimbolo() {
        return (color == Color.BLANCO) ? "\u2654" : "\u265A";
    }

    @Override
    public boolean esMovimientoValido(int filaOrigen, int columnaOrigen,
                                      int filaDestino, int columnaDestino,
                                      Pieza[][] tablero) {
        int diffFila = Math.abs(filaDestino - filaOrigen);
        int diffCol = Math.abs(columnaDestino - columnaOrigen);
        // Una casilla hacia cualquier lado, sin quedarse quieto al "moverse"
        return (diffFila <= 1 && diffCol <= 1) && !(diffFila == 0 && diffCol == 0);
    }
}
