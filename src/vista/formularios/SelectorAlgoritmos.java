package vista.formularios;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import modelo.motor_inferencia.EstrategiaInferencia;

/**
 * Selector avanzado de algoritmos y estrategias de inferencia
 */
public class SelectorAlgoritmos extends JDialog {

    // Variables declaration
    private String algoritmoSeleccionado;
    private EstrategiaInferencia estrategiaSeleccionada;
    private boolean seleccionConfirmada = false;

    private JPanel panelAlgoritmos;
    private JPanel panelEstrategias;
    private JPanel panelDescripcion;
    private JPanel panelBotones;

    private ButtonGroup grupoAlgoritmos;
    private JRadioButton rbForwardChaining;
    private JRadioButton rbBackwardChaining;
    private JRadioButton rbMotorAvanzado;

    private ButtonGroup grupoEstrategias;
    private JRadioButton rbAmplitudPrimero;
    private JRadioButton rbProfundidadPrimero;
    private JRadioButton rbPrioridadReglas;
    private JRadioButton rbFactorCerteza;

    private JTextArea txtDescripcion;
    private JPanel panelVisualizacion;

    private JButton btnConfirmar;
    private JButton btnCancelar;
    private JButton btnAyuda;
    private JButton btnComparar;

    public SelectorAlgoritmos(JFrame parent) {
        super(parent, "Selección de Algoritmo y Estrategia", true);
        initComponents();
        configurarListeners();
        actualizarDescripcion();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(getParent());

        // Panel principal con pestañas
        JTabbedPane panelPestanas = new JTabbedPane();

        // Pestaña 1: Selección de Algoritmo
        crearPanelSeleccion();
        panelPestanas.addTab("Selección", crearPanelPrincipal());

        // Pestaña 2: Comparación de Algoritmos
        panelPestanas.addTab("Comparación", crearPanelComparacion());

        // Pestaña 3: Ayuda y Ejemplos
        panelPestanas.addTab("Ayuda", crearPanelAyuda());

        // Panel de botones
        crearPanelBotones();

        // Layout principal
        setLayout(new BorderLayout());
        add(panelPestanas, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private JPanel crearPanelPrincipal() {
        JPanel panel = new JPanel(new BorderLayout());

        // Panel superior: Selección de algoritmos
        panelAlgoritmos = new JPanel();
        panelAlgoritmos.setBorder(BorderFactory.createTitledBorder("Algoritmos de Inferencia"));
        panelAlgoritmos.setLayout(new GridLayout(3, 1, 5, 5));

        grupoAlgoritmos = new ButtonGroup();

        rbForwardChaining = new JRadioButton("Forward Chaining (Encadenamiento hacia adelante)");
        rbForwardChaining.setSelected(true);
        rbBackwardChaining = new JRadioButton("Backward Chaining (Encadenamiento hacia atrás)");
        rbMotorAvanzado = new JRadioButton("Motor Avanzado (Múltiples estrategias)");

        grupoAlgoritmos.add(rbForwardChaining);
        grupoAlgoritmos.add(rbBackwardChaining);
        grupoAlgoritmos.add(rbMotorAvanzado);

        panelAlgoritmos.add(rbForwardChaining);
        panelAlgoritmos.add(rbBackwardChaining);
        panelAlgoritmos.add(rbMotorAvanzado);

        // Panel central: Selección de estrategias
        panelEstrategias = new JPanel();
        panelEstrategias.setBorder(BorderFactory.createTitledBorder("Estrategias de Ejecución"));
        panelEstrategias.setLayout(new GridLayout(4, 1, 5, 5));

        grupoEstrategias = new ButtonGroup();

        rbAmplitudPrimero = new JRadioButton("Búsqueda en Amplitud");
        rbAmplitudPrimero.setSelected(true);
        rbProfundidadPrimero = new JRadioButton("Búsqueda en Profundidad");
        rbPrioridadReglas = new JRadioButton("Prioridad de Reglas");
        rbFactorCerteza = new JRadioButton("Factor de Certeza");

        grupoEstrategias.add(rbAmplitudPrimero);
        grupoEstrategias.add(rbProfundidadPrimero);
        grupoEstrategias.add(rbPrioridadReglas);
        grupoEstrategias.add(rbFactorCerteza);

        panelEstrategias.add(rbAmplitudPrimero);
        panelEstrategias.add(rbProfundidadPrimero);
        panelEstrategias.add(rbPrioridadReglas);
        panelEstrategias.add(rbFactorCerteza);

        // Panel de configuración
        JPanel panelConfig = new JPanel(new BorderLayout());
        panelConfig.add(panelAlgoritmos, BorderLayout.NORTH);
        panelConfig.add(panelEstrategias, BorderLayout.CENTER);

        // Panel inferior: Descripción
        panelDescripcion = new JPanel(new BorderLayout());
        panelDescripcion.setBorder(BorderFactory.createTitledBorder("Descripción y Características"));

        txtDescripcion = new JTextArea(8, 50);
        txtDescripcion.setEditable(false);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setFont(new Font("Arial", Font.PLAIN, 12));
        txtDescripcion.setBackground(new Color(248, 248, 255));

        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        panelDescripcion.add(scrollDescripcion, BorderLayout.CENTER);

        // Panel de visualización
        panelVisualizacion = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarVisualizacion(g);
            }
        };
        panelVisualizacion.setPreferredSize(new Dimension(200, 150));
        panelVisualizacion.setBackground(Color.WHITE);
        panelVisualizacion.setBorder(BorderFactory.createLoweredBevelBorder());

        panelDescripcion.add(panelVisualizacion, BorderLayout.EAST);

        panel.add(panelConfig, BorderLayout.NORTH);
        panel.add(panelDescripcion, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelComparacion() {
        JPanel panel = new JPanel(new BorderLayout());

        // Tabla de comparación
        String[] columnas = {"Característica", "Forward Chaining", "Backward Chaining", "Motor Avanzado"};
        String[][] datos = {
            {"Dirección", "Datos → Conclusiones", "Objetivos → Datos", "Bidireccional"},
            {"Velocidad", "Rápido", "Variable", "Optimizado"},
            {"Memoria", "Media", "Alta", "Eficiente"},
            {"Explicación", "Básica", "Detallada", "Completa"},
            {"Estrategias", "Limitadas", "Básicas", "Múltiples"},
            {"Complejidad", "Baja", "Media", "Alta"},
            {"Casos de Uso", "Diagnóstico simple", "Planificación", "Análisis complejo"},
            {"Certeza", "Básica", "Media", "Avanzada"}
        };

        JTable tablaComparacion = new JTable(datos, columnas);
        tablaComparacion.setRowHeight(25);
        tablaComparacion.getTableHeader().setBackground(new Color(173, 216, 230));

        JScrollPane scrollTabla = new JScrollPane(tablaComparacion);

        // Panel de recomendaciones
        JTextArea txtRecomendaciones = new JTextArea();
        txtRecomendaciones.setEditable(false);
        txtRecomendaciones.setFont(new Font("Arial", Font.PLAIN, 12));
        txtRecomendaciones.setText(
            "RECOMENDACIONES DE USO:\n\n" +
            "Forward Chaining:\n" +
            "   • Ideal para diagnósticos médicos\n" +
            "   • Sistemas de clasificación\n" +
            "   • Cuando tienes muchos datos iniciales\n\n" +
            "Backward Chaining:\n" +
            "   • Perfecto para planificación\n" +
            "   • Verificación de hipótesis específicas\n" +
            "   • Cuando buscas objetivos concretos\n\n" +
            "Motor Avanzado:\n" +
            "   • Análisis complejos que requieren múltiples enfoques\n" +
            "   • Sistemas que necesitan explicaciones detalladas\n" +
            "   • Cuando la certeza es crítica"
        );

        JScrollPane scrollRecomendaciones = new JScrollPane(txtRecomendaciones);
        scrollRecomendaciones.setPreferredSize(new Dimension(400, 200));

        panel.add(scrollTabla, BorderLayout.CENTER);
        panel.add(scrollRecomendaciones, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelAyuda() {
        JPanel panel = new JPanel(new BorderLayout());

        JTextArea txtAyuda = new JTextArea();
        txtAyuda.setEditable(false);
        txtAyuda.setFont(new Font("Monospaced", Font.PLAIN, 11));
        txtAyuda.setText(
            "GUÍA DE ALGORITMOS DE INFERENCIA\n" +
            "=====================================\n\n" +
            "1. FORWARD CHAINING (Encadenamiento hacia adelante)\n" +
            "   Funcionamiento:\n" +
            "   • Parte de los hechos conocidos\n" +
            "   • Aplica reglas para inferir nuevos hechos\n" +
            "   • Continúa hasta que no se puedan inferir más hechos\n\n" +
            "   Ejemplo:\n" +
            "   Hechos: [fiebre=true, temperatura=39]\n" +
            "   Regla: SI fiebre=true Y temperatura>38 ENTONCES gripe=posible\n" +
            "   Resultado: gripe=posible\n\n" +
            "2. BACKWARD CHAINING (Encadenamiento hacia atrás)\n" +
            "   Funcionamiento:\n" +
            "   • Parte de los objetivos a demostrar\n" +
            "   • Busca hechos y reglas que los sustenten\n" +
            "   • Trabaja hacia atrás hasta los hechos conocidos\n\n" +
            "   Ejemplo:\n" +
            "   Objetivo: ¿gripe=posible?\n" +
            "   Busca reglas que generen gripe=posible\n" +
            "   Verifica si se cumplen las condiciones\n\n" +
            "3. MOTOR AVANZADO\n" +
            "   Características:\n" +
            "   • Combina múltiples estrategias\n" +
            "   • Explicaciones paso a paso\n" +
            "   • Factores de certeza avanzados\n" +
            "   • Optimización de rendimiento\n\n" +
            "ESTRATEGIAS DE EJECUCIÓN:\n" +
            "=========================\n\n" +
            "• Amplitud Primero: Evalúa todas las reglas aplicables antes de continuar\n" +
            "• Profundidad Primero: Sigue una línea de razonamiento hasta el final\n" +
            "• Prioridad de Reglas: Ejecuta primero las reglas con mayor prioridad\n" +
            "• Factor de Certeza: Prioriza reglas con mayor factor de confianza\n\n" +
            "CONSEJOS PARA LA SELECCIÓN:\n" +
            "============================\n\n" +
            "1. Para diagnósticos rápidos: Forward Chaining + Amplitud\n" +
            "2. Para verificar hipótesis: Backward Chaining + Profundidad\n" +
            "3. Para análisis críticos: Motor Avanzado + Factor de Certeza\n" +
            "4. Para sistemas complejos: Motor Avanzado + Prioridad de Reglas"
        );

        JScrollPane scrollAyuda = new JScrollPane(txtAyuda);
        panel.add(scrollAyuda, BorderLayout.CENTER);

        return panel;
    }

    private void crearPanelSeleccion() {
        // Este método ya está integrado en crearPanelPrincipal()
    }

    private void crearPanelBotones() {
        panelBotones = new JPanel(new FlowLayout());

        btnAyuda = new JButton("Ayuda Rápida");
        btnAyuda.setIcon(createIcon("?", Color.BLUE));
        btnAyuda.addActionListener(e -> mostrarAyudaRapida());

        btnComparar = new JButton("Comparar Algoritmos");
        btnComparar.setIcon(createIcon("⚖", Color.ORANGE));
        btnComparar.addActionListener(e -> mostrarComparacion());

        btnConfirmar = new JButton("Confirmar Selección");
        btnConfirmar.setIcon(createIcon("✓", Color.GREEN));
        btnConfirmar.addActionListener(e -> confirmarSeleccion());

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setIcon(createIcon("✗", Color.RED));
        btnCancelar.addActionListener(e -> dispose());

        panelBotones.add(btnAyuda);
        panelBotones.add(btnComparar);
        panelBotones.add(btnConfirmar);
        panelBotones.add(btnCancelar);
    }

    private Icon createIcon(String text, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                g.setColor(color);
                g.setFont(new Font("Arial", Font.BOLD, 12));
                g.drawString(text, x, y + 10);
            }

            @Override
            public int getIconWidth() { return 15; }

            @Override
            public int getIconHeight() { return 15; }
        };
    }

    private void configurarListeners() {
        ActionListener actualizador = e -> actualizarDescripcion();

        rbForwardChaining.addActionListener(actualizador);
        rbBackwardChaining.addActionListener(actualizador);
        rbMotorAvanzado.addActionListener(actualizador);

        rbAmplitudPrimero.addActionListener(actualizador);
        rbProfundidadPrimero.addActionListener(actualizador);
        rbPrioridadReglas.addActionListener(actualizador);
        rbFactorCerteza.addActionListener(actualizador);
    }

    private void actualizarDescripcion() {
        StringBuilder descripcion = new StringBuilder();

        // Descripción del algoritmo
        if (rbForwardChaining.isSelected()) {
            descripcion.append("FORWARD CHAINING (Encadenamiento hacia adelante)\n\n");
            descripcion.append("Funcionamiento:\n");
            descripcion.append("• Comienza con los hechos conocidos\n");
            descripcion.append("• Aplica reglas para inferir nuevos hechos\n");
            descripcion.append("• Continúa hasta que no se puedan inferir más hechos\n\n");
            descripcion.append("Ventajas:\n");
            descripcion.append("• Rápido y eficiente\n");
            descripcion.append("• Ideal para diagnósticos\n");
            descripcion.append("• Explora todas las posibilidades\n\n");
            descripcion.append("Desventajas:\n");
            descripcion.append("• Puede generar información irrelevante\n");
            descripcion.append("• Uso intensivo de memoria\n");
        } else if (rbBackwardChaining.isSelected()) {
            descripcion.append("BACKWARD CHAINING (Encadenamiento hacia atrás)\n\n");
            descripcion.append("Funcionamiento:\n");
            descripcion.append("• Comienza con los objetivos a demostrar\n");
            descripcion.append("• Busca reglas que puedan generar esos objetivos\n");
            descripcion.append("• Verifica recursivamente las condiciones\n\n");
            descripcion.append("Ventajas:\n");
            descripcion.append("• Enfocado en objetivos específicos\n");
            descripcion.append("• Eficiente para verificación de hipótesis\n");
            descripcion.append("• Evita cálculos innecesarios\n\n");
            descripcion.append("Desventajas:\n");
            descripcion.append("• Puede ser más lento en casos complejos\n");
            descripcion.append("• Requiere objetivos bien definidos\n");
        } else if (rbMotorAvanzado.isSelected()) {
            descripcion.append("MOTOR AVANZADO (Múltiples estrategias)\n\n");
            descripcion.append("Funcionamiento:\n");
            descripcion.append("• Combina Forward y Backward Chaining\n");
            descripcion.append("• Utiliza múltiples estrategias de optimización\n");
            descripcion.append("• Proporciona explicaciones detalladas\n\n");
            descripcion.append("Ventajas:\n");
            descripcion.append("• Máxima flexibilidad y potencia\n");
            descripcion.append("• Explicaciones paso a paso\n");
            descripcion.append("• Manejo avanzado de certeza\n\n");
            descripcion.append("Características:\n");
            descripcion.append("• Análisis de pasos de inferencia\n");
            descripcion.append("• Visualización gráfica del proceso\n");
        }

        // Descripción de la estrategia
        descripcion.append("\nESTRATEGIA SELECCIONADA:\n");

        if (rbAmplitudPrimero.isSelected()) {
            descripcion.append("• Búsqueda en Amplitud: Explora todas las reglas aplicables en cada nivel antes de continuar\n");
            descripcion.append("• Ideal para: Exploración exhaustiva, encontrar múltiples soluciones\n");
        } else if (rbProfundidadPrimero.isSelected()) {
            descripcion.append("• Búsqueda en Profundidad: Sigue una línea de razonamiento hasta el final antes de explorar alternativas\n");
            descripcion.append("• Ideal para: Verificación rápida de hipótesis específicas\n");
        } else if (rbPrioridadReglas.isSelected()) {
            descripcion.append("• Prioridad de Reglas: Ejecuta primero las reglas con mayor prioridad asignada\n");
            descripcion.append("• Ideal para: Sistemas con jerarquías de importancia bien definidas\n");
        } else if (rbFactorCerteza.isSelected()) {
            descripcion.append("• Factor de Certeza: Prioriza reglas con mayor factor de confianza\n");
            descripcion.append("• Ideal para: Análisis donde la certeza es crítica\n");
        }

        txtDescripcion.setText(descripcion.toString());
        panelVisualizacion.repaint();
    }

    private void dibujarVisualizacion(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = panelVisualizacion.getWidth();
        int height = panelVisualizacion.getHeight();

        // Limpiar el fondo
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // Dibujar visualización según el algoritmo seleccionado
        if (rbForwardChaining.isSelected()) {
            dibujarForwardChaining(g2d, width, height);
        } else if (rbBackwardChaining.isSelected()) {
            dibujarBackwardChaining(g2d, width, height);
        } else if (rbMotorAvanzado.isSelected()) {
            dibujarMotorAvanzado(g2d, width, height);
        }
    }

    private void dibujarForwardChaining(Graphics2D g2d, int width, int height) {
        // Dibujar diagrama simple de Forward Chaining
        g2d.setColor(Color.BLUE);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));

        // Hechos iniciales
        g2d.fillRect(10, 10, 60, 30);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Hechos", 15, 28);

        // Flecha
        g2d.setColor(Color.BLACK);
        g2d.drawLine(70, 25, 100, 25);
        g2d.drawLine(95, 20, 100, 25);
        g2d.drawLine(95, 30, 100, 25);

        // Reglas
        g2d.setColor(Color.GREEN);
        g2d.fillRect(105, 10, 60, 30);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Reglas", 115, 28);

        // Flecha
        g2d.setColor(Color.BLACK);
        g2d.drawLine(165, 25, 195, 25);
        g2d.drawLine(190, 20, 195, 25);
        g2d.drawLine(190, 30, 195, 25);

        // Conclusiones
        g2d.setColor(Color.RED);
        g2d.fillRect(200, 10, 70, 30);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Conclusiones", 205, 28);

        // Etiqueta de dirección
        g2d.setColor(Color.BLACK);
        g2d.drawString("Datos → Conclusiones", 10, 60);
    }

    private void dibujarBackwardChaining(Graphics2D g2d, int width, int height) {
        // Dibujar diagrama simple de Backward Chaining
        g2d.setColor(Color.RED);
        g2d.setFont(new Font("Arial", Font.BOLD, 10));

        // Objetivos
        g2d.fillRect(10, 10, 60, 30);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Objetivos", 15, 28);

        // Flecha hacia atrás
        g2d.setColor(Color.BLACK);
        g2d.drawLine(70, 25, 100, 25);
        g2d.drawLine(75, 20, 70, 25);
        g2d.drawLine(75, 30, 70, 25);

        // Reglas
        g2d.setColor(Color.GREEN);
        g2d.fillRect(105, 10, 60, 30);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Reglas", 115, 28);

        // Flecha hacia atrás
        g2d.setColor(Color.BLACK);
        g2d.drawLine(165, 25, 195, 25);
        g2d.drawLine(170, 20, 165, 25);
        g2d.drawLine(170, 30, 165, 25);

        // Hechos
        g2d.setColor(Color.BLUE);
        g2d.fillRect(200, 10, 60, 30);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Hechos", 210, 28);

        // Etiqueta de dirección
        g2d.setColor(Color.BLACK);
        g2d.drawString("Objetivos ← Hechos", 10, 60);
    }

    private void dibujarMotorAvanzado(Graphics2D g2d, int width, int height) {
        // Dibujar diagrama del motor avanzado
        g2d.setColor(Color.MAGENTA);
        g2d.setFont(new Font("Arial", Font.BOLD, 9));

        // Centro del diagrama
        g2d.fillOval(80, 40, 60, 40);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Motor", 95, 58);
        g2d.drawString("Avanzado", 85, 70);

        // Componentes alrededor
        g2d.setColor(Color.BLUE);
        g2d.fillRect(10, 10, 40, 20);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 8));
        g2d.drawString("Hechos", 15, 22);

        g2d.setColor(Color.GREEN);
        g2d.fillRect(170, 10, 40, 20);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Reglas", 180, 22);

        g2d.setColor(Color.RED);
        g2d.fillRect(10, 100, 50, 20);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Objetivos", 15, 112);

        g2d.setColor(Color.ORANGE);
        g2d.fillRect(160, 100, 60, 20);
        g2d.setColor(Color.WHITE);
        g2d.drawString("Explicaciones", 165, 112);

        // Líneas de conexión
        g2d.setColor(Color.BLACK);
        g2d.drawLine(50, 20, 80, 50);
        g2d.drawLine(170, 20, 140, 50);
        g2d.drawLine(60, 100, 90, 80);
        g2d.drawLine(160, 110, 130, 80);

        // Etiqueta
        g2d.drawString("Bidireccional + Estrategias", 10, 140);
    }

    private void mostrarAyudaRapida() {
        String ayuda = "SELECCIÓN RÁPIDA:\n\n" +
                      "¿Tienes muchos datos y quieres ver qué puedes concluir?\n" +
                      "   → Forward Chaining + Amplitud\n\n" +
                      "¿Quieres verificar si algo específico es cierto?\n" +
                      "   → Backward Chaining + Profundidad\n\n" +
                      "¿Necesitas el análisis más completo posible?\n" +
                      "   → Motor Avanzado + Factor de Certeza\n\n" +
                      "¿Las reglas tienen diferentes niveles de importancia?\n" +
                      "   → Cualquier algoritmo + Prioridad de Reglas";

        JOptionPane.showMessageDialog(this, ayuda, "Ayuda Rápida", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarComparacion() {
        // Esta funcionalidad se activaría cambiando a la pestaña de comparación
        JOptionPane.showMessageDialog(this,
            "Consulte la pestaña 'Comparación' para ver una tabla detallada de diferencias",
            "Comparación",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void confirmarSeleccion() {
        // Determinar algoritmo seleccionado
        if (rbForwardChaining.isSelected()) {
            algoritmoSeleccionado = "Forward Chaining";
        } else if (rbBackwardChaining.isSelected()) {
            algoritmoSeleccionado = "Backward Chaining";
        } else if (rbMotorAvanzado.isSelected()) {
            algoritmoSeleccionado = "Motor Avanzado";
        }

        // Determinar estrategia seleccionada
        if (rbAmplitudPrimero.isSelected()) {
            estrategiaSeleccionada = EstrategiaInferencia.AMPLITUD_PRIMERO;
        } else if (rbProfundidadPrimero.isSelected()) {
            estrategiaSeleccionada = EstrategiaInferencia.PROFUNDIDAD_PRIMERO;
        } else if (rbPrioridadReglas.isSelected()) {
            estrategiaSeleccionada = EstrategiaInferencia.PRIORIDAD_REGLAS;
        } else if (rbFactorCerteza.isSelected()) {
            estrategiaSeleccionada = EstrategiaInferencia.FACTOR_CERTEZA;
        }

        // Mostrar confirmación
        String mensaje = "Configuración seleccionada:\n\n" +
                        "Algoritmo: " + algoritmoSeleccionado + "\n" +
                        "Estrategia: " + estrategiaSeleccionada + "\n\n" +
                        "¿Confirma esta selección?";

        int respuesta = JOptionPane.showConfirmDialog(this, mensaje, "Confirmar Selección", JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            seleccionConfirmada = true;
            dispose();
        }
    }

    // Getters para obtener las selecciones
    public String getAlgoritmoSeleccionado() {
        return algoritmoSeleccionado;
    }

    public EstrategiaInferencia getEstrategiaSeleccionada() {
        return estrategiaSeleccionada;
    }

    public boolean isSeleccionConfirmada() {
        return seleccionConfirmada;
    }
}