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

        // Panel de gestión con guía paso a paso
        JPanel panelGestion = new JPanel(new BorderLayout());
        panelGestion.setBorder(BorderFactory.createTitledBorder("Configuración de la Base de Conocimiento"));

        // Panel de instrucciones
        JTextArea txtInstrucciones = new JTextArea();
        txtInstrucciones.setEditable(false);
        txtInstrucciones.setBackground(getBackground());
        txtInstrucciones.setFont(new Font("Arial", Font.PLAIN, 12));
        txtInstrucciones.setText("Para que la base funcione correctamente, debe completar los siguientes pasos:\n\n" +
                                "1. PREMISAS: Variables que el cliente ingresará como datos\n" +
                                "2. OBJETIVOS: Metas o conclusiones que el sistema debe alcanzar\n" +
                                "3. REGLAS: Lógica que conecta premisas con objetivos\n\n" +
                                "Solo las bases completas aparecerán disponibles para los clientes.");
        JScrollPane scrollInstrucciones = new JScrollPane(txtInstrucciones);
        scrollInstrucciones.setPreferredSize(new Dimension(450, 100));

        // Panel de botones con indicadores de estado
        JPanel panelBotonesGestion = new JPanel(new GridLayout(3, 1, 5, 5));

        btnGestionPremisas = new JButton("Gestionar Premisas");
        btnGestionObjetivos = new JButton("Gestionar Objetivos");
        btnGestionReglas = new JButton("Gestionar Reglas");

        // Configurar apariencia inicial
        configurarBotonGestion(btnGestionPremisas);
        configurarBotonGestion(btnGestionObjetivos);
        configurarBotonGestion(btnGestionReglas);

        btnGestionPremisas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (baseConocimiento != null) {
                    GestionPremisasCompleta ventana = new GestionPremisasCompleta(EditorBaseConocimiento.this, baseConocimiento);
                    ventana.setVisible(true);
                    // Actualizar estado después de cerrar la ventana
                    ventana.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                            actualizarEstadoBotones();
                        }
                    });
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
                    GestionObjetivosCompleta ventana = new GestionObjetivosCompleta(EditorBaseConocimiento.this, baseConocimiento);
                    ventana.setVisible(true);
                    ventana.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                            actualizarEstadoBotones();
                        }
                    });
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
                    GestionReglasCompleta ventana = new GestionReglasCompleta(EditorBaseConocimiento.this, baseConocimiento);
                    ventana.setVisible(true);
                    ventana.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                            actualizarEstadoBotones();
                        }
                    });
                } else {
                    JOptionPane.showMessageDialog(EditorBaseConocimiento.this,
                        "Debe guardar la base primero", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        panelBotonesGestion.add(btnGestionPremisas);
        panelBotonesGestion.add(btnGestionObjetivos);
        panelBotonesGestion.add(btnGestionReglas);

        panelGestion.add(scrollInstrucciones, BorderLayout.NORTH);
        panelGestion.add(panelBotonesGestion, BorderLayout.CENTER);

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
            actualizarEstadoBotones();
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

            // Actualizar estado de botones y título
            actualizarEstadoBotones();

            // Si es nueva, cambiar título
            if (esNueva) {
                esNueva = false;
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar la base",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Configura la apariencia inicial de un botón de gestión
     */
    private void configurarBotonGestion(JButton boton) {
        boton.setPreferredSize(new Dimension(300, 40));
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setFont(new Font("Arial", Font.BOLD, 12));
    }

    /**
     * Actualiza el estado visual de los botones según el contenido de la base
     */
    private void actualizarEstadoBotones() {
        if (baseConocimiento == null) return;

        // Actualizar botón de premisas
        if (baseConocimiento.getPremisas().isEmpty()) {
            btnGestionPremisas.setText("Gestionar Premisas (VACÍO - Requerido)");
            btnGestionPremisas.setBackground(new Color(255, 235, 235));
            btnGestionPremisas.setForeground(new Color(139, 0, 0));
        } else {
            btnGestionPremisas.setText("Gestionar Premisas (" + baseConocimiento.getPremisas().size() + " definidas)");
            btnGestionPremisas.setBackground(new Color(235, 255, 235));
            btnGestionPremisas.setForeground(new Color(0, 100, 0));
        }

        // Actualizar botón de objetivos
        if (baseConocimiento.getObjetivos().isEmpty()) {
            btnGestionObjetivos.setText("Gestionar Objetivos (VACÍO - Requerido)");
            btnGestionObjetivos.setBackground(new Color(255, 235, 235));
            btnGestionObjetivos.setForeground(new Color(139, 0, 0));
        } else {
            btnGestionObjetivos.setText("Gestionar Objetivos (" + baseConocimiento.getObjetivos().size() + " definidos)");
            btnGestionObjetivos.setBackground(new Color(235, 255, 235));
            btnGestionObjetivos.setForeground(new Color(0, 100, 0));
        }

        // Actualizar botón de reglas
        if (baseConocimiento.getReglas().isEmpty()) {
            btnGestionReglas.setText("Gestionar Reglas (VACÍO - Requerido)");
            btnGestionReglas.setBackground(new Color(255, 235, 235));
            btnGestionReglas.setForeground(new Color(139, 0, 0));
        } else {
            btnGestionReglas.setText("Gestionar Reglas (" + baseConocimiento.getReglas().size() + " definidas)");
            btnGestionReglas.setBackground(new Color(235, 255, 235));
            btnGestionReglas.setForeground(new Color(0, 100, 0));
        }

        // Actualizar título de la ventana con estado general
        boolean esCompleta = !baseConocimiento.getPremisas().isEmpty() &&
                           !baseConocimiento.getObjetivos().isEmpty() &&
                           !baseConocimiento.getReglas().isEmpty();

        if (esCompleta) {
            setTitle("Editor de Base de Conocimiento: " + baseConocimiento.getNombre() + " (COMPLETA)");
        } else {
            setTitle("Editor de Base de Conocimiento: " + baseConocimiento.getNombre() + " (INCOMPLETA)");
        }

        repaint();
    }
}