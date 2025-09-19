package vista.formularios;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import modelo.entidades.*;
import modelo.motor_inferencia.MotorInferenciaAvanzado;

/**
 * Formulario para mostrar resultados detallados con explicaciones
 */
public class FormularioResultados extends JDialog {

    // Variables declaration
    private List<String> conclusiones;
    private String explicacionCompleta;
    private List<MotorInferenciaAvanzado.PasoInferencia> pasos;
    private String algoritmoUsado;
    private String estrategiaUsada;

    private JTabbedPane panelPestanas;
    private JPanel panelResumen;
    private JPanel panelExplicacion;
    private JPanel panelPasos;
    private JPanel panelGrafico;
    private JPanel panelBotones;

    // Componentes de resumen
    private JTextArea txtConclusiones;
    private JLabel lblAlgoritmo;
    private JLabel lblEstrategia;
    private JLabel lblTiempoProceso;
    private JProgressBar barraConfianza;

    // Componentes de explicación
    private JTextArea txtExplicacionCompleta;

    // Componentes de pasos
    private JTree arbolPasos;
    private JTextArea txtDetallePaso;

    // Componentes gráficos
    private JPanel panelVisualizacion;

    // Botones
    private JButton btnExportar;
    private JButton btnImprimir;
    private JButton btnNuevoAnalisis;
    private JButton btnCerrar;

    public FormularioResultados(JFrame parent, List<String> conclusiones, String explicacion,
                               List<MotorInferenciaAvanzado.PasoInferencia> pasos,
                               String algoritmo, String estrategia) {
        super(parent, "Resultados del Análisis", true);
        this.conclusiones = conclusiones;
        this.explicacionCompleta = explicacion;
        this.pasos = pasos;
        this.algoritmoUsado = algoritmo;
        this.estrategiaUsada = estrategia;
        initComponents();
        cargarDatos();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(getParent());

        // Panel de pestañas principal
        panelPestanas = new JTabbedPane();

        // Pestaña 1: Resumen
        crearPanelResumen();
        panelPestanas.addTab("Resumen", panelResumen);

        // Pestaña 2: Explicación Detallada
        crearPanelExplicacion();
        panelPestanas.addTab("Explicación", panelExplicacion);

        // Pestaña 3: Pasos de Inferencia
        crearPanelPasos();
        panelPestanas.addTab("Pasos", panelPasos);

        // Pestaña 4: Visualización Gráfica
        crearPanelGrafico();
        panelPestanas.addTab("Gráfico", panelGrafico);

        // Panel de botones
        crearPanelBotones();

        // Layout principal
        setLayout(new BorderLayout());
        add(panelPestanas, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void crearPanelResumen() {
        panelResumen = new JPanel(new BorderLayout());

        // Panel superior con información general
        JPanel panelInfo = new JPanel(new GridBagLayout());
        panelInfo.setBorder(BorderFactory.createTitledBorder("Información del Análisis"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Algoritmo usado
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panelInfo.add(new JLabel("Algoritmo:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        lblAlgoritmo = new JLabel(algoritmoUsado);
        lblAlgoritmo.setFont(new Font("Arial", Font.BOLD, 12));
        lblAlgoritmo.setForeground(Color.BLUE);
        panelInfo.add(lblAlgoritmo, gbc);

        // Estrategia usada
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelInfo.add(new JLabel("Estrategia:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        lblEstrategia = new JLabel(estrategiaUsada);
        lblEstrategia.setFont(new Font("Arial", Font.BOLD, 12));
        lblEstrategia.setForeground(Color.GREEN);
        panelInfo.add(lblEstrategia, gbc);

        // Tiempo de proceso (simulado)
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelInfo.add(new JLabel("Tiempo:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        lblTiempoProceso = new JLabel("< 1 segundo");
        lblTiempoProceso.setFont(new Font("Arial", Font.ITALIC, 12));
        panelInfo.add(lblTiempoProceso, gbc);

        // Barra de confianza
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelInfo.add(new JLabel("Confianza:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        barraConfianza = new JProgressBar(0, 100);
        barraConfianza.setStringPainted(true);
        barraConfianza.setValue(calcularConfianzaPromedio());
        barraConfianza.setString(barraConfianza.getValue() + "%");
        panelInfo.add(barraConfianza, gbc);

        // Panel de conclusiones
        JPanel panelConclusiones = new JPanel(new BorderLayout());
        panelConclusiones.setBorder(BorderFactory.createTitledBorder("Conclusiones Obtenidas"));

        txtConclusiones = new JTextArea();
        txtConclusiones.setEditable(false);
        txtConclusiones.setFont(new Font("Arial", Font.PLAIN, 12));
        txtConclusiones.setBackground(new Color(248, 248, 255));

        JScrollPane scrollConclusiones = new JScrollPane(txtConclusiones);
        scrollConclusiones.setPreferredSize(new Dimension(400, 200));

        panelConclusiones.add(scrollConclusiones, BorderLayout.CENTER);

        panelResumen.add(panelInfo, BorderLayout.NORTH);
        panelResumen.add(panelConclusiones, BorderLayout.CENTER);
    }

    private void crearPanelExplicacion() {
        panelExplicacion = new JPanel(new BorderLayout());

        // Título
        JLabel lblTitulo = new JLabel("Explicación Detallada del Proceso de Inferencia");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Área de texto para la explicación
        txtExplicacionCompleta = new JTextArea();
        txtExplicacionCompleta.setEditable(false);
        txtExplicacionCompleta.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtExplicacionCompleta.setLineWrap(false);

        JScrollPane scrollExplicacion = new JScrollPane(txtExplicacionCompleta);
        scrollExplicacion.setPreferredSize(new Dimension(750, 400));

        // Panel de controles
        JPanel panelControles = new JPanel(new FlowLayout());
        JButton btnBuscar = new JButton("Buscar en Explicación");
        JButton btnResaltar = new JButton("Resaltar Pasos Clave");
        JButton btnCopiar = new JButton("Copiar Texto");

        btnBuscar.addActionListener(e -> buscarEnExplicacion());
        btnResaltar.addActionListener(e -> resaltarPasosClave());
        btnCopiar.addActionListener(e -> copiarExplicacion());

        panelControles.add(btnBuscar);
        panelControles.add(btnResaltar);
        panelControles.add(btnCopiar);

        panelExplicacion.add(lblTitulo, BorderLayout.NORTH);
        panelExplicacion.add(scrollExplicacion, BorderLayout.CENTER);
        panelExplicacion.add(panelControles, BorderLayout.SOUTH);
    }

    private void crearPanelPasos() {
        panelPasos = new JPanel(new BorderLayout());

        // Panel dividido
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // Panel izquierdo: Árbol de pasos
        JPanel panelArbol = new JPanel(new BorderLayout());
        panelArbol.setBorder(BorderFactory.createTitledBorder("Secuencia de Pasos"));

        arbolPasos = new JTree();
        arbolPasos.addTreeSelectionListener(e -> mostrarDetallePaso());

        JScrollPane scrollArbol = new JScrollPane(arbolPasos);
        scrollArbol.setPreferredSize(new Dimension(300, 400));

        panelArbol.add(scrollArbol, BorderLayout.CENTER);

        // Panel derecho: Detalle del paso
        JPanel panelDetalle = new JPanel(new BorderLayout());
        panelDetalle.setBorder(BorderFactory.createTitledBorder("Detalle del Paso"));

        txtDetallePaso = new JTextArea();
        txtDetallePaso.setEditable(false);
        txtDetallePaso.setFont(new Font("Monospaced", Font.PLAIN, 11));

        JScrollPane scrollDetalle = new JScrollPane(txtDetallePaso);
        scrollDetalle.setPreferredSize(new Dimension(400, 400));

        panelDetalle.add(scrollDetalle, BorderLayout.CENTER);

        splitPane.setLeftComponent(panelArbol);
        splitPane.setRightComponent(panelDetalle);
        splitPane.setDividerLocation(350);

        panelPasos.add(splitPane, BorderLayout.CENTER);
    }

    private void crearPanelGrafico() {
        panelGrafico = new JPanel(new BorderLayout());

        // Título
        JLabel lblTitulo = new JLabel("Visualización del Proceso de Inferencia");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);

        // Panel de visualización
        panelVisualizacion = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarGraficoInferencia(g);
            }
        };
        panelVisualizacion.setBackground(Color.WHITE);
        panelVisualizacion.setBorder(BorderFactory.createLoweredBevelBorder());

        // Panel de controles
        JPanel panelControlesGrafico = new JPanel(new FlowLayout());
        JButton btnZoomIn = new JButton("Zoom +");
        JButton btnZoomOut = new JButton("Zoom -");
        JButton btnExportarGrafico = new JButton("Exportar Gráfico");

        panelControlesGrafico.add(btnZoomIn);
        panelControlesGrafico.add(btnZoomOut);
        panelControlesGrafico.add(btnExportarGrafico);

        panelGrafico.add(lblTitulo, BorderLayout.NORTH);
        panelGrafico.add(panelVisualizacion, BorderLayout.CENTER);
        panelGrafico.add(panelControlesGrafico, BorderLayout.SOUTH);
    }

    private void crearPanelBotones() {
        panelBotones = new JPanel(new FlowLayout());

        btnExportar = new JButton("Exportar Resultados");
        btnExportar.addActionListener(e -> exportarResultados());

        btnImprimir = new JButton("Imprimir");
        btnImprimir.addActionListener(e -> imprimirResultados());

        btnNuevoAnalisis = new JButton("Nuevo Análisis");
        btnNuevoAnalisis.addActionListener(e -> nuevoAnalisis());

        btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());

        panelBotones.add(btnExportar);
        panelBotones.add(btnImprimir);
        panelBotones.add(btnNuevoAnalisis);
        panelBotones.add(btnCerrar);
    }

    private void cargarDatos() {
        // Cargar conclusiones en el resumen
        StringBuilder conclusionesTexto = new StringBuilder();
        if (conclusiones.isEmpty()) {
            conclusionesTexto.append("No se obtuvieron conclusiones con los datos proporcionados.\n\n");
            conclusionesTexto.append("Posibles causas:\n");
            conclusionesTexto.append("• Los datos ingresados no activan ninguna regla\n");
            conclusionesTexto.append("• Las reglas definidas no cubren este caso\n");
            conclusionesTexto.append("• Los valores no cumplen los criterios establecidos\n");
        } else {
            conclusionesTexto.append("CONCLUSIONES OBTENIDAS:\n\n");
            for (int i = 0; i < conclusiones.size(); i++) {
                conclusionesTexto.append((i + 1)).append(". ")
                               .append(conclusiones.get(i)).append("\n");
            }

            conclusionesTexto.append("\nRECOMENDACIONES:\n");
            conclusionesTexto.append("• Revise las conclusiones obtenidas\n");
            conclusionesTexto.append("• Consulte la explicación detallada\n");
            conclusionesTexto.append("• Analice los pasos de inferencia\n");
        }

        txtConclusiones.setText(conclusionesTexto.toString());

        // Cargar explicación completa
        txtExplicacionCompleta.setText(explicacionCompleta);

        // Cargar árbol de pasos
        cargarArbolPasos();
    }

    private int calcularConfianzaPromedio() {
        if (pasos == null || pasos.isEmpty()) {
            return conclusiones.isEmpty() ? 0 : 75; // Valor por defecto
        }

        double sumaConfianza = 0;
        for (MotorInferenciaAvanzado.PasoInferencia paso : pasos) {
            sumaConfianza += paso.getCerteza();
        }

        return (int) ((sumaConfianza / pasos.size()) * 100);
    }

    private void cargarArbolPasos() {
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Proceso de Inferencia");

        if (pasos != null && !pasos.isEmpty()) {
            for (MotorInferenciaAvanzado.PasoInferencia paso : pasos) {
                DefaultMutableTreeNode nodoPaso = new DefaultMutableTreeNode(
                    "Paso " + paso.getNumero() + ": " + paso.getRegla()
                );

                // Agregar condiciones
                DefaultMutableTreeNode nodoCondiciones = new DefaultMutableTreeNode("Condiciones");
                for (String condicion : paso.getCondicionesEvaluadas()) {
                    nodoCondiciones.add(new DefaultMutableTreeNode(condicion));
                }
                nodoPaso.add(nodoCondiciones);

                // Agregar nuevos hechos
                if (paso.getNuevosHechos() != null && !paso.getNuevosHechos().isEmpty()) {
                    DefaultMutableTreeNode nodoHechos = new DefaultMutableTreeNode("Nuevos Hechos");
                    for (Hecho hecho : paso.getNuevosHechos()) {
                        nodoHechos.add(new DefaultMutableTreeNode(hecho.toString()));
                    }
                    nodoPaso.add(nodoHechos);
                }

                raiz.add(nodoPaso);
            }
        } else {
            raiz.add(new DefaultMutableTreeNode("No hay pasos registrados"));
        }

        DefaultTreeModel modelo = new DefaultTreeModel(raiz);
        arbolPasos.setModel(modelo);

        // Expandir todos los nodos
        for (int i = 0; i < arbolPasos.getRowCount(); i++) {
            arbolPasos.expandRow(i);
        }
    }

    private void mostrarDetallePaso() {
        DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) arbolPasos.getLastSelectedPathComponent();

        if (nodo == null) return;

        String textoNodo = nodo.toString();

        if (textoNodo.startsWith("Paso ")) {
            // Extraer número del paso
            try {
                String[] partes = textoNodo.split(":");
                String numeroParte = partes[0].replace("Paso ", "");
                int numeroPaso = Integer.parseInt(numeroParte);

                if (pasos != null && numeroPaso <= pasos.size()) {
                    MotorInferenciaAvanzado.PasoInferencia paso = pasos.get(numeroPaso - 1);
                    mostrarDetallePasoCompleto(paso);
                }
            } catch (Exception e) {
                txtDetallePaso.setText("Error al cargar detalles del paso");
            }
        } else {
            txtDetallePaso.setText("Seleccione un paso para ver los detalles");
        }
    }

    private void mostrarDetallePasoCompleto(MotorInferenciaAvanzado.PasoInferencia paso) {
        StringBuilder detalle = new StringBuilder();

        detalle.append("=== DETALLE DEL PASO ").append(paso.getNumero()).append(" ===\n\n");
        detalle.append("Regla: ").append(paso.getRegla()).append("\n");
        detalle.append("Factor de Certeza: ").append(String.format("%.2f", paso.getCerteza())).append("\n\n");

        detalle.append("CONDICIONES EVALUADAS:\n");
        for (String condicion : paso.getCondicionesEvaluadas()) {
            detalle.append("• ").append(condicion).append("\n");
        }

        detalle.append("\nNUEVOS HECHOS GENERADOS:\n");
        if (paso.getNuevosHechos() != null && !paso.getNuevosHechos().isEmpty()) {
            for (Hecho hecho : paso.getNuevosHechos()) {
                detalle.append("• ").append(hecho.toString()).append("\n");
            }
        } else {
            detalle.append("Ningún hecho nuevo generado\n");
        }

        detalle.append("\nIMPACTO:\n");
        detalle.append("Este paso ");
        if (paso.getNuevosHechos() != null && !paso.getNuevosHechos().isEmpty()) {
            detalle.append("generó ").append(paso.getNuevosHechos().size())
                   .append(" nuevo(s) hecho(s) que pueden activar reglas adicionales.");
        } else {
            detalle.append("no generó nuevos hechos.");
        }

        txtDetallePaso.setText(detalle.toString());
    }

    private void dibujarGraficoInferencia(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = panelVisualizacion.getWidth();
        int height = panelVisualizacion.getHeight();

        if (pasos == null || pasos.isEmpty()) {
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Arial", Font.ITALIC, 16));
            String mensaje = "No hay datos para visualizar";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (width - fm.stringWidth(mensaje)) / 2;
            int y = height / 2;
            g2d.drawString(mensaje, x, y);
            return;
        }

        // Dibujar diagrama de flujo simplificado
        int stepHeight = 60;
        int stepWidth = 200;
        int startY = 50;
        int centerX = width / 2;

        for (int i = 0; i < pasos.size(); i++) {
            MotorInferenciaAvanzado.PasoInferencia paso = pasos.get(i);
            int y = startY + (i * (stepHeight + 20));

            // Dibujar rectángulo del paso
            g2d.setColor(new Color(173, 216, 230));
            g2d.fillRoundRect(centerX - stepWidth/2, y, stepWidth, stepHeight, 10, 10);

            g2d.setColor(Color.BLACK);
            g2d.drawRoundRect(centerX - stepWidth/2, y, stepWidth, stepHeight, 10, 10);

            // Dibujar texto del paso
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            String texto = "Paso " + paso.getNumero() + ": " + paso.getRegla();
            if (texto.length() > 25) {
                texto = texto.substring(0, 22) + "...";
            }

            FontMetrics fm = g2d.getFontMetrics();
            int textX = centerX - fm.stringWidth(texto) / 2;
            int textY = y + stepHeight / 2 + fm.getAscent() / 2;
            g2d.drawString(texto, textX, textY);

            // Dibujar flecha al siguiente paso
            if (i < pasos.size() - 1) {
                g2d.setColor(Color.BLUE);
                int arrowX = centerX;
                int arrowY1 = y + stepHeight;
                int arrowY2 = y + stepHeight + 20;

                g2d.drawLine(arrowX, arrowY1, arrowX, arrowY2);
                g2d.drawLine(arrowX, arrowY2, arrowX - 5, arrowY2 - 5);
                g2d.drawLine(arrowX, arrowY2, arrowX + 5, arrowY2 - 5);
            }
        }
    }

    private void buscarEnExplicacion() {
        String termino = JOptionPane.showInputDialog(this, "Ingrese el término a buscar:");
        if (termino != null && !termino.trim().isEmpty()) {
            String texto = txtExplicacionCompleta.getText();
            int indice = texto.toLowerCase().indexOf(termino.toLowerCase());

            if (indice >= 0) {
                txtExplicacionCompleta.setCaretPosition(indice);
                txtExplicacionCompleta.select(indice, indice + termino.length());
                JOptionPane.showMessageDialog(this, "Término encontrado", "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Término no encontrado", "Búsqueda", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void resaltarPasosClave() {
        // Implementación básica - en una versión completa se podrían resaltar líneas específicas
        JOptionPane.showMessageDialog(this,
            "Los pasos clave están marcados con 'PASO X:' en la explicación",
            "Pasos Clave",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void copiarExplicacion() {
        txtExplicacionCompleta.selectAll();
        txtExplicacionCompleta.copy();
        JOptionPane.showMessageDialog(this,
            "Explicación copiada al portapapeles",
            "Copiado",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportarResultados() {
        JOptionPane.showMessageDialog(this,
            "Función de exportación no implementada en esta versión demo",
            "Exportar",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void imprimirResultados() {
        JOptionPane.showMessageDialog(this,
            "Función de impresión no implementada en esta versión demo",
            "Imprimir",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void nuevoAnalisis() {
        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Desea realizar un nuevo análisis?\nEsto cerrará la ventana actual.",
            "Nuevo Análisis",
            JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            dispose();
        }
    }
}