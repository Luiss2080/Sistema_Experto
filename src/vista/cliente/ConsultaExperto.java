package vista.cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import modelo.entidades.*;
import modelo.motor_inferencia.*;
import persistencia.dao.*;
import vista.formularios.*;

public class ConsultaExperto extends JFrame {
    private MenuCliente parent;
    private Usuario usuario;
    private BaseConocimientoDAO baseDAO;
    private SesionDAO sesionDAO;

    private JComboBox<BaseConocimiento> cmbBases;
    private JComboBox<String> cmbAlgoritmo;
    private JPanel panelFormulario;
    private JButton btnEjecutar;
    private JButton btnActualizar;
    private JButton btnVolver;
    private JTextArea txtResultados;
    private List<JComponent> camposPremisas;

    public ConsultaExperto(MenuCliente parent, Usuario usuario) {
        this.parent = parent;
        this.usuario = usuario;
        this.baseDAO = new BaseConocimientoDAOImpl();
        this.sesionDAO = new SesionDAOImpl();
        this.camposPremisas = new ArrayList<>();
        initComponents();
        cargarBases();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Consulta al Sistema Experto");
        setSize(800, 600);
        setLocationRelativeTo(parent);

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        JPanel panelSuperior = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panelSuperior.add(new JLabel("Base de Conocimiento:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        cmbBases = new JComboBox<>();
        cmbBases.addActionListener(e -> cargarFormulario());
        panelSuperior.add(cmbBases, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelSuperior.add(new JLabel("Algoritmo:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        cmbAlgoritmo = new JComboBox<>(new String[]{"Forward Chaining", "Backward Chaining"});
        panelSuperior.add(cmbAlgoritmo, gbc);

        JPanel panelCentral = new JPanel(new BorderLayout());

        panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos de Entrada"));
        JScrollPane scrollFormulario = new JScrollPane(panelFormulario);
        scrollFormulario.setPreferredSize(new Dimension(400, 200));

        txtResultados = new JTextArea(10, 30);
        txtResultados.setEditable(false);
        txtResultados.setBorder(BorderFactory.createTitledBorder("Resultados"));
        JScrollPane scrollResultados = new JScrollPane(txtResultados);

        panelCentral.add(scrollFormulario, BorderLayout.NORTH);
        panelCentral.add(scrollResultados, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout());
        btnEjecutar = new JButton("Ejecutar Inferencia");
        btnActualizar = new JButton("Actualizar Bases");
        btnVolver = new JButton("Volver");

        btnEjecutar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ejecutarInferenciaAvanzada();
            }
        });

        btnActualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarBases();
                JOptionPane.showMessageDialog(ConsultaExperto.this,
                    "Lista de bases de conocimiento actualizada",
                    "Actualización", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnVolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        panelBotones.add(btnEjecutar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnVolver);

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private void cargarBases() {
        cmbBases.removeAllItems();
        for (BaseConocimiento base : baseDAO.listarTodas()) {
            cmbBases.addItem(base);
        }
        cmbBases.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof BaseConocimiento) {
                    setText(((BaseConocimiento) value).getNombre());
                }
                return this;
            }
        });
    }

    private void cargarFormulario() {
        panelFormulario.removeAll();
        camposPremisas.clear();

        BaseConocimiento base = (BaseConocimiento) cmbBases.getSelectedItem();
        if (base == null) return;

        for (Premisa premisa : base.getPremisas()) {
            JPanel panelPremisa = new JPanel(new FlowLayout(FlowLayout.LEFT));

            JLabel lblPremisa = new JLabel(premisa.getNombre() + ":");
            lblPremisa.setPreferredSize(new Dimension(150, 25));
            panelPremisa.add(lblPremisa);

            JComponent campo = crearCampoPremisa(premisa);
            panelPremisa.add(campo);
            camposPremisas.add(campo);

            if (!premisa.getDescripcion().isEmpty()) {
                JLabel lblDescripcion = new JLabel("(" + premisa.getDescripcion() + ")");
                lblDescripcion.setFont(lblDescripcion.getFont().deriveFont(Font.ITALIC, 10f));
                panelPremisa.add(lblDescripcion);
            }

            panelFormulario.add(panelPremisa);
        }

        panelFormulario.revalidate();
        panelFormulario.repaint();
    }

    private JComponent crearCampoPremisa(Premisa premisa) {
        switch (premisa.getTipo()) {
            case BOOLEANA:
                JComboBox<String> cmbBoolean = new JComboBox<>(new String[]{"true", "false"});
                if (premisa.getValorDefecto() != null) {
                    cmbBoolean.setSelectedItem(premisa.getValorDefecto());
                }
                return cmbBoolean;
            case NUMERICA:
                JTextField txtNumerico = new JTextField(10);
                if (premisa.getValorDefecto() != null) {
                    txtNumerico.setText(premisa.getValorDefecto());
                }
                return txtNumerico;
            case STRING:
            default:
                JTextField txtTexto = new JTextField(20);
                if (premisa.getValorDefecto() != null) {
                    txtTexto.setText(premisa.getValorDefecto());
                }
                return txtTexto;
        }
    }

    private void ejecutarInferenciaAvanzada() {
        BaseConocimiento base = (BaseConocimiento) cmbBases.getSelectedItem();
        if (base == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una base de conocimiento",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (base.getPremisas().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La base de conocimiento no tiene premisas definidas",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Paso 1: Seleccionar algoritmo y estrategia
        SelectorAlgoritmos selectorAlgoritmos = new SelectorAlgoritmos(this);
        selectorAlgoritmos.setVisible(true);

        if (!selectorAlgoritmos.isSeleccionConfirmada()) {
            return; // Usuario canceló la selección
        }

        String algoritmoSeleccionado = selectorAlgoritmos.getAlgoritmoSeleccionado();
        EstrategiaInferencia estrategiaSeleccionada = selectorAlgoritmos.getEstrategiaSeleccionada();

        // Paso 2: Entrada de datos dinámica
        FormularioEntradaDatos formularioEntrada = new FormularioEntradaDatos(this, base);
        formularioEntrada.setVisible(true);

        List<Hecho> hechos = formularioEntrada.getHechos();
        if (hechos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se ingresaron datos válidos",
                "Sin Datos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Paso 3: Ejecutar inferencia con el motor seleccionado
        final MotorInferencia motor;
        final MotorInferenciaAvanzado motorAvanzado;

        switch (algoritmoSeleccionado) {
            case "Forward Chaining":
                motor = new ForwardChaining();
                motorAvanzado = null;
                break;
            case "Backward Chaining":
                motor = new BackwardChaining();
                motorAvanzado = null;
                break;
            case "Motor Avanzado":
            default:
                motorAvanzado = new MotorInferenciaAvanzado();
                motor = motorAvanzado;
                break;
        }

        motor.establecerEstrategia(estrategiaSeleccionada);

        // Mostrar progress dialog durante la ejecución
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setString("Ejecutando inferencia...");
        progressBar.setStringPainted(true);

        JDialog progressDialog = new JDialog(this, "Procesando", true);
        progressDialog.add(progressBar);
        progressDialog.setSize(300, 100);
        progressDialog.setLocationRelativeTo(this);

        // Ejecutar en hilo separado para no bloquear la UI
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private List<String> conclusiones;
            private String explicacion;
            private List<MotorInferenciaAvanzado.PasoInferencia> pasos;

            @Override
            protected Void doInBackground() throws Exception {
                // Simular un pequeño delay para mostrar el progress
                Thread.sleep(500);

                conclusiones = motor.ejecutar(hechos, base.getReglas(), base.getObjetivos());
                explicacion = motor.obtenerExplicacion();

                if (motorAvanzado != null) {
                    pasos = motorAvanzado.obtenerPasosInferencia();
                }

                return null;
            }

            @Override
            protected void done() {
                progressDialog.dispose();

                try {
                    get(); // Verificar si hubo excepciones

                    // Paso 4: Mostrar resultados con explicaciones detalladas
                    FormularioResultados formularioResultados = new FormularioResultados(
                        ConsultaExperto.this,
                        conclusiones,
                        explicacion,
                        pasos,
                        algoritmoSeleccionado,
                        estrategiaSeleccionada.toString()
                    );
                    formularioResultados.setVisible(true);

                    // Guardar sesión
                    guardarSesionAvanzada(base, hechos, conclusiones, algoritmoSeleccionado, estrategiaSeleccionada);

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ConsultaExperto.this,
                        "Error durante la ejecución: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
        progressDialog.setVisible(true);
    }

    private void ejecutarInferencia() {
        BaseConocimiento base = (BaseConocimiento) cmbBases.getSelectedItem();
        if (base == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una base de conocimiento",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (base.getPremisas().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La base de conocimiento no tiene premisas definidas",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Hecho> hechos = recopilarHechos(base);
        if (hechos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe completar al menos una premisa",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        MotorInferencia motor;
        String algoritmo = (String) cmbAlgoritmo.getSelectedItem();

        if ("Forward Chaining".equals(algoritmo)) {
            motor = new ForwardChaining();
        } else {
            motor = new BackwardChaining();
        }

        List<String> conclusiones = motor.ejecutar(hechos, base.getReglas(), base.getObjetivos());
        String explicacion = motor.obtenerExplicacion();

        mostrarResultados(conclusiones, explicacion);
        guardarSesion(base, hechos, conclusiones);
    }

    private List<Hecho> recopilarHechos(BaseConocimiento base) {
        List<Hecho> hechos = new ArrayList<>();
        List<Premisa> premisas = base.getPremisas();

        for (int i = 0; i < camposPremisas.size() && i < premisas.size(); i++) {
            JComponent campo = camposPremisas.get(i);
            Premisa premisa = premisas.get(i);
            String valor = obtenerValorCampo(campo);

            if (valor != null && !valor.trim().isEmpty()) {
                Hecho hecho = new Hecho(premisa.getId(), premisa.getNombre(), valor);
                hechos.add(hecho);
            }
        }

        return hechos;
    }

    private String obtenerValorCampo(JComponent campo) {
        if (campo instanceof JTextField) {
            return ((JTextField) campo).getText().trim();
        } else if (campo instanceof JComboBox) {
            return (String) ((JComboBox<?>) campo).getSelectedItem();
        }
        return null;
    }

    private void mostrarResultados(List<String> conclusiones, String explicacion) {
        StringBuilder resultado = new StringBuilder();

        resultado.append("=== RESULTADOS DE LA INFERENCIA ===\n\n");

        if (conclusiones.isEmpty()) {
            resultado.append("No se encontraron conclusiones.\n\n");
        } else {
            resultado.append("CONCLUSIONES:\n");
            for (int i = 0; i < conclusiones.size(); i++) {
                resultado.append((i + 1)).append(". ").append(conclusiones.get(i)).append("\n");
            }
            resultado.append("\n");
        }

        resultado.append("EXPLICACIÓN DEL PROCESO:\n");
        resultado.append(explicacion);

        txtResultados.setText(resultado.toString());
        txtResultados.setCaretPosition(0);
    }

    private void guardarSesionAvanzada(BaseConocimiento base, List<Hecho> hechos, List<String> conclusiones,
                                     String algoritmo, EstrategiaInferencia estrategia) {
        Sesion sesion = new Sesion(usuario.getId(), base.getId());
        sesion.setHechos(hechos);

        // Agregar información del algoritmo y estrategia a las conclusiones
        List<String> conclusionesExtendidas = new ArrayList<>(conclusiones);
        conclusionesExtendidas.add("--- CONFIGURACIÓN UTILIZADA ---");
        conclusionesExtendidas.add("Algoritmo: " + algoritmo);
        conclusionesExtendidas.add("Estrategia: " + estrategia.toString());
        conclusionesExtendidas.add("Hechos procesados: " + hechos.size());

        sesion.setConclusiones(conclusionesExtendidas);
        sesion.finalizarSesion();

        sesionDAO.crear(sesion);
    }

    private void guardarSesion(BaseConocimiento base, List<Hecho> hechos, List<String> conclusiones) {
        Sesion sesion = new Sesion(usuario.getId(), base.getId());
        sesion.setHechos(hechos);
        sesion.setConclusiones(conclusiones);
        sesion.finalizarSesion();

        sesionDAO.crear(sesion);
    }
}