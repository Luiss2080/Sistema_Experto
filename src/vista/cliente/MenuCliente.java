package vista.cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.entidades.Usuario;
import vista.formularios.GestorArchivos;
import vista.comun.InfoSistema;

public class MenuCliente extends JFrame {
    private Usuario usuario;
    private JButton btnConsultaExperto;
    private JButton btnHistorial;
    private JButton btnAyuda;
    private JButton btnSalir;

    public MenuCliente(Usuario usuario) {
        this.usuario = usuario;
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Sistema Experto - Cliente: " + usuario.getNombreUsuario());
        setSize(400, 300);
        setLocationRelativeTo(null);

        // Crear barra de menú
        JMenuBar menuBar = new JMenuBar();

        // Menú Archivo
        JMenu menuArchivo = new JMenu("Archivo");

        JMenuItem itemGestorArchivos = new JMenuItem("Importar Base de Conocimiento...");
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

        // Menú Ayuda
        JMenu menuAyuda = new JMenu("Ayuda");

        JMenuItem itemAcercaDe = new JMenuItem("Acerca de...");
        itemAcercaDe.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Sistema Experto Universal v1.0\n" +
                "Modo Cliente\n" +
                "Consulte bases de conocimiento e importe nuevas",
                "Acerca de", JOptionPane.INFORMATION_MESSAGE);
        });

        JMenuItem itemGuia = new JMenuItem("Guía de Usuario");
        itemGuia.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "GUÍA RÁPIDA:\n\n" +
                "1. Consultar Sistema Experto: Ejecute inferencias\n" +
                "2. Ver Historial: Revise sesiones anteriores\n" +
                "3. Archivo > Importar: Cargue bases de conocimiento\n\n" +
                "Use el selector de algoritmos para elegir entre:\n" +
                "- Forward Chaining (hacia adelante)\n" +
                "- Backward Chaining (hacia atrás)\n" +
                "- Motor Avanzado (múltiples estrategias)",
                "Guía de Usuario", JOptionPane.INFORMATION_MESSAGE);
        });

        JMenuItem itemInfoSistema = new JMenuItem("Información del Sistema");
        itemInfoSistema.addActionListener(e -> {
            InfoSistema.mostrar(this, usuario);
        });

        menuAyuda.add(itemGuia);
        menuAyuda.addSeparator();
        menuAyuda.add(itemInfoSistema);
        menuAyuda.add(itemAcercaDe);

        menuBar.add(menuArchivo);
        menuBar.add(menuAyuda);

        setJMenuBar(menuBar);

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        JPanel panelTitulo = new JPanel();
        JLabel lblTitulo = new JLabel("MENÚ CLIENTE");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelTitulo.add(lblTitulo);

        JPanel panelBotones = new JPanel(new GridLayout(4, 1, 10, 10));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        btnConsultaExperto = new JButton("Consultar Sistema Experto");
        btnHistorial = new JButton("Ver Historial");
        btnAyuda = new JButton("Ayuda");
        btnSalir = new JButton("Salir");

        btnConsultaExperto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ConsultaExperto(MenuCliente.this, usuario).setVisible(true);
            }
        });

        btnHistorial.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new HistorialConsultas(MenuCliente.this, usuario).setVisible(true);
            }
        });

        btnAyuda.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MenuCliente.this,
                    "Sistema Experto v1.0\n\n" +
                    "Para usar el sistema:\n" +
                    "1. Seleccione 'Consultar Sistema Experto'\n" +
                    "2. Elija una base de conocimiento\n" +
                    "3. Complete los datos solicitados\n" +
                    "4. Ejecute la inferencia\n" +
                    "5. Revise los resultados",
                    "Ayuda", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                System.exit(0);
            }
        });

        panelBotones.add(btnConsultaExperto);
        panelBotones.add(btnHistorial);
        panelBotones.add(btnAyuda);
        panelBotones.add(btnSalir);

        panelPrincipal.add(panelTitulo, BorderLayout.NORTH);
        panelPrincipal.add(panelBotones, BorderLayout.CENTER);

        add(panelPrincipal);
    }
}