package vista.administrador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.entidades.*;
import persistencia.dao.*;

public class GestionBaseConocimiento extends JFrame {
    private MenuAdministrador parent;
    private Usuario usuario;
    private BaseConocimientoDAO baseDAO;
    private JList<BaseConocimiento> listaBases;
    private DefaultListModel<BaseConocimiento> modeloLista;
    private JButton btnNueva;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnActualizar;
    private JButton btnVolver;

    public GestionBaseConocimiento(MenuAdministrador parent, Usuario usuario) {
        this.parent = parent;
        this.usuario = usuario;
        this.baseDAO = new BaseConocimientoDAOImpl();
        initComponents();
        cargarBases();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Gestión de Bases de Conocimiento");
        setSize(600, 400);
        setLocationRelativeTo(parent);

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        JPanel panelTitulo = new JPanel();
        JLabel lblTitulo = new JLabel("BASES DE CONOCIMIENTO");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        panelTitulo.add(lblTitulo);

        modeloLista = new DefaultListModel<>();
        listaBases = new JList<>(modeloLista);
        listaBases.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaBases.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof BaseConocimiento) {
                    BaseConocimiento base = (BaseConocimiento) value;
                    setText(base.getNombre() + " - " + base.getDescripcion());
                }
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(listaBases);

        JPanel panelBotones = new JPanel(new FlowLayout());
        btnNueva = new JButton("Nueva Base");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        btnActualizar = new JButton("Actualizar");
        btnVolver = new JButton("Volver");

        btnNueva.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new EditorBaseConocimiento(GestionBaseConocimiento.this, usuario, null).setVisible(true);
            }
        });

        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BaseConocimiento seleccionada = listaBases.getSelectedValue();
                if (seleccionada != null) {
                    new EditorBaseConocimiento(GestionBaseConocimiento.this, usuario, seleccionada).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(GestionBaseConocimiento.this,
                        "Debe seleccionar una base de conocimiento", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarBase();
            }
        });

        btnActualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarLista();
                JOptionPane.showMessageDialog(GestionBaseConocimiento.this,
                    "Lista actualizada correctamente", "Actualización", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnVolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        panelBotones.add(btnNueva);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnVolver);

        panelPrincipal.add(panelTitulo, BorderLayout.NORTH);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private void cargarBases() {
        BaseConocimiento seleccionada = listaBases.getSelectedValue();
        int seleccionPrevia = -1;

        if (seleccionada != null) {
            seleccionPrevia = seleccionada.getId();
        }

        modeloLista.clear();
        int nuevaSeleccion = -1;

        for (BaseConocimiento base : baseDAO.listarTodas()) {
            modeloLista.addElement(base);
            if (seleccionPrevia != -1 && base.getId() == seleccionPrevia) {
                nuevaSeleccion = modeloLista.getSize() - 1;
            }
        }

        // Restaurar selección si aún existe
        if (nuevaSeleccion != -1) {
            listaBases.setSelectedIndex(nuevaSeleccion);
        }

        // Actualizar información de estado
        SwingUtilities.invokeLater(() -> {
            setTitle("Gestión de Bases de Conocimiento (" + modeloLista.getSize() + " bases)");
        });
    }

    private void eliminarBase() {
        BaseConocimiento seleccionada = listaBases.getSelectedValue();
        if (seleccionada == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una base de conocimiento",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar la base: " + seleccionada.getNombre() + "?",
            "Confirmar", JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            if (baseDAO.eliminar(seleccionada.getId())) {
                cargarBases();
                JOptionPane.showMessageDialog(this, "Base eliminada exitosamente",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar la base",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void actualizarLista() {
        cargarBases();
    }

    public void actualizarYSeleccionar(BaseConocimiento baseASeleccionar) {
        cargarBases();
        if (baseASeleccionar != null) {
            for (int i = 0; i < modeloLista.size(); i++) {
                BaseConocimiento base = modeloLista.getElementAt(i);
                if (base.getId() == baseASeleccionar.getId()) {
                    listaBases.setSelectedIndex(i);
                    listaBases.ensureIndexIsVisible(i);
                    break;
                }
            }
        }
    }

    public void mostrarMensajeExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
    }
}