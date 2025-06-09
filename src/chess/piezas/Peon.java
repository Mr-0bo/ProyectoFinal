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
        // Direccion y fila de inicio segun color
        int direccion = (color == Color.BLANCO) ? -1 : 1;
        int filaInicio = (color == Color.BLANCO) ? 6 : 1;
        int diffFila = filaDestino - filaOrigen;
        int diffCol = columnaDestino - columnaOrigen;

        // Movimiento hacia adelante de una casillla
        if (diffCol == 0 && diffFila == direccion && tablero[filaDestino][columnaDestino] == null) {
            return true;
        }
        // Movimiento doble en el primer movimiento.
        // No diferencia de columnas y direccion -2/2 (blancas y negras respectivamente) y casilla intermedia vacia y
        // casilla destino vacia
        if (diffCol == 0 && filaOrigen == filaInicio && diffFila == 2 * direccion &&
                tablero[filaOrigen + direccion][columnaOrigen] == null && tablero[filaDestino][columnaDestino] == null) {
            return true;
        }
        // Captura diagonal normal.
        // Diferencia de columna y direccion de -1/1 y casilla destino no vacia y ocupada por pieza de otro color
        if (Math.abs(diffCol) == 1 && diffFila == direccion &&
                tablero[filaDestino][columnaDestino] != null &&
                tablero[filaDestino][columnaDestino].getColor() != color) {
            return true;
        }
        return false;
    }
}
