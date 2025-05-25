package chess.piezas;

import chess.logica.Color;

public class Peon extends Pieza {
    public Peon(Color color) {
        super(color);
    }

    @Override
    public String getSimbolo() {
        return (color == Color.BLANCO) ? "\u2659" : "\u265F";
    }

    @Override
    public boolean esMovimientoValido(int filaOrigen, int columnaOrigen,
                                      int filaDestino, int columnaDestino,
                                      Pieza[][] tablero) {
        int direccion = (color == Color.BLANCO) ? -1 : 1;
        int filaInicio = (color == Color.BLANCO) ? 6 : 1;
        int diffFila = filaDestino - filaOrigen;
        int diffCol = columnaDestino - columnaOrigen;

        // Movimiento hacia adelante de una casilla y destino vacío.
        if (diffCol == 0 && diffFila == direccion && tablero[filaDestino][columnaDestino] == null) {
            return true;
        }
        // Movimiento doble en el primer movimiento.
        if (diffCol == 0 && filaOrigen == filaInicio && diffFila == 2 * direccion &&
                tablero[filaOrigen + direccion][columnaOrigen] == null && tablero[filaDestino][columnaDestino] == null) {
            return true;
        }
        // Captura diagonal normal.
        if (Math.abs(diffCol) == 1 && diffFila == direccion &&
                tablero[filaDestino][columnaDestino] != null &&
                tablero[filaDestino][columnaDestino].getColor() != color) {
            return true;
        }
        // La captura "en passant" se gestiona en el motor.
        return false;
    }
}

