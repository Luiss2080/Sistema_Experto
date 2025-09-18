package vista.administrador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.entidades.*;
import persistencia.dao.*;

public class EditorBaseConocimiento extends JDialog {
    private GestionBaseConocimiento parent;
    private Usuario usuario;
    private BaseConocimiento baseConocimiento;
    private BaseConocimientoDAO baseDAO;

    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private JButton btnGestionReglas;
    private JButton btnGestionPremisas;
    private JButton btnGestionObjetivos;

    public EditorBaseConocimiento(GestionBaseConocimiento parent, Usuario usuario,
                                 BaseConocimiento baseConocimiento) {
        super(parent, "Editor de Base de Conocimiento", true);
        this.parent = parent;
        this.usuario = usuario;
        this.baseConocimiento = baseConocimiento;
        this.baseDAO = new BaseConocimientoDAOImpl();
        initComponents();
        cargarDatos();
    }

    private void initComponents() {
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        JPanel panelDatos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panelDatos.add(new JLabel("Nombre:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtNombre = new JTextField(20);
        panelDatos.add(txtNombre, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelDatos.add(new JLabel("Descripción:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        txtDescripcion = new JTextArea(5, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        panelDatos.add(scrollDescripcion, gbc);

        JPanel panelGestion = new JPanel(new FlowLayout());
        btnGestionPremisas = new JButton("Gestionar Premisas");
        btnGestionObjetivos = new JButton("Gestionar Objetivos");
        btnGestionReglas = new JButton("Gestionar Reglas");

        btnGestionPremisas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (baseConocimiento != null) {
                    new GestionPremisas(EditorBaseConocimiento.this, baseConocimiento).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(EditorBaseConocimiento.this,
                        "Debe guardar la base primero", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnGestionObjetivos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (baseConocimiento != null) {
                    new GestionObjetivos(EditorBaseConocimiento.this, baseConocimiento).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(EditorBaseConocimiento.this,
                        "Debe guardar la base primero", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        btnGestionReglas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (baseConocimiento != null) {
                    new GestionReglas(EditorBaseConocimiento.this, baseConocimiento).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(EditorBaseConocimiento.this,
                        "Debe guardar la base primero", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        panelGestion.add(btnGestionPremisas);
        panelGestion.add(btnGestionObjetivos);
        panelGestion.add(btnGestionReglas);

        JPanel panelBotones = new JPanel(new FlowLayout());
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");

        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardar();
            }
        });

        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        panelPrincipal.add(panelDatos, BorderLayout.CENTER);
        panelPrincipal.add(panelGestion, BorderLayout.SOUTH);
        panelPrincipal.add(panelBotones, BorderLayout.PAGE_END);

        add(panelPrincipal);
    }

    private void cargarDatos() {
        if (baseConocimiento != null) {
            txtNombre.setText(baseConocimiento.getNombre());
            txtDescripcion.setText(baseConocimiento.getDescripcion());
        }
    }

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un nombre",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean esNueva = (baseConocimiento == null);

        if (esNueva) {
            baseConocimiento = new BaseConocimiento(nombre, descripcion, usuario.getId());
        } else {
            baseConocimiento.setNombre(nombre);
            baseConocimiento.setDescripcion(descripcion);
        }

        boolean exito;
        if (esNueva) {
            BaseConocimiento baseCreada = baseDAO.crear(baseConocimiento);
            exito = baseCreada != null;
            if (exito) {
                baseConocimiento = baseCreada;
            }
        } else {
            exito = baseDAO.actualizar(baseConocimiento);
        }

        if (exito) {
            parent.actualizarYSeleccionar(baseConocimiento);

            String mensaje = esNueva ?
                "Base de conocimiento creada exitosamente.\n¡Ya puede agregar premisas, objetivos y reglas!" :
                "Base de conocimiento actualizada exitosamente.";

            parent.mostrarMensajeExito(mensaje);

            btnGestionPremisas.setEnabled(true);
            btnGestionObjetivos.setEnabled(true);
            btnGestionReglas.setEnabled(true);

            // Actualizar título para reflejar que ya no es nueva
            if (esNueva) {
                esNueva = false;
                setTitle("Editar Base de Conocimiento: " + baseConocimiento.getNombre());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar la base",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}