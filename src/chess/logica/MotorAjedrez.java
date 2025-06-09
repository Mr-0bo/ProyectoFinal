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
        // Turno inicial
        this.turnoActual = Color.BLANCO;
        historialMovimientos = new ArrayList<>();
        objetivoEnPassant = null;
        capturasBlancas = new ArrayList<>();
        capturasNegras = new ArrayList<>();
    }

    public Color getTurnoActual() {
        return turnoActual;
    }

    private void cambiarTurno() {
        turnoActual = (turnoActual == Color.BLANCO) ? Color.NEGRO : Color.BLANCO;
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
            // <numero de jugada> + . + <movimiento realizado> + salto de linea
            sb.append(num).append(". ").append(mov).append("\n");
            num++;
        }
        return sb.toString();
    }

    public boolean hacerMovimiento(int filaOrigen, int colOrigen, int filaDestino, int colDestino) {
        Pieza pieza = tablero.getPieza(filaOrigen, colOrigen);
        // Pieza valida y en turno
        if (pieza == null || pieza.getColor() != turnoActual)
            return false;

        Pieza destino = tablero.getPieza(filaDestino, colDestino);
        // No comas de tu mismo color!
        if (destino != null && destino.getColor() == turnoActual)
            return false;

        // Enroque
        // Que si sea Rey y no se mueva a Asia
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

        // En Passant
        boolean enPassant = false;
        if (pieza instanceof Peon) {
            int direccion = (pieza.getColor() == Color.BLANCO) ? -1 : 1;
            // Movimiento diagonal, direccion corrrecta y destino vacío
            if (Math.abs(colDestino - colOrigen) == 1 && (filaDestino - filaOrigen) == direccion && destino == null) {
                // Captura valida: Casilla vulnerable
                if (objetivoEnPassant != null && objetivoEnPassant[0] == filaDestino && objetivoEnPassant[1] == colDestino) {
                    enPassant = true;
                } else {
                    return false;
                }
            }
        }

        if (!pieza.esMovimientoValido(filaOrigen, colOrigen, filaDestino, colDestino, tablero.tablero))
            return false;

        // Respaldo del tablero
        Pieza[][] respaldo = copiarTablero(tablero.tablero);
        Pieza capturada = tablero.getPieza(filaDestino, colDestino);
        if (enPassant) {
            // Peon a ser capturado
            int filaPeon = (pieza.getColor() == Color.BLANCO) ? filaDestino + 1 : filaDestino - 1;
            capturada = tablero.getPieza(filaPeon, colDestino);
            tablero.tablero[filaPeon][colDestino] = null;
        }

        tablero.moverPieza(filaOrigen, colOrigen, filaDestino, colDestino);
        pieza.setSeMovio(true);

        objetivoEnPassant = null;
        // Primer movimiento
        if (pieza instanceof Peon && Math.abs(filaDestino - filaOrigen) == 2) {
            // Casilla vulnerable
            int filaEP = filaOrigen + ((filaDestino - filaOrigen) / 2);
            objetivoEnPassant = new int[]{filaEP, colOrigen};
        }

        // No dejar en jaque
        if (estaElReyEnJaque(turnoActual)) {
            tablero.tablero = respaldo;
            tablero.actualizarTablero();
            return false;
        }

        // Registrar la captura en el historial
        if (capturada != null) {
            if (pieza.getColor() == Color.BLANCO)
                capturasBlancas.add(capturada);
            else
                capturasNegras.add(capturada);
        }

        // Actualizar historial y tablero tras captura
        String notacion = convertirNotacion(filaOrigen, colOrigen, filaDestino, colDestino, pieza, capturada);
        historialMovimientos.add(notacion);
        cambiarTurno();
        tablero.actualizarTablero();
        return true;
    }

    // Respaldos
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
        // Centinelas
        int filaRey = -1, colRey = -1;
        // Busqueda
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Pieza p = tablero.tablero[i][j];
                if (p instanceof Rey && p.getColor() == color) {
                    filaRey = i;
                    colRey = j;
                    break;
                }
            }
            // Fin
            if (filaRey != -1)
                break;
        }
        // No hay rey? wtf
        if (filaRey == -1)
            return true;
        // Busqueda de enemigos
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // Enemigo valido
                Pieza p = tablero.tablero[i][j];
                if (p != null && p.getColor() != color) {
                    // Test
                    if (p.esMovimientoValido(i, j, filaRey, colRey, tablero.tablero))
                        return true;
                }
            }
        }
        return false;
    }

    public boolean tieneMovimientoLegal(Color color) {
        // Busqueda
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // Pieza encontrada
                Pieza p = tablero.tablero[i][j];
                if (p != null && p.getColor() == color) {
                    // Casillas destino
                    for (int filaDestino = 0; filaDestino < 8; filaDestino++) {
                        for (int colDestino = 0; colDestino < 8; colDestino++) {
                            // No se movió
                            if (i == filaDestino && j == colDestino)
                                continue;
                            Pieza[][] respaldo = copiarTablero(tablero.tablero);
                            // Test
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
        // Pieza a mover valida
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
        // true: Ilegal (TRUE)
        // false: Legal (FALSE)
        return !enJaque;
    }

    // VERIFICACIÓN DEL ENROQUE
    private boolean puedeEnrocar(int filaOrigen, int colOrigen, int filaDestino, int colDestino) {
        Pieza rey = tablero.getPieza(filaOrigen, colOrigen);
        if (!(rey instanceof Rey) || rey.getSeMovio())
            return false;
        int diffCol = colDestino - colOrigen;
        if (Math.abs(diffCol) != 2)
            return false;
        // -2: Columna 7
        // 2: Columna 0
        int colTorre = (diffCol > 0) ? 7 : 0;
        Pieza torre = tablero.getPieza(filaOrigen, colTorre);
        if (torre == null || !(torre instanceof Torre) || torre.getSeMovio())
            return false;
        int inicio = Math.min(colOrigen, colTorre) + 1;
        int fin = Math.max(colOrigen, colTorre) - 1;
        // Busqueda de obstaculos: Piezas
        for (int c = inicio; c <= fin; c++) {
            if (tablero.getPieza(filaOrigen, c) != null)
                return false;
        }
        // Busqueda de obstaculos: Ataques en casillas intermedias
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
        // Se mueve el rey y se guarda
        tablero.moverPieza(filaOrigen, colOrigen, filaDestino, colDestino);
        Pieza rey = tablero.getPieza(filaDestino, colDestino);
        rey.setSeMovio(true);
        // Mayor: Movimiento hacia la derecha
        // Menor: Movimiento a la izquierda
        if (colDestino > colOrigen) {
            Pieza torre = tablero.getPieza(filaOrigen, 7);
            // -1 con respecto al destino del rey
            tablero.moverPieza(filaOrigen, 7, filaOrigen, colDestino - 1);
            if (torre != null)
                torre.setSeMovio(true);
        } else {
            Pieza torre = tablero.getPieza(filaOrigen, 0);
            // +1 con respecto al destino del rey
            tablero.moverPieza(filaOrigen, 0, filaOrigen, colDestino + 1);
            if (torre != null)
                torre.setSeMovio(true);
        }
        tablero.actualizarTablero();
    }

    private String convertirNotacion(int filaOrigen, int colOrigen, int filaDestino, int colDestino, Pieza pieza, Pieza capturada) {
        // 97: a
        // 98: b
        // 99: c
        // 100: d
        // 101: e
        // 102: f
        // 103: g
        // 104: h
        // simbolo + columna + fila + <pieza capturada>
        String origen = "" + (char) ('a' + colOrigen) + (8 - filaOrigen);
        String destino = "" + (char) ('a' + colDestino) + (8 - filaDestino);
        String notacion = pieza.getSimbolo() + ": " + origen + "-" + destino;
        if (capturada != null)
            notacion += " x" + capturada.getSimbolo();
        if (pieza instanceof Peon && Math.abs(colDestino - colOrigen) == 1 && capturada == null)
            notacion += " (en passant)";
        return notacion;
    }
}
