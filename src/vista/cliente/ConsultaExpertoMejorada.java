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

public class ConsultaExpertoMejorada extends JFrame {
    private MenuCliente parent;
    private Usuario usuario;
    private BaseConocimientoDAO baseDAO;
    private SesionDAO sesionDAO;

    private JComboBox<BaseConocimiento> cmbBases;
    private JComboBox<String> cmbAlgoritmo;
    private JPanel panelFormulario;
    private JButton btnEjecutar;
    private JButton btnLimpiar;
    private JButton btnVolver;
    private JTextArea txtResultados;
    private JTextArea txtExplicacion;
    private List<ComponentePremisa> camposPremisas;

    public ConsultaExpertoMejorada(MenuCliente parent, Usuario usuario) {
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
        setTitle("Consulta al Sistema Experto - " + usuario.getNombreUsuario());
        setSize(1000, 700);
        setLocationRelativeTo(parent);

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Panel superior - Selección
        JPanel panelSuperior = new JPanel(new GridBagLayout());
        panelSuperior.setBorder(BorderFactory.createTitledBorder("Seleccione el Sistema Experto que Desea Consultar"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblSistema = new JLabel("Sistema Experto:");
        lblSistema.setFont(lblSistema.getFont().deriveFont(Font.BOLD, 13f));
        panelSuperior.add(lblSistema, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        cmbBases = new JComboBox<>();
        cmbBases.addActionListener(e -> cargarFormulario());
        panelSuperior.add(cmbBases, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel lblMetodo = new JLabel("Método de Análisis:");
        lblMetodo.setFont(lblMetodo.getFont().deriveFont(Font.BOLD, 13f));
        panelSuperior.add(lblMetodo, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        cmbAlgoritmo = new JComboBox<>(new String[]{
            "Análisis Completo (Recomendado)",
            "Análisis Directo",
            "Análisis Inverso"
        });
        cmbAlgoritmo.setToolTipText("El Análisis Completo es más preciso y recomendado para mejores resultados");
        panelSuperior.add(cmbAlgoritmo, gbc);

        // Panel central dividido
        JPanel panelCentral = new JPanel(new BorderLayout());
        
        // Panel izquierdo - Formulario de datos
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.setBorder(BorderFactory.createTitledBorder("Responda las Siguientes Preguntas"));
        
        panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        JScrollPane scrollFormulario = new JScrollPane(panelFormulario);
        scrollFormulario.setPreferredSize(new Dimension(400, 300));
        
        panelIzquierdo.add(scrollFormulario, BorderLayout.CENTER);

        // Panel derecho - Resultados
        JPanel panelDerecho = new JPanel(new BorderLayout());
        
        // Resultados
        JPanel panelResultados = new JPanel(new BorderLayout());
        panelResultados.setBorder(BorderFactory.createTitledBorder("Conclusiones"));
        
        txtResultados = new JTextArea(8, 30);
        txtResultados.setEditable(false);
        txtResultados.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollResultados = new JScrollPane(txtResultados);
        panelResultados.add(scrollResultados, BorderLayout.CENTER);

        // Explicación
        JPanel panelExplicacion = new JPanel(new BorderLayout());
        panelExplicacion.setBorder(BorderFactory.createTitledBorder("Explicación del Razonamiento"));
        
        txtExplicacion = new JTextArea(8, 30);
        txtExplicacion.setEditable(false);
        txtExplicacion.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        JScrollPane scrollExplicacion = new JScrollPane(txtExplicacion);
        panelExplicacion.add(scrollExplicacion, BorderLayout.CENTER);

        // Dividir resultados y explicación verticalmente
        JSplitPane splitResultados = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panelResultados, panelExplicacion);
        splitResultados.setDividerLocation(200);
        
        panelDerecho.add(splitResultados, BorderLayout.CENTER);

        // Dividir formulario y resultados horizontalmente
        JSplitPane splitPrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, panelDerecho);
        splitPrincipal.setDividerLocation(420);
        
        panelCentral.add(splitPrincipal, BorderLayout.CENTER);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnEjecutar = new JButton("Ejecutar Inferencia");
        btnLimpiar = new JButton("Limpiar");
        btnVolver = new JButton("Volver");

        btnEjecutar.addActionListener(e -> ejecutarInferencia());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        btnVolver.addActionListener(e -> {
            dispose();
            parent.setVisible(true);
        });

        panelBotones.add(btnEjecutar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnVolver);

        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);
        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private void cargarBases() {
        try {
            List<BaseConocimiento> todasLasBases = baseDAO.listarTodas(); // Cargar todas las bases
            List<BaseConocimiento> basesCompletas = new ArrayList<>();

            // Filtrar solo las bases completas (que tienen premisas, objetivos y reglas)
            for (BaseConocimiento base : todasLasBases) {
                if (esBaseCompleta(base)) {
                    basesCompletas.add(base);
                    cmbBases.addItem(base);
                }
            }

            if (basesCompletas.isEmpty()) {
                btnEjecutar.setEnabled(false);
                if (todasLasBases.isEmpty()) {
                    txtResultados.setText("No hay bases de conocimiento disponibles.\n" +
                                         "Contacte al administrador para crear una base de conocimiento.");
                } else {
                    txtResultados.setText("No hay bases de conocimiento completas disponibles.\n\n" +
                                         "Las bases de conocimiento deben tener:\n" +
                                         "• Al menos una premisa\n" +
                                         "• Al menos un objetivo\n" +
                                         "• Al menos una regla\n\n" +
                                         "Contacte al administrador para completar las bases existentes.");
                }
            } else {
                txtResultados.setText("Seleccione una base de conocimiento y configure los datos para comenzar.\n\n" +
                                     "Bases completas disponibles: " + basesCompletas.size() + "\n" +
                                     "Total de bases en el sistema: " + todasLasBases.size());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar bases de conocimiento: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Verifica si una base de conocimiento está completa para ser usada por clientes
     */
    private boolean esBaseCompleta(BaseConocimiento base) {
        return !base.getPremisas().isEmpty() &&
               !base.getObjetivos().isEmpty() &&
               !base.getReglas().isEmpty();
    }

    private void cargarFormulario() {
        panelFormulario.removeAll();
        camposPremisas.clear();

        BaseConocimiento base = (BaseConocimiento) cmbBases.getSelectedItem();
        if (base == null || base.getPremisas().isEmpty()) {
            JLabel lblSinPremisas = new JLabel("Esta base no tiene premisas definidas.");
            lblSinPremisas.setForeground(Color.RED);
            panelFormulario.add(lblSinPremisas);
            btnEjecutar.setEnabled(false);
        } else {
            btnEjecutar.setEnabled(true);
            
            for (Premisa premisa : base.getPremisas()) {
                ComponentePremisa compPremisa = new ComponentePremisa(premisa);
                camposPremisas.add(compPremisa);
                panelFormulario.add(compPremisa.getPanel());
            }
        }

        panelFormulario.revalidate();
        panelFormulario.repaint();
    }

    private void ejecutarInferencia() {
        BaseConocimiento base = (BaseConocimiento) cmbBases.getSelectedItem();
        if (base == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una base de conocimiento",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar que haya reglas
        if (base.getReglas().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La base de conocimiento no tiene reglas definidas",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Recopilar hechos del formulario
        List<Hecho> hechos = recopilarHechos();
        if (hechos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar al menos un valor",
                "Sin Datos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ejecutar inferencia
        String algoritmo = (String) cmbAlgoritmo.getSelectedItem();
        
        txtResultados.setText("Ejecutando inferencia...\n");
        txtExplicacion.setText("");
        
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            private List<String> conclusiones;
            private String explicacion;
            
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    switch (algoritmo) {
                        case "Forward Chaining":
                            ForwardChaining fc = new ForwardChaining();
                            conclusiones = fc.ejecutar(hechos, base.getReglas(), base.getObjetivos());
                            explicacion = fc.obtenerExplicacion();
                            break;
                            
                        case "Backward Chaining":
                            BackwardChaining bc = new BackwardChaining();
                            conclusiones = bc.ejecutar(hechos, base.getReglas(), base.getObjetivos());
                            explicacion = bc.obtenerExplicacion();
                            break;
                            
                        case "Motor Avanzado":
                        default:
                            MotorInferenciaAvanzado motor = new MotorInferenciaAvanzado();
                            motor.establecerEstrategia(EstrategiaInferencia.AMPLITUD_PRIMERO);
                            conclusiones = motor.ejecutar(hechos, base.getReglas(), base.getObjetivos());
                            explicacion = motor.obtenerExplicacion();
                            break;
                    }
                } catch (Exception e) {
                    throw new Exception("Error durante la inferencia: " + e.getMessage());
                }
                return null;
            }
            
            @Override
            protected void done() {
                try {
                    get(); // Verificar si hubo errores
                    mostrarResultados(conclusiones, explicacion);
                    guardarSesion(base, hechos, conclusiones);
                } catch (Exception e) {
                    txtResultados.setText("Error: " + e.getMessage());
                    txtExplicacion.setText("");
                }
            }
        };
        
        worker.execute();
    }

    private List<Hecho> recopilarHechos() {
        List<Hecho> hechos = new ArrayList<>();
        
        for (ComponentePremisa comp : camposPremisas) {
            String valor = comp.getValor();
            if (valor != null && !valor.trim().isEmpty()) {
                Hecho hecho = new Hecho();
                hecho.setPremisaId(comp.getPremisa().getId());
                hecho.setPremisaNombre(comp.getPremisa().getNombre());
                hecho.setValor(valor.trim());
                hecho.setCerteza(1.0);
                hecho.setFuente("usuario");
                hechos.add(hecho);
            }
        }
        
        return hechos;
    }

    private void mostrarResultados(List<String> conclusiones, String explicacion) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RESULTADOS DE LA CONSULTA ===\n\n");
        
        if (conclusiones.isEmpty()) {
            sb.append("No se obtuvieron conclusiones con los datos proporcionados.\n");
            sb.append("Verifique:\n");
            sb.append("- Que los datos ingresados sean correctos\n");
            sb.append("- Que las reglas estén bien definidas\n");
            sb.append("- Que existan reglas aplicables a los datos\n");
        } else {
            sb.append("CONCLUSIONES OBTENIDAS:\n");
            for (int i = 0; i < conclusiones.size(); i++) {
                sb.append((i + 1)).append(". ").append(conclusiones.get(i)).append("\n");
            }
        }
        
        txtResultados.setText(sb.toString());
        txtExplicacion.setText(explicacion);
    }

    private void guardarSesion(BaseConocimiento base, List<Hecho> hechos, List<String> conclusiones) {
        try {
            Sesion sesion = new Sesion();
            sesion.setUsuarioId(usuario.getId());
            sesion.setBaseConocimientoId(base.getId());
            sesion.setHechos(hechos);
            
            ArrayList<String> conclusionesList = new ArrayList<>(conclusiones);
            sesion.setConclusiones(conclusionesList);
            sesion.finalizarSesion();
            
            sesionDAO.crear(sesion);
        } catch (Exception e) {
            // Error al guardar, pero no interrumpir el flujo
            System.err.println("Error al guardar sesión: " + e.getMessage());
        }
    }

    private void limpiarFormulario() {
        for (ComponentePremisa comp : camposPremisas) {
            comp.limpiar();
        }
        txtResultados.setText("");
        txtExplicacion.setText("");
    }

    // Clase interna para manejar componentes de premisas
    private class ComponentePremisa {
        private Premisa premisa;
        private JPanel panel;
        private JComponent campo;
        
        public ComponentePremisa(Premisa premisa) {
            this.premisa = premisa;
            crearComponente();
        }
        
        private void crearComponente() {
            panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));
            panel.setBackground(new Color(252, 252, 252));

            // Panel de pregunta
            JPanel panelPregunta = new JPanel(new BorderLayout());
            panelPregunta.setBackground(new Color(252, 252, 252));

            JLabel lblPregunta = new JLabel();
            if (premisa.getDescripcion() != null && !premisa.getDescripcion().trim().isEmpty()) {
                lblPregunta.setText(premisa.getDescripcion());
            } else {
                lblPregunta.setText(premisa.getNombre() + ":");
            }
            lblPregunta.setFont(lblPregunta.getFont().deriveFont(Font.BOLD, 13f));
            lblPregunta.setForeground(new Color(60, 60, 60));

            panelPregunta.add(lblPregunta, BorderLayout.WEST);

            // Indicador de tipo
            String tipoTexto = "";
            Color tipoColor = Color.GRAY;
            switch (premisa.getTipo()) {
                case BOOLEANA:
                    tipoTexto = "[Sí/No]";
                    tipoColor = new Color(0, 120, 0);
                    break;
                case NUMERICA:
                    tipoTexto = "[Número]";
                    tipoColor = new Color(0, 0, 180);
                    break;
                case STRING:
                    tipoTexto = "[Texto]";
                    tipoColor = new Color(150, 0, 150);
                    break;
            }
            JLabel lblTipo = new JLabel(tipoTexto);
            lblTipo.setFont(lblTipo.getFont().deriveFont(Font.ITALIC, 11f));
            lblTipo.setForeground(tipoColor);
            panelPregunta.add(lblTipo, BorderLayout.EAST);

            // Campo de respuesta
            campo = crearCampoSegunTipo();

            panel.add(panelPregunta, BorderLayout.NORTH);
            panel.add(campo, BorderLayout.CENTER);
        }
        
        private JComponent crearCampoSegunTipo() {
            switch (premisa.getTipo()) {
                case BOOLEANA:
                    String[] opciones = {"-- Seleccionar --", "Sí", "No", "true", "false"};
                    JComboBox<String> cmbBool = new JComboBox<>(opciones);
                    cmbBool.setPreferredSize(new Dimension(150, 25));
                    if (premisa.getValorDefecto() != null) {
                        String valorDef = premisa.getValorDefecto().toLowerCase();
                        if (valorDef.equals("true") || valorDef.equals("sí")) {
                            cmbBool.setSelectedItem("Sí");
                        } else if (valorDef.equals("false") || valorDef.equals("no")) {
                            cmbBool.setSelectedItem("No");
                        }
                    }
                    cmbBool.setToolTipText("Seleccione Sí o No");
                    return cmbBool;

                case NUMERICA:
                    JTextField txtNum = new JTextField(10);
                    txtNum.setPreferredSize(new Dimension(120, 25));
                    if (premisa.getValorDefecto() != null) {
                        txtNum.setText(premisa.getValorDefecto());
                    }
                    txtNum.setToolTipText("Ingrese un número (ej: 25, 38.5, 100)");

                    // Agregar validación numérica
                    txtNum.addKeyListener(new java.awt.event.KeyAdapter() {
                        public void keyTyped(java.awt.event.KeyEvent evt) {
                            char c = evt.getKeyChar();
                            if (!Character.isDigit(c) && c != '.' && c != '-' && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                                evt.consume();
                            }
                        }
                    });
                    return txtNum;

                case STRING:
                default:
                    JTextField txtStr = new JTextField(15);
                    txtStr.setPreferredSize(new Dimension(200, 25));
                    if (premisa.getValorDefecto() != null) {
                        txtStr.setText(premisa.getValorDefecto());
                    }
                    txtStr.setToolTipText("Ingrese texto libre");
                    return txtStr;
            }
        }
        
        public String getValor() {
            if (campo instanceof JComboBox) {
                Object selected = ((JComboBox<?>) campo).getSelectedItem();
                if (selected == null || selected.toString().startsWith("--")) {
                    return null;
                }
                String valor = selected.toString();
                // Convertir opciones amigables a valores técnicos
                if (valor.equals("Sí")) return "true";
                if (valor.equals("No")) return "false";
                return valor;
            } else if (campo instanceof JTextField) {
                String texto = ((JTextField) campo).getText().trim();
                return texto.isEmpty() ? null : texto;
            }
            return null;
        }
        
        public void limpiar() {
            if (campo instanceof JComboBox) {
                ((JComboBox<?>) campo).setSelectedIndex(0);
            } else if (campo instanceof JTextField) {
                ((JTextField) campo).setText("");
            }
        }
        
        public Premisa getPremisa() {
            return premisa;
        }
        
        public JPanel getPanel() {
            return panel;
        }
    }
}