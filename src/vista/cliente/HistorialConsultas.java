package vista.cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import modelo.entidades.*;
import persistencia.dao.*;

public class HistorialConsultas extends JDialog {
    private MenuCliente parent;
    private Usuario usuario;
    private SesionDAO sesionDAO;
    private BaseConocimientoDAO baseDAO;

    private JList<Sesion> listaSesiones;
    private DefaultListModel<Sesion> modeloLista;
    private JTextArea txtDetalles;
    private JButton btnVer;
    private JButton btnEliminar;
    private JButton btnCerrar;

    public HistorialConsultas(MenuCliente parent, Usuario usuario) {
        super(parent, "Historial de Consultas", true);
        this.parent = parent;
        this.usuario = usuario;
        this.sesionDAO = new SesionDAOImpl();
        this.baseDAO = new BaseConocimientoDAOImpl();
        initComponents();
        cargarHistorial();
    }

    private void initComponents() {
        setSize(700, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.setBorder(BorderFactory.createTitledBorder("Sesiones Anteriores"));

        modeloLista = new DefaultListModel<>();
        listaSesiones = new JList<>(modeloLista);
        listaSesiones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaSesiones.setCellRenderer(new DefaultListCellRenderer() {
            private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Sesion) {
                    Sesion sesion = (Sesion) value;
                    BaseConocimiento base = baseDAO.buscarPorId(sesion.getBaseConocimientoId());
                    String nombreBase = (base != null) ? base.getNombre() : "Base eliminada";
                    setText(nombreBase + " - " + sesion.getFechaInicio().format(formatter));
                }
                return this;
            }
        });

        listaSesiones.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalles();
            }
        });

        JScrollPane scrollLista = new JScrollPane(listaSesiones);
        scrollLista.setPreferredSize(new Dimension(250, 300));
        panelIzquierdo.add(scrollLista, BorderLayout.CENTER);

        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.setBorder(BorderFactory.createTitledBorder("Detalles de la Sesión"));

        txtDetalles = new JTextArea();
        txtDetalles.setEditable(false);
        txtDetalles.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollDetalles = new JScrollPane(txtDetalles);
        panelDerecho.add(scrollDetalles, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout());
        btnVer = new JButton("Ver Detalles");
        btnEliminar = new JButton("Eliminar");
        btnCerrar = new JButton("Cerrar");

        btnVer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarDetalles();
            }
        });

        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarSesion();
            }
        });

        btnCerrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        panelBotones.add(btnVer);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnCerrar);

        panelPrincipal.add(panelIzquierdo, BorderLayout.WEST);
        panelPrincipal.add(panelDerecho, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private void cargarHistorial() {
        modeloLista.clear();
        for (Sesion sesion : sesionDAO.obtenerHistorial(usuario.getId())) {
            modeloLista.addElement(sesion);
        }
    }

    private void mostrarDetalles() {
        Sesion seleccionada = listaSesiones.getSelectedValue();
        if (seleccionada == null) {
            txtDetalles.setText("Seleccione una sesión para ver los detalles.");
            return;
        }

        StringBuilder detalles = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        BaseConocimiento base = baseDAO.buscarPorId(seleccionada.getBaseConocimientoId());
        String nombreBase = (base != null) ? base.getNombre() : "Base eliminada";

        detalles.append("=== DETALLES DE LA SESIÓN ===\n\n");
        detalles.append("Base de Conocimiento: ").append(nombreBase).append("\n");
        detalles.append("Fecha de Inicio: ").append(seleccionada.getFechaInicio().format(formatter)).append("\n");

        if (seleccionada.getFechaFin() != null) {
            detalles.append("Fecha de Fin: ").append(seleccionada.getFechaFin().format(formatter)).append("\n");
        }

        detalles.append("\n--- HECHOS INTRODUCIDOS ---\n");
        if (seleccionada.getHechos().isEmpty()) {
            detalles.append("No se registraron hechos.\n");
        } else {
            for (int i = 0; i < seleccionada.getHechos().size(); i++) {
                Hecho hecho = seleccionada.getHechos().get(i);
                detalles.append((i + 1)).append(". ").append(hecho.toString()).append("\n");
            }
        }

        detalles.append("\n--- CONCLUSIONES OBTENIDAS ---\n");
        if (seleccionada.getConclusiones().isEmpty()) {
            detalles.append("No se obtuvieron conclusiones.\n");
        } else {
            for (int i = 0; i < seleccionada.getConclusiones().size(); i++) {
                String conclusion = seleccionada.getConclusiones().get(i);
                detalles.append((i + 1)).append(". ").append(conclusion).append("\n");
            }
        }

        txtDetalles.setText(detalles.toString());
        txtDetalles.setCaretPosition(0);
    }

    private void eliminarSesion() {
        Sesion seleccionada = listaSesiones.getSelectedValue();
        if (seleccionada == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una sesión",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar esta sesión del historial?",
            "Confirmar", JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            if (sesionDAO.eliminar(seleccionada.getId())) {
                cargarHistorial();
                txtDetalles.setText("");
                JOptionPane.showMessageDialog(this, "Sesión eliminada exitosamente",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar la sesión",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}