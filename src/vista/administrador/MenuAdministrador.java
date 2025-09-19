package vista.administrador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.entidades.Usuario;
import vista.formularios.GestorArchivos;

public class MenuAdministrador extends JFrame {
    private Usuario usuario;
    private JButton btnGestionBases;
    private JButton btnGestionUsuarios;
    private JButton btnConfiguracion;
    private JButton btnReportes;
    private JButton btnSalir;

    public MenuAdministrador(Usuario usuario) {
        this.usuario = usuario;
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Sistema Experto - Administrador: " + usuario.getNombreUsuario());
        setSize(500, 400);
        setLocationRelativeTo(null);

        // Crear barra de menú
        JMenuBar menuBar = new JMenuBar();

        // Menú Archivo
        JMenu menuArchivo = new JMenu("Archivo");

        JMenuItem itemGestorArchivos = new JMenuItem("Gestión de Archivos...");
        itemGestorArchivos.addActionListener(e -> {
            GestorArchivos gestor = new GestorArchivos(this, usuario);
            gestor.setVisible(true);
        });

        JMenuItem itemLogout = new JMenuItem("Cambiar Usuario");
        itemLogout.addActionListener(e -> {
            volverAlLogin();
        });

        JMenuItem itemSalir = new JMenuItem("Salir del Sistema");
        itemSalir.addActionListener(e -> {
            dispose();
            System.exit(0);
        });

        menuArchivo.add(itemGestorArchivos);
        menuArchivo.addSeparator();
        menuArchivo.add(itemLogout);
        menuArchivo.add(itemSalir);

        // Menú Herramientas
        JMenu menuHerramientas = new JMenu("Herramientas");

        JMenuItem itemConfiguracion = new JMenuItem("Configuración");
        itemConfiguracion.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Función en desarrollo", "Info", JOptionPane.INFORMATION_MESSAGE);
        });

        JMenuItem itemReportes = new JMenuItem("Reportes");
        itemReportes.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Función en desarrollo", "Info", JOptionPane.INFORMATION_MESSAGE);
        });


        menuHerramientas.add(itemConfiguracion);
        menuHerramientas.add(itemReportes);

        // Menú Ayuda
        JMenu menuAyuda = new JMenu("Ayuda");

        JMenuItem itemAcercaDe = new JMenuItem("Acerca de...");
        itemAcercaDe.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Sistema Experto Universal v1.0\n" +
                "Desarrollado en Java con Swing\n" +
                "Funcionalidades de exportación e importación incluidas",
                "Acerca de", JOptionPane.INFORMATION_MESSAGE);
        });

        menuAyuda.add(itemAcercaDe);

        menuBar.add(menuArchivo);
        menuBar.add(menuHerramientas);
        menuBar.add(menuAyuda);

        setJMenuBar(menuBar);

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        JPanel panelTitulo = new JPanel();
        JLabel lblTitulo = new JLabel("MENÚ ADMINISTRADOR");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelTitulo.add(lblTitulo);

        JPanel panelBotones = new JPanel(new GridLayout(6, 1, 10, 10));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        btnGestionBases = new JButton("Gestión de Bases de Conocimiento");
        btnGestionUsuarios = new JButton("Gestión de Usuarios");
        btnConfiguracion = new JButton("Configuración del Sistema");
        btnReportes = new JButton("Reportes y Estadísticas");
        JButton btnLogout = new JButton("Cambiar Usuario");
        btnSalir = new JButton("Salir del Sistema");

        btnGestionBases.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GestionBaseConocimiento(MenuAdministrador.this, usuario).setVisible(true);
            }
        });

        btnGestionUsuarios.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MenuAdministrador.this,
                    "Función en desarrollo", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnConfiguracion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MenuAdministrador.this,
                    "Función en desarrollo", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnReportes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MenuAdministrador.this,
                    "Función en desarrollo", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                volverAlLogin();
            }
        });

        btnSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                System.exit(0);
            }
        });

        panelBotones.add(btnGestionBases);
        panelBotones.add(btnGestionUsuarios);
        panelBotones.add(btnConfiguracion);
        panelBotones.add(btnReportes);
        panelBotones.add(btnLogout);
        panelBotones.add(btnSalir);

        panelPrincipal.add(panelTitulo, BorderLayout.NORTH);
        panelPrincipal.add(panelBotones, BorderLayout.CENTER);

        add(panelPrincipal);
    }

    private void volverAlLogin() {
        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de que desea cambiar de usuario?\n" +
            "Volverá a la pantalla de login.",
            "Cambiar Usuario",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (respuesta == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                vista.login.LoginPrincipal login = new vista.login.LoginPrincipal();
                login.setVisible(true);
            });
        }
    }
}