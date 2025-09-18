package modelo.motor_inferencia;

import modelo.entidades.*;
import java.util.*;

/**
 * Motor de inferencia avanzado con múltiples estrategias y explicaciones detalladas
 */
public class MotorInferenciaAvanzado implements MotorInferencia {

    private EstrategiaInferencia estrategia;
    private List<String> conclusiones;
    private StringBuilder explicacionDetallada;
    private List<PasoInferencia> pasosInferencia;
    private Map<String, Double> factoresCerteza;
    private Set<String> reglasProcesadas;
    private int contadorPasos;

    public MotorInferenciaAvanzado() {
        this.estrategia = EstrategiaInferencia.AMPLITUD_PRIMERO;
        this.conclusiones = new ArrayList<>();
        this.explicacionDetallada = new StringBuilder();
        this.pasosInferencia = new ArrayList<>();
        this.factoresCerteza = new HashMap<>();
        this.reglasProcesadas = new HashSet<>();
        this.contadorPasos = 0;
    }

    @Override
    public List<String> ejecutar(List<Hecho> hechos, List<Regla> reglas, List<Objetivo> objetivos) {
        inicializar();

        explicacionDetallada.append("=== INICIO DEL PROCESO DE INFERENCIA ===\n");
        explicacionDetallada.append("Estrategia: ").append(estrategia).append("\n");
        explicacionDetallada.append("Hechos iniciales: ").append(hechos.size()).append("\n");
        explicacionDetallada.append("Reglas disponibles: ").append(reglas.size()).append("\n");
        explicacionDetallada.append("Objetivos a evaluar: ").append(objetivos.size()).append("\n\n");

        // Mostrar hechos iniciales
        explicacionDetallada.append("--- HECHOS INICIALES ---\n");
        for (Hecho hecho : hechos) {
            explicacionDetallada.append("• ").append(hecho.toString()).append("\n");
        }
        explicacionDetallada.append("\n");

        List<Hecho> baseDatos = new ArrayList<>(hechos);

        switch (estrategia) {
            case AMPLITUD_PRIMERO:
                ejecutarAmplitudPrimero(baseDatos, reglas, objetivos);
                break;
            case PROFUNDIDAD_PRIMERO:
                ejecutarProfundidadPrimero(baseDatos, reglas, objetivos);
                break;
            case PRIORIDAD_REGLAS:
                ejecutarPorPrioridad(baseDatos, reglas, objetivos);
                break;
            case FACTOR_CERTEZA:
                ejecutarPorCerteza(baseDatos, reglas, objetivos);
                break;
        }

        evaluarObjetivos(baseDatos, objetivos);
        generarResumenFinal();

        return new ArrayList<>(conclusiones);
    }

    private void inicializar() {
        conclusiones.clear();
        explicacionDetallada.setLength(0);
        pasosInferencia.clear();
        factoresCerteza.clear();
        reglasProcesadas.clear();
        contadorPasos = 0;
    }

    private void ejecutarAmplitudPrimero(List<Hecho> baseDatos, List<Regla> reglas, List<Objetivo> objetivos) {
        explicacionDetallada.append("--- EJECUTANDO AMPLITUD PRIMERO ---\n");

        Queue<Regla> cola = new LinkedList<>();
        Set<Integer> reglasEnCola = new HashSet<>();

        // Agregar todas las reglas aplicables inicialmente
        for (Regla regla : reglas) {
            if (puedeAplicarse(regla, baseDatos)) {
                cola.offer(regla);
                reglasEnCola.add(regla.getId());
            }
        }

        while (!cola.isEmpty()) {
            Regla reglaActual = cola.poll();
            reglasEnCola.remove(reglaActual.getId());

            if (aplicarRegla(reglaActual, baseDatos)) {
                // Buscar nuevas reglas aplicables
                for (Regla regla : reglas) {
                    if (!reglasEnCola.contains(regla.getId()) &&
                        !reglasProcesadas.contains(regla.getNombre()) &&
                        puedeAplicarse(regla, baseDatos)) {
                        cola.offer(regla);
                        reglasEnCola.add(regla.getId());
                    }
                }
            }
        }
    }

    private void ejecutarProfundidadPrimero(List<Hecho> baseDatos, List<Regla> reglas, List<Objetivo> objetivos) {
        explicacionDetallada.append("--- EJECUTANDO PROFUNDIDAD PRIMERO ---\n");

        Stack<Regla> pila = new Stack<>();

        // Agregar reglas aplicables
        for (Regla regla : reglas) {
            if (puedeAplicarse(regla, baseDatos)) {
                pila.push(regla);
            }
        }

        while (!pila.isEmpty()) {
            Regla reglaActual = pila.pop();

            if (aplicarRegla(reglaActual, baseDatos)) {
                // Agregar nuevas reglas aplicables al tope de la pila
                for (Regla regla : reglas) {
                    if (!reglasProcesadas.contains(regla.getNombre()) &&
                        puedeAplicarse(regla, baseDatos)) {
                        pila.push(regla);
                    }
                }
            }
        }
    }

    private void ejecutarPorPrioridad(List<Hecho> baseDatos, List<Regla> reglas, List<Objetivo> objetivos) {
        explicacionDetallada.append("--- EJECUTANDO POR PRIORIDAD ---\n");

        List<Regla> reglasOrdenadas = new ArrayList<>(reglas);
        reglasOrdenadas.sort((r1, r2) -> Integer.compare(r2.getPrioridad(), r1.getPrioridad()));

        boolean cambios = true;
        while (cambios) {
            cambios = false;
            for (Regla regla : reglasOrdenadas) {
                if (!reglasProcesadas.contains(regla.getNombre()) && puedeAplicarse(regla, baseDatos)) {
                    if (aplicarRegla(regla, baseDatos)) {
                        cambios = true;
                    }
                }
            }
        }
    }

    private void ejecutarPorCerteza(List<Hecho> baseDatos, List<Regla> reglas, List<Objetivo> objetivos) {
        explicacionDetallada.append("--- EJECUTANDO POR FACTOR DE CERTEZA ---\n");

        List<Regla> reglasOrdenadas = new ArrayList<>(reglas);
        reglasOrdenadas.sort((r1, r2) -> Double.compare(r2.getFactorCerteza(), r1.getFactorCerteza()));

        boolean cambios = true;
        while (cambios) {
            cambios = false;
            for (Regla regla : reglasOrdenadas) {
                if (!reglasProcesadas.contains(regla.getNombre()) && puedeAplicarse(regla, baseDatos)) {
                    if (aplicarRegla(regla, baseDatos)) {
                        cambios = true;
                    }
                }
            }
        }
    }

    private boolean puedeAplicarse(Regla regla, List<Hecho> baseDatos) {
        if (reglasProcesadas.contains(regla.getNombre())) {
            return false;
        }

        for (String condicion : regla.getCondiciones()) {
            if (!evaluarCondicion(condicion, baseDatos)) {
                return false;
            }
        }
        return true;
    }

    private boolean evaluarCondicion(String condicion, List<Hecho> baseDatos) {
        // Parsear la condición (ej: "fiebre = true", "temperatura > 38")
        condicion = condicion.trim();

        if (condicion.contains("=")) {
            String[] partes = condicion.split("=");
            if (partes.length == 2) {
                String premisa = partes[0].trim();
                String valor = partes[1].trim();

                for (Hecho hecho : baseDatos) {
                    if (hecho.getPremisaNombre().equals(premisa) &&
                        hecho.getValor().equals(valor)) {
                        return true;
                    }
                }
            }
        } else if (condicion.contains(">")) {
            String[] partes = condicion.split(">");
            if (partes.length == 2) {
                String premisa = partes[0].trim();
                double valorLimite = Double.parseDouble(partes[1].trim());

                for (Hecho hecho : baseDatos) {
                    if (hecho.getPremisaNombre().equals(premisa)) {
                        try {
                            double valorHecho = Double.parseDouble(hecho.getValor());
                            return valorHecho > valorLimite;
                        } catch (NumberFormatException e) {
                            // Si no es numérico, no se puede comparar
                        }
                    }
                }
            }
        } else if (condicion.contains("<")) {
            String[] partes = condicion.split("<");
            if (partes.length == 2) {
                String premisa = partes[0].trim();
                double valorLimite = Double.parseDouble(partes[1].trim());

                for (Hecho hecho : baseDatos) {
                    if (hecho.getPremisaNombre().equals(premisa)) {
                        try {
                            double valorHecho = Double.parseDouble(hecho.getValor());
                            return valorHecho < valorLimite;
                        } catch (NumberFormatException e) {
                            // Si no es numérico, no se puede comparar
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean aplicarRegla(Regla regla, List<Hecho> baseDatos) {
        contadorPasos++;

        PasoInferencia paso = new PasoInferencia();
        paso.setNumero(contadorPasos);
        paso.setRegla(regla.getNombre());
        paso.setCondicionesEvaluadas(new ArrayList<>(regla.getCondiciones()));

        explicacionDetallada.append("PASO ").append(contadorPasos).append(": Aplicando regla '")
                           .append(regla.getNombre()).append("'\n");

        // Verificar condiciones
        explicacionDetallada.append("Condiciones evaluadas:\n");
        for (String condicion : regla.getCondiciones()) {
            boolean cumplida = evaluarCondicion(condicion, baseDatos);
            explicacionDetallada.append("  • ").append(condicion).append(" = ")
                               .append(cumplida ? "VERDADERO" : "FALSO").append("\n");
        }

        // Aplicar acciones
        List<Hecho> nuevosHechos = new ArrayList<>();
        double certezaRegla = regla.calcularCerteza(baseDatos);

        explicacionDetallada.append("Acciones ejecutadas:\n");
        for (String accion : regla.getAcciones()) {
            Hecho nuevoHecho = crearHechoDesdeAccion(accion, certezaRegla);
            if (nuevoHecho != null && !existeHecho(nuevoHecho, baseDatos)) {
                baseDatos.add(nuevoHecho);
                nuevosHechos.add(nuevoHecho);
                conclusiones.add(accion + " (certeza: " + String.format("%.2f", certezaRegla) + ")");

                explicacionDetallada.append("  → ").append(accion)
                                   .append(" (certeza: ").append(String.format("%.2f", certezaRegla))
                                   .append(")\n");
            }
        }

        paso.setNuevosHechos(nuevosHechos);
        paso.setCerteza(certezaRegla);
        pasosInferencia.add(paso);

        reglasProcesadas.add(regla.getNombre());
        explicacionDetallada.append("\n");

        return !nuevosHechos.isEmpty();
    }

    private boolean existeHecho(Hecho hecho, List<Hecho> baseDatos) {
        return baseDatos.stream().anyMatch(h ->
            h.getPremisaNombre().equals(hecho.getPremisaNombre()) &&
            h.getValor().equals(hecho.getValor()));
    }

    private Hecho crearHechoDesdeAccion(String accion, double certeza) {
        // Parsear la acción (ej: "diagnostico = gripe")
        if (accion.contains("=")) {
            String[] partes = accion.split("=");
            if (partes.length == 2) {
                Hecho hecho = new Hecho();
                hecho.setPremisaNombre(partes[0].trim());
                hecho.setValor(partes[1].trim());
                hecho.setCerteza(certeza);
                hecho.setFuente("inferencia");
                return hecho;
            }
        }
        return null;
    }

    private void evaluarObjetivos(List<Hecho> baseDatos, List<Objetivo> objetivos) {
        explicacionDetallada.append("--- EVALUACIÓN DE OBJETIVOS ---\n");

        for (Objetivo objetivo : objetivos) {
            boolean cumplido = false;
            double certezaTotal = 0.0;
            int coincidencias = 0;

            for (Hecho hecho : baseDatos) {
                if (hecho.getPremisaNombre().contains(objetivo.getNombre()) ||
                    hecho.getValor().contains(objetivo.getNombre())) {
                    cumplido = true;
                    certezaTotal += hecho.getCerteza();
                    coincidencias++;
                }
            }

            if (cumplido) {
                double certezaPromedio = certezaTotal / coincidencias;
                objetivo.establecerConclusion("CUMPLIDO");
                conclusiones.add("Objetivo '" + objetivo.getNombre() + "' CUMPLIDO (certeza: " +
                               String.format("%.2f", certezaPromedio) + ")");

                explicacionDetallada.append("✓ Objetivo '").append(objetivo.getNombre())
                                   .append("' CUMPLIDO con certeza ").append(String.format("%.2f", certezaPromedio))
                                   .append("\n");
            } else {
                objetivo.establecerConclusion("NO CUMPLIDO");
                explicacionDetallada.append("✗ Objetivo '").append(objetivo.getNombre())
                                   .append("' NO CUMPLIDO\n");
            }
        }
        explicacionDetallada.append("\n");
    }

    private void generarResumenFinal() {
        explicacionDetallada.append("=== RESUMEN FINAL ===\n");
        explicacionDetallada.append("Total de pasos ejecutados: ").append(contadorPasos).append("\n");
        explicacionDetallada.append("Reglas procesadas: ").append(reglasProcesadas.size()).append("\n");
        explicacionDetallada.append("Conclusiones obtenidas: ").append(conclusiones.size()).append("\n\n");

        explicacionDetallada.append("--- CONCLUSIONES FINALES ---\n");
        if (conclusiones.isEmpty()) {
            explicacionDetallada.append("No se obtuvieron conclusiones con los datos proporcionados.\n");
        } else {
            for (int i = 0; i < conclusiones.size(); i++) {
                explicacionDetallada.append((i + 1)).append(". ").append(conclusiones.get(i)).append("\n");
            }
        }
    }

    @Override
    public void establecerEstrategia(EstrategiaInferencia estrategia) {
        this.estrategia = estrategia;
    }

    @Override
    public List<String> obtenerConclusiones() {
        return new ArrayList<>(conclusiones);
    }

    @Override
    public String obtenerExplicacion() {
        return explicacionDetallada.toString();
    }

    public List<PasoInferencia> obtenerPasosInferencia() {
        return new ArrayList<>(pasosInferencia);
    }

    public Map<String, Double> obtenerFactoresCerteza() {
        return new HashMap<>(factoresCerteza);
    }

    // Clase interna para representar un paso de inferencia
    public static class PasoInferencia {
        private int numero;
        private String regla;
        private List<String> condicionesEvaluadas;
        private List<Hecho> nuevosHechos;
        private double certeza;

        // Getters y setters
        public int getNumero() { return numero; }
        public void setNumero(int numero) { this.numero = numero; }

        public String getRegla() { return regla; }
        public void setRegla(String regla) { this.regla = regla; }

        public List<String> getCondicionesEvaluadas() { return condicionesEvaluadas; }
        public void setCondicionesEvaluadas(List<String> condicionesEvaluadas) { this.condicionesEvaluadas = condicionesEvaluadas; }

        public List<Hecho> getNuevosHechos() { return nuevosHechos; }
        public void setNuevosHechos(List<Hecho> nuevosHechos) { this.nuevosHechos = nuevosHechos; }

        public double getCerteza() { return certeza; }
        public void setCerteza(double certeza) { this.certeza = certeza; }
    }
}