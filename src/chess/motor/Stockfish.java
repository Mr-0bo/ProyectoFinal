package chess.motor;

import java.io.*;

public class Stockfish {
    private Process procesoMotor;
    private BufferedReader lector;
    private BufferedWriter escritor;

    public Stockfish(String rutaMotor) {
        try {
            ProcessBuilder pb = new ProcessBuilder(rutaMotor);
            procesoMotor = pb.start();
            lector = new BufferedReader(new InputStreamReader(procesoMotor.getInputStream()));
            escritor = new BufferedWriter(new OutputStreamWriter(procesoMotor.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviarComando(String comando) {
        try {
            escritor.write(comando + "\n");
            escritor.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Solicita el mejor movimiento para la posición FEN dada y un tiempo de búsqueda (ms)
    public String obtenerMejorMovimiento(String fen, int tiempoMs) {
        enviarComando("position fen " + fen);
        enviarComando("go movetime " + tiempoMs);
        String mejorMovimiento = "";
        try {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.startsWith("bestmove")) {
                    mejorMovimiento = linea.split(" ")[1];
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mejorMovimiento;
    }

    public void detenerMotor() {
        enviarComando("quit");
        procesoMotor.destroy();
    }
}