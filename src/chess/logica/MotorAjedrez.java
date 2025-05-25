package chess.logica;

import chess.gui.Tablero;
import chess.piezas.*;

import java.util.ArrayList;
import java.util.List;

public class MotorAjedrez {
    private Tablero tablero;
    private Color turnoActual;
    private List<String> historialMovimientos;
    private int[] objetivoEnPassant;

    private List<Pieza> capturasBlancas;  // Piezas negras capturadas
    private List<Pieza> capturasNegras;   // Lo otro

    public MotorAjedrez(Tablero tablero) {
        this.tablero = tablero;
        this.turnoActual = Color.BLANCO;
        historialMovimientos = new ArrayList<>();
        objetivoEnPassant = null;
        capturasBlancas = new ArrayList<>();
        capturasNegras = new ArrayList<>();
    }

    public Color getTurnoActual() {
        return turnoActual;
    }

    public List<String> getHistorialMovimientos() {
        return historialMovimientos;
    }

    public List<Pieza> getCapturasBlancas() {
        return capturasBlancas;
    }

    public List<Pieza> getCapturasNegras() {
        return capturasNegras;
    }

    // Puntos
    public int getPuntosCapturas(List<Pieza> capturas) {
        int suma = 0;
        for (Pieza p : capturas) {
            if (p instanceof Reina) suma += 9;
            else if (p instanceof Torre) suma += 5;
            else if (p instanceof Alfil) suma += 3;
            else if (p instanceof Caballo) suma += 3;
            else if (p instanceof Peon) suma += 1;
        }
        return suma;
    }

    // Nuevo formato del historial: una línea por movimiento, numerado.
    public String getHistorialFormateado() {
        StringBuilder sb = new StringBuilder();
        int num = 1;
        for (String mov : historialMovimientos) {
            sb.append(num).append(". ").append(mov).append("\n");
            num++;
        }
        return sb.toString();
    }

    public boolean hacerMovimiento(int filaOrigen, int colOrigen, int filaDestino, int colDestino) {
        Pieza pieza = tablero.getPieza(filaOrigen, colOrigen);
        if (pieza == null || pieza.getColor() != turnoActual)
            return false;

        Pieza destino = tablero.getPieza(filaDestino, colDestino);
        if (destino != null && destino.getColor() == turnoActual)
            return false;

        // Enroque
        if (pieza instanceof Rey && Math.abs(colDestino - colOrigen) == 2 && filaOrigen == filaDestino) {
            if (!puedeEnrocar(filaOrigen, colOrigen, filaDestino, colDestino))
                return false;
            realizarEnroque(filaOrigen, colOrigen, filaDestino, colDestino);
            String notacionEnroque = (colDestino > colOrigen) ? "O-O" : "O-O-O";
            historialMovimientos.add(notacionEnroque);
            cambiarTurno();
            objetivoEnPassant = null;
            tablero.actualizarTablero();
            return true;
        }

        // En passant (no sirve lol)
        boolean enPassant = false;
        if (pieza instanceof Peon) {
            int direccion = (pieza.getColor() == Color.BLANCO) ? -1 : 1;
            if (Math.abs(colDestino - colOrigen) == 1 && (filaDestino - filaOrigen) == direccion && destino == null) {
                if (objetivoEnPassant != null && objetivoEnPassant[0] == filaDestino && objetivoEnPassant[1] == colDestino) {
                    enPassant = true;
                } else {
                    return false;
                }
            }
        }

        if (!pieza.esMovimientoValido(filaOrigen, colOrigen, filaDestino, colDestino, tablero.tablero))
            return false;

        // Respaldar el tablero.
        Pieza[][] respaldo = copiarTablero(tablero.tablero);
        Pieza capturada = tablero.getPieza(filaDestino, colDestino);
        if (enPassant) {
            int filaPeon = (pieza.getColor() == Color.BLANCO) ? filaDestino + 1 : filaDestino - 1;
            capturada = tablero.getPieza(filaPeon, colDestino);
            tablero.tablero[filaPeon][colDestino] = null;
        }

        tablero.moverPieza(filaOrigen, colOrigen, filaDestino, colDestino);
        pieza.setSeMovio(true);

        // Actualizar en passant si el peón se mueve dos casillas.
        objetivoEnPassant = null;
        if (pieza instanceof Peon && Math.abs(filaDestino - filaOrigen) == 2) {
            int filaEP = filaOrigen + ((filaDestino - filaOrigen) / 2);
            objetivoEnPassant = new int[]{filaEP, colOrigen};
        }

        if (estaElReyEnJaque(turnoActual)) {
            tablero.tablero = respaldo;
            tablero.actualizarTablero();
            return false;
        }

        // Registrar la captura.
        if (capturada != null) {
            if (pieza.getColor() == Color.BLANCO)
                capturasBlancas.add(capturada);
            else
                capturasNegras.add(capturada);
        }

        String notacion = convertirNotacion(filaOrigen, colOrigen, filaDestino, colDestino, pieza, capturada);
        historialMovimientos.add(notacion);
        cambiarTurno();
        tablero.actualizarTablero();
        return true;
    }

    private Pieza[][] copiarTablero(Pieza[][] orig) {
        Pieza[][] copia = new Pieza[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                copia[i][j] = orig[i][j];
            }
        }
        return copia;
    }

    public boolean estaElReyEnJaque(Color color) {
        int filaRey = -1, colRey = -1;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Pieza p = tablero.tablero[i][j];
                if (p instanceof Rey && p.getColor() == color) {
                    filaRey = i;
                    colRey = j;
                    break;
                }
            }
            if (filaRey != -1)
                break;
        }
        if (filaRey == -1)
            return true;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Pieza p = tablero.tablero[i][j];
                if (p != null && p.getColor() != color) {
                    if (p.esMovimientoValido(i, j, filaRey, colRey, tablero.tablero))
                        return true;
                }
            }
        }
        return false;
    }

    public boolean tieneMovimientoLegal(Color color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Pieza p = tablero.tablero[i][j];
                if (p != null && p.getColor() == color) {
                    for (int filaDestino = 0; filaDestino < 8; filaDestino++) {
                        for (int colDestino = 0; colDestino < 8; colDestino++) {
                            if (i == filaDestino && j == colDestino)
                                continue;
                            Pieza[][] respaldo = copiarTablero(tablero.tablero);
                            boolean valido = hacerMovimientoTest(i, j, filaDestino, colDestino);
                            tablero.tablero = respaldo;
                            tablero.actualizarTablero();
                            if (valido)
                                return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean hacerMovimientoTest(int filaOrigen, int colOrigen, int filaDestino, int colDestino) {
        Pieza pieza = tablero.tablero[filaOrigen][colOrigen];
        if (pieza == null)
            return false;
        if (!pieza.esMovimientoValido(filaOrigen, colOrigen, filaDestino, colDestino, tablero.tablero))
            return false;
        Pieza[][] respaldo = copiarTablero(tablero.tablero);
        tablero.moverPieza(filaOrigen, colOrigen, filaDestino, colDestino);
        boolean enJaque = estaElReyEnJaque(pieza.getColor());
        tablero.tablero = respaldo;
        tablero.actualizarTablero();
        return !enJaque;
    }

    // VERIFICACIÓN DEL ENROQUE:
    // Esta versión simula el paso del rey por las casillas intermedias (excluyendo la posición inicial)
    // y verifica que en cada una de ellas el rey no quede en jaque.
    private boolean puedeEnrocar(int filaOrigen, int colOrigen, int filaDestino, int colDestino) {
        Pieza rey = tablero.getPieza(filaOrigen, colOrigen);
        if (!(rey instanceof Rey) || rey.getSeMovio())
            return false;
        int diffCol = colDestino - colOrigen;
        if (Math.abs(diffCol) != 2)
            return false;
        int colTorre = (diffCol > 0) ? 7 : 0;
        Pieza torre = tablero.getPieza(filaOrigen, colTorre);
        if (torre == null || !(torre instanceof Torre) || torre.getSeMovio())
            return false;
        int inicio = Math.min(colOrigen, colTorre) + 1;
        int fin = Math.max(colOrigen, colTorre) - 1;
        for (int c = inicio; c <= fin; c++) {
            if (tablero.getPieza(filaOrigen, c) != null)
                return false;
        }
        // Verificar que las casillas intermedias por las que pasa el rey estén libres de ataques.
        int paso = (diffCol > 0) ? 1 : -1;
        for (int c = colOrigen + paso; c != colDestino + paso; c += paso) {
            Pieza[][] respaldo = copiarTablero(tablero.tablero);
            tablero.moverPieza(filaOrigen, colOrigen, filaOrigen, c);
            if (estaElReyEnJaque(rey.getColor())) {
                tablero.tablero = respaldo;
                tablero.actualizarTablero();
                return false;
            }
            tablero.tablero = respaldo;
            tablero.actualizarTablero();
        }
        return true;
    }

    private void realizarEnroque(int filaOrigen, int colOrigen, int filaDestino, int colDestino) {
        tablero.moverPieza(filaOrigen, colOrigen, filaDestino, colDestino);
        Pieza rey = tablero.getPieza(filaDestino, colDestino);
        rey.setSeMovio(true);
        if (colDestino > colOrigen) {
            Pieza torre = tablero.getPieza(filaOrigen, 7);
            tablero.moverPieza(filaOrigen, 7, filaOrigen, colDestino - 1);
            if (torre != null)
                torre.setSeMovio(true);
        } else {
            Pieza torre = tablero.getPieza(filaOrigen, 0);
            tablero.moverPieza(filaOrigen, 0, filaOrigen, colDestino + 1);
            if (torre != null)
                torre.setSeMovio(true);
        }
        tablero.actualizarTablero();
    }

    private String convertirNotacion(int filaOrigen, int colOrigen, int filaDestino, int colDestino, Pieza pieza, Pieza capturada) {
        String origen = "" + (char) ('a' + colOrigen) + (8 - filaOrigen);
        String destino = "" + (char) ('a' + colDestino) + (8 - filaDestino);
        String notacion = pieza.getSimbolo() + ": " + origen + "-" + destino;
        if (capturada != null)
            notacion += " x" + capturada.getSimbolo();
        if (pieza instanceof Peon && Math.abs(colDestino - colOrigen) == 1 && capturada == null)
            notacion += " (en passant)";
        return notacion;
    }

    private void cambiarTurno() {
        turnoActual = (turnoActual == Color.BLANCO) ? Color.NEGRO : Color.BLANCO;
    }
}

