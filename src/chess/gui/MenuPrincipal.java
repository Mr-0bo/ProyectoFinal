package chess.gui;

import chess.logica.JuegoAjedrez;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {
        setTitle("Menú Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Imagen
        FondoPanel fondo = new FondoPanel("src/resources/fondo_menu.png");
        fondo.setLayout(new BorderLayout());
        setContentPane(fondo);

        // Título
        JLabel labelTitulo = new JLabel("Ajedrez");
        labelTitulo.setFont(new Font("Serif", Font.BOLD, 72));
        labelTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        fondo.add(labelTitulo, BorderLayout.NORTH);

        // Botones centrales
        JPanel panelBotones = new JPanel();
        panelBotones.setOpaque(false);
        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));

        JButton btnLocal = new JButton("Juego Local");
        JButton btnBot = new JButton("Jugar contra Bot");
        JButton btnSalir = new JButton("Salir");
        Dimension btnDim = new Dimension(250, 40);

        for (JButton btn : new JButton[]{btnLocal, btnBot, btnSalir}) {
            btn.setMaximumSize(btnDim);
            btn.setPreferredSize(btnDim);
            btn.setFont(new Font("Arial", Font.PLAIN, 18));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        panelBotones.add(Box.createVerticalGlue());
        panelBotones.add(btnLocal);
        panelBotones.add(Box.createVerticalStrut(15));
        panelBotones.add(btnBot);
        panelBotones.add(Box.createVerticalStrut(15));
        panelBotones.add(btnSalir);
        panelBotones.add(Box.createVerticalGlue());
        fondo.add(panelBotones, BorderLayout.CENTER);

        // Panel inferior
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setOpaque(false);
        JLabel labelVersion = new JLabel("V. 1.0");

        // Textos
        labelVersion.setFont(new Font("Arial", Font.BOLD, 20));
        labelVersion.setForeground(java.awt.Color.WHITE);
        JLabel labelAutor = new JLabel("Por: Mario García Bonal");
        labelAutor.setFont(new Font("Arial", Font.BOLD, 20));
        labelAutor.setForeground(java.awt.Color.WHITE);
        panelInferior.add(labelVersion, BorderLayout.WEST);
        panelInferior.add(labelAutor, BorderLayout.EAST);
        fondo.add(panelInferior, BorderLayout.SOUTH);

        // Botones
        btnLocal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Si se juega sin IA
                new JuegoAjedrez(false).setVisible(true);
                dispose();
            }
        });

        btnBot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Si se juega con IA
                new JuegoAjedrez(true).setVisible(true);
                dispose();
            }
        });

        btnSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Adaptación
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuPrincipal().setVisible(true);
        });
    }
}
