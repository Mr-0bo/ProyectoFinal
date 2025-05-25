package chess.gui;

import chess.logica.Color;
import chess.piezas.Pieza;
import chess.piezas.Alfil;
import chess.piezas.Caballo;
import chess.piezas.Reina;
import chess.piezas.Torre;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class DialogoPromocion {
    public static Pieza mostrarDialogoPromocion(Color color) {
        List<String> opciones = Arrays.asList("Reina", "Torre", "Alfil", "Caballo");
        String eleccion = (String) JOptionPane.showInputDialog(
                null,
                "Elige una pieza:",
                "Promoción",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones.toArray(),
                "Reina");
        if (eleccion == null) {
            eleccion = "Reina";
        }
        switch(eleccion) {
            case "Reina":
                return new Reina(color);
            case "Torre":
                return new Torre(color);
            case "Alfil":
                return new Alfil(color);
            case "Caballo":
                return new Caballo(color);
            default:
                return new Reina(color);
        }
    }
}
