package vista.administrador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.entidades.Usuario;
import vista.formularios.GestorArchivos;
import vista.comun.InfoSistema;

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

        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> {
            dispose();
            System.exit(0);
        });

        menuArchivo.add(itemGestorArchivos);
        menuArchivo.addSeparator();
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

        JMenuItem itemInfoSistema = new JMenuItem("Información del Sistema");
        itemInfoSistema.addActionListener(e -> {
            InfoSistema.mostrar(this, usuario);
        });

        menuHerramientas.add(itemConfiguracion);
        menuHerramientas.add(itemReportes);
        menuHerramientas.addSeparator();
        menuHerramientas.add(itemInfoSistema);

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

        JPanel panelBotones = new JPanel(new GridLayout(5, 1, 10, 10));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        btnGestionBases = new JButton("Gestión de Bases de Conocimiento");
        btnGestionUsuarios = new JButton("Gestión de Usuarios");
        btnConfiguracion = new JButton("Configuración del Sistema");
        btnReportes = new JButton("Reportes y Estadísticas");
        btnSalir = new JButton("Salir");

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
        panelBotones.add(btnSalir);

        panelPrincipal.add(panelTitulo, BorderLayout.NORTH);
        panelPrincipal.add(panelBotones, BorderLayout.CENTER);

        add(panelPrincipal);
    }
}