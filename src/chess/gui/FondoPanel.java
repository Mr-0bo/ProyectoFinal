package chess.gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class FondoPanel extends JPanel {
    private BufferedImage imagenFondo;

    public FondoPanel(String rutaImagen) {
        try {
            imagenFondo = ImageIO.read(new File(rutaImagen));
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen de fondo: " + e.getMessage());
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (imagenFondo != null) {
            return new Dimension(imagenFondo.getWidth(), imagenFondo.getHeight());
        } else {
            return super.getPreferredSize();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagenFondo != null) {
            // Dimensiones de la imagen
            g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
