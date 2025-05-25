package chess.gui;

import javax.swing.*;

public class DialogoDificultad {
    public static int mostrarDialogoDificultad() {
        Object[] opciones = {"Principiante (500 ms)", "Intermedio (1000 ms)", "Experto (2000 ms)", "Magnus Carlsen (2882 ms)"};
        String eleccion = (String) JOptionPane.showInputDialog(
                null,
                "Elige la dificultad:",
                "Dificultad",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                "Intermedio (1000 ms)");
        if (eleccion == null) {
            eleccion = "Intermedio (1000 ms)";
        }
        switch(eleccion) {
            case "Principiante (500 ms)":
                return 500;
            case "Intermedio (1000 ms)":
                return 1000;
            case "Experto (2000 ms)":
                return 2000;
            case "Magnus Carlsen (2882 ms)":
                return 2882;
            default:
                return 1000;
        }
    }
}
