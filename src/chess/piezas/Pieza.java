package chess.piezas;

import chess.logica.Color;

public abstract class Pieza {
    protected Color color;
    protected boolean seMovio = false;

    public Pieza(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public boolean getSeMovio() {
        return seMovio;
    }

    public void setSeMovio(boolean seMovio) {
        this.seMovio = seMovio;
    }

    // Devuelve el símbolo Unicode de la pieza.
    public abstract String getSimbolo();

    // Valida el movimiento según la lógica propia de la pieza
    // (sin considerar enroque, en passant o promoción, que se gestionan en el motor).
    public abstract boolean esMovimientoValido(int filaOrigen, int columnaOrigen,
                                               int filaDestino, int columnaDestino,
                                               Pieza[][] tablero);
}
