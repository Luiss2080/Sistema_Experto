package modelo.motor_inferencia;

import modelo.entidades.*;
import java.util.*;

/**
 * Explicador de inferencias que genera explicaciones detalladas del razonamiento
 */
public class ExplicadorInferencia {
    
    private List<PasoExplicacion> pasos;
    private StringBuilder explicacionCompleta;
    private Map<String, String> justificaciones;
    
    public ExplicadorInferencia() {
        this.pasos = new ArrayList<>();
        this.explicacionCompleta = new StringBuilder();
        this.justificaciones = new HashMap<>();
    }
    
    public String generarExplicacion(List<Hecho> hechos, List<Regla> reglas, 
                                   List<String> conclusiones, String algoritmo) {
        explicacionCompleta.setLength(0);
        pasos.clear();
        justificaciones.clear();
        
        explicacionCompleta.append("=== EXPLICACIÓN DEL RAZONAMIENTO ===\n");
        explicacionCompleta.append("Algoritmo utilizado: ").append(algoritmo).append("\n");
        explicacionCompleta.append("Fecha: ").append(new Date()).append("\n\n");
        
        explicacionCompleta.append("--- DATOS INICIALES ---\n");
        explicacionCompleta.append("Hechos proporcionados por el usuario:\n");
        for (Hecho hecho : hechos) {
            if ("usuario".equals(hecho.getFuente())) {
                explicacionCompleta.append("• ").append(hecho.getPremisaNombre())
                                 .append(" = ").append(hecho.getValor())
                                 .append(" (certeza: ").append(String.format("%.2f", hecho.getCerteza()))
                                 .append(")\n");
            }
        }
        
        explicacionCompleta.append("\nReglas disponibles en el sistema:\n");
        for (int i = 0; i < reglas.size(); i++) {
            Regla regla = reglas.get(i);
            explicacionCompleta.append((i + 1)).append(". ").append(regla.getNombre()).append("\n");
            explicacionCompleta.append("   IF: ").append(String.join(" AND ", regla.getCondiciones())).append("\n");
            explicacionCompleta.append("   THEN: ").append(String.join(", ", regla.getAcciones())).append("\n");
            explicacionCompleta.append("   Prioridad: ").append(regla.getPrioridad())
                             .append(", Factor certeza: ").append(regla.getFactorCerteza()).append("\n\n");
        }
        
        return explicacionCompleta.toString();
    }
    
    public String trazarRazonamiento(List<MotorInferenciaAvanzado.PasoInferencia> pasosInferencia) {
        StringBuilder traza = new StringBuilder();
        
        traza.append("--- TRAZA DEL RAZONAMIENTO ---\n");
        
        if (pasosInferencia.isEmpty()) {
            traza.append("No se ejecutaron pasos de inferencia.\n");
            traza.append("Posibles causas:\n");
            traza.append("- Las condiciones de las reglas no se cumplen con los datos proporcionados\n");
            traza.append("- No hay reglas aplicables a los hechos dados\n");
            traza.append("- Los datos de entrada no son suficientes\n");
        } else {
            for (MotorInferenciaAvanzado.PasoInferencia paso : pasosInferencia) {
                traza.append("\nPASO ").append(paso.getNumero()).append(":\n");
                traza.append("Regla aplicada: ").append(paso.getRegla()).append("\n");
                
                traza.append("Condiciones evaluadas:\n");
                for (String condicion : paso.getCondicionesEvaluadas()) {
                    traza.append("  ✓ ").append(condicion).append("\n");
                }
                
                traza.append("Nuevos hechos derivados:\n");
                for (Hecho hecho : paso.getNuevosHechos()) {
                    traza.append("  → ").append(hecho.getPremisaNombre())
                         .append(" = ").append(hecho.getValor())
                         .append(" (certeza: ").append(String.format("%.2f", hecho.getCerteza()))
                         .append(")\n");
                }
                
                traza.append("Factor de certeza del paso: ").append(String.format("%.2f", paso.getCerteza())).append("\n");
            }
        }
        
        return traza.toString();
    }
    
    public String justificarConclusion(String conclusion, List<Regla> reglas, List<Hecho> hechos) {
        StringBuilder justificacion = new StringBuilder();
        
        justificacion.append("--- JUSTIFICACIÓN DE LA CONCLUSIÓN ---\n");
        justificacion.append("Conclusión: ").append(conclusion).append("\n\n");
        
        // Buscar la regla que generó esta conclusión
        Regla reglaAplicada = null;
        for (Regla regla : reglas) {
            for (String accion : regla.getAcciones()) {
                if (conclusion.contains(accion)) {
                    reglaAplicada = regla;
                    break;
                }
            }
            if (reglaAplicada != null) break;
        }
        
        if (reglaAplicada != null) {
            justificacion.append("Esta conclusión se derivó de la regla: ").append(reglaAplicada.getNombre()).append("\n");
            justificacion.append("Condiciones que se cumplieron:\n");
            
            for (String condicion : reglaAplicada.getCondiciones()) {
                boolean cumplida = evaluarCondicionJustificacion(condicion, hechos);
                justificacion.append("  ").append(cumplida ? "✓" : "✗").append(" ").append(condicion);
                
                if (cumplida) {
                    // Buscar el hecho que satisface esta condición
                    Hecho hechoSoporte = encontrarHechoSoporte(condicion, hechos);
                    if (hechoSoporte != null) {
                        justificacion.append(" [Respaldado por: ").append(hechoSoporte.getPremisaNombre())
                                   .append(" = ").append(hechoSoporte.getValor()).append("]");
                    }
                }
                justificacion.append("\n");
            }
            
            justificacion.append("\nPor lo tanto: ").append(String.join(", ", reglaAplicada.getAcciones())).append("\n");
            justificacion.append("Nivel de confianza: ").append(String.format("%.2f", reglaAplicada.getFactorCerteza())).append("\n");
        } else {
            justificacion.append("No se pudo identificar la regla específica que generó esta conclusión.\n");
        }
        
        return justificacion.toString();
    }
    
    private boolean evaluarCondicionJustificacion(String condicion, List<Hecho> hechos) {
        condicion = condicion.trim();

        // Usar la misma lógica de evaluación de condiciones mejorada
        if (condicion.contains(" = ")) {
            String[] partes = condicion.split(" = ");
            if (partes.length == 2) {
                String premisa = partes[0].trim();
                String valorEsperado = partes[1].trim();
                return verificarIgualdadJustificacion(premisa, valorEsperado, hechos);
            }
        } else if (condicion.contains(" > ")) {
            String[] partes = condicion.split(" > ");
            if (partes.length == 2) {
                String premisa = partes[0].trim();
                String valorEsperado = partes[1].trim();
                return verificarMayorQueJustificacion(premisa, valorEsperado, hechos);
            }
        } else if (condicion.contains(" < ")) {
            String[] partes = condicion.split(" < ");
            if (partes.length == 2) {
                String premisa = partes[0].trim();
                String valorEsperado = partes[1].trim();
                return verificarMenorQueJustificacion(premisa, valorEsperado, hechos);
            }
        } else if (condicion.contains(" >= ")) {
            String[] partes = condicion.split(" >= ");
            if (partes.length == 2) {
                String premisa = partes[0].trim();
                String valorEsperado = partes[1].trim();
                return verificarMayorIgualQueJustificacion(premisa, valorEsperado, hechos);
            }
        } else if (condicion.contains(" <= ")) {
            String[] partes = condicion.split(" <= ");
            if (partes.length == 2) {
                String premisa = partes[0].trim();
                String valorEsperado = partes[1].trim();
                return verificarMenorIgualQueJustificacion(premisa, valorEsperado, hechos);
            }
        }

        return false;
    }

    private boolean verificarIgualdadJustificacion(String premisa, String valorEsperado, List<Hecho> hechos) {
        for (Hecho hecho : hechos) {
            if (hecho.getPremisaNombre().equals(premisa)) {
                String valorHecho = hecho.getValor().toLowerCase().trim();
                String valorEsp = valorEsperado.toLowerCase().trim();

                // Manejo especial para valores booleanos
                if (valorEsp.equals("true") || valorEsp.equals("false")) {
                    return normalizarBooleano(valorHecho).equals(valorEsp);
                }

                return valorHecho.equals(valorEsp);
            }
        }
        return false;
    }

    private boolean verificarMayorQueJustificacion(String premisa, String valorEsperado, List<Hecho> hechos) {
        return compararNumericamenteJustificacion(premisa, valorEsperado, hechos, ">");
    }

    private boolean verificarMenorQueJustificacion(String premisa, String valorEsperado, List<Hecho> hechos) {
        return compararNumericamenteJustificacion(premisa, valorEsperado, hechos, "<");
    }

    private boolean verificarMayorIgualQueJustificacion(String premisa, String valorEsperado, List<Hecho> hechos) {
        return compararNumericamenteJustificacion(premisa, valorEsperado, hechos, ">=");
    }

    private boolean verificarMenorIgualQueJustificacion(String premisa, String valorEsperado, List<Hecho> hechos) {
        return compararNumericamenteJustificacion(premisa, valorEsperado, hechos, "<=");
    }

    private boolean compararNumericamenteJustificacion(String premisa, String valorEsperado, List<Hecho> hechos, String operador) {
        try {
            double valorNumericoEsperado = Double.parseDouble(valorEsperado);

            for (Hecho hecho : hechos) {
                if (hecho.getPremisaNombre().equals(premisa)) {
                    try {
                        double valorNumericoHecho = Double.parseDouble(hecho.getValor());

                        switch (operador) {
                            case ">":
                                return valorNumericoHecho > valorNumericoEsperado;
                            case "<":
                                return valorNumericoHecho < valorNumericoEsperado;
                            case ">=":
                                return valorNumericoHecho >= valorNumericoEsperado;
                            case "<=":
                                return valorNumericoHecho <= valorNumericoEsperado;
                        }
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return false;
    }

    private String normalizarBooleano(String valor) {
        valor = valor.toLowerCase().trim();
        if (valor.equals("sí") || valor.equals("si") || valor.equals("s") || valor.equals("1")) {
            return "true";
        } else if (valor.equals("no") || valor.equals("n") || valor.equals("0")) {
            return "false";
        }
        return valor;
    }
    
    private Hecho encontrarHechoSoporte(String condicion, List<Hecho> hechos) {
        condicion = condicion.trim();

        if (condicion.contains(" = ")) {
            String[] partes = condicion.split(" = ");
            if (partes.length == 2) {
                String premisa = partes[0].trim();
                String valor = partes[1].trim();

                for (Hecho hecho : hechos) {
                    if (hecho.getPremisaNombre().equals(premisa)) {
                        String valorHecho = hecho.getValor().toLowerCase().trim();
                        String valorBuscado = valor.toLowerCase().trim();

                        // Manejo especial para valores booleanos
                        if (valorBuscado.equals("true") || valorBuscado.equals("false")) {
                            if (normalizarBooleano(valorHecho).equals(valorBuscado)) {
                                return hecho;
                            }
                        } else if (valorHecho.equals(valorBuscado)) {
                            return hecho;
                        }
                    }
                }
            }
        } else if (condicion.contains(" > ") || condicion.contains(" < ") ||
                   condicion.contains(" >= ") || condicion.contains(" <= ")) {
            String operador;
            if (condicion.contains(" >= ")) {
                operador = " >= ";
            } else if (condicion.contains(" <= ")) {
                operador = " <= ";
            } else if (condicion.contains(" > ")) {
                operador = " > ";
            } else {
                operador = " < ";
            }

            String[] partes = condicion.split(operador);
            if (partes.length == 2) {
                String premisa = partes[0].trim();

                for (Hecho hecho : hechos) {
                    if (hecho.getPremisaNombre().equals(premisa)) {
                        return hecho;
                    }
                }
            }
        }

        return null;
    }
    
    public String generarResumenEjecutivo(List<String> conclusiones, int totalPasos, int reglasAplicadas) {
        StringBuilder resumen = new StringBuilder();
        
        resumen.append("=== RESUMEN EJECUTIVO ===\n");
        resumen.append("Total de conclusiones obtenidas: ").append(conclusiones.size()).append("\n");
        resumen.append("Pasos de inferencia ejecutados: ").append(totalPasos).append("\n");
        resumen.append("Reglas aplicadas: ").append(reglasAplicadas).append("\n\n");
        
        if (!conclusiones.isEmpty()) {
            resumen.append("CONCLUSIONES PRINCIPALES:\n");
            for (int i = 0; i < Math.min(conclusiones.size(), 5); i++) {
                resumen.append((i + 1)).append(". ").append(conclusiones.get(i)).append("\n");
            }
            
            if (conclusiones.size() > 5) {
                resumen.append("... y ").append(conclusiones.size() - 5).append(" conclusiones adicionales\n");
            }
        } else {
            resumen.append("NO SE OBTUVIERON CONCLUSIONES\n");
            resumen.append("Recomendaciones:\n");
            resumen.append("- Verificar que los datos de entrada sean correctos\n");
            resumen.append("- Asegurar que las reglas estén bien definidas\n");
            resumen.append("- Comprobar que existan reglas aplicables a los datos proporcionados\n");
        }
        
        return resumen.toString();
    }
    
    // Clase interna para representar un paso de explicación
    public static class PasoExplicacion {
        private int numero;
        private String descripcion;
        private String justificacion;
        private double nivelConfianza;
        
        public PasoExplicacion(int numero, String descripcion, String justificacion, double nivelConfianza) {
            this.numero = numero;
            this.descripcion = descripcion;
            this.justificacion = justificacion;
            this.nivelConfianza = nivelConfianza;
        }
        
        // Getters y setters
        public int getNumero() { return numero; }
        public void setNumero(int numero) { this.numero = numero; }
        
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
        
        public String getJustificacion() { return justificacion; }
        public void setJustificacion(String justificacion) { this.justificacion = justificacion; }
        
        public double getNivelConfianza() { return nivelConfianza; }
        public void setNivelConfianza(double nivelConfianza) { this.nivelConfianza = nivelConfianza; }
    }
}