package modelo.motor_inferencia;

import modelo.entidades.*;
import java.util.*;

public class BackwardChaining implements MotorInferencia {
    private EstrategiaInferencia estrategia;
    private List<String> conclusiones;
    private StringBuilder explicacion;

    public BackwardChaining() {
        this.estrategia = EstrategiaInferencia.PROFUNDIDAD_PRIMERO;
        this.conclusiones = new ArrayList<>();
        this.explicacion = new StringBuilder();
    }

    @Override
    public List<String> ejecutar(List<Hecho> hechos, List<Regla> reglas, List<Objetivo> objetivos) {
        conclusiones.clear();
        explicacion.setLength(0);
        explicacion.append("Iniciando encadenamiento hacia atrás...\n");

        for (Objetivo objetivo : objetivos) {
            explicacion.append("Intentando demostrar objetivo: ")
                      .append(objetivo.getNombre()).append("\n");

            if (demostrarObjetivo(objetivo, hechos, reglas, new HashSet<>())) {
                conclusiones.add("Objetivo cumplido: " + objetivo.getNombre());
                objetivo.establecerConclusion("VERDADERO");
            } else {
                objetivo.establecerConclusion("FALSO");
            }
        }

        return new ArrayList<>(conclusiones);
    }

    private boolean demostrarObjetivo(Objetivo objetivo, List<Hecho> hechos,
                                    List<Regla> reglas, Set<String> visitados) {
        String objetivoKey = objetivo.getNombre();
        if (visitados.contains(objetivoKey)) {
            return false;
        }
        visitados.add(objetivoKey);

        if (estaEnBaseHechos(objetivo, hechos)) {
            explicacion.append("Objetivo encontrado en base de hechos: ")
                      .append(objetivo.getNombre()).append("\n");
            return true;
        }

        List<Regla> reglasQueGeneran = buscarReglasQueGeneran(objetivo, reglas);

        for (Regla regla : reglasQueGeneran) {
            explicacion.append("Evaluando regla: ").append(regla.getNombre()).append("\n");

            if (demostrarCondiciones(regla.getCondiciones(), hechos, reglas,
                                   new HashSet<>(visitados))) {
                explicacion.append("Condiciones demostradas para regla: ")
                          .append(regla.getNombre()).append("\n");
                return true;
            }
        }

        explicacion.append("No se pudo demostrar objetivo: ")
                   .append(objetivo.getNombre()).append("\n");
        return false;
    }

    private boolean estaEnBaseHechos(Objetivo objetivo, List<Hecho> hechos) {
        return hechos.stream().anyMatch(h ->
            h.getPremisaNombre().equals(objetivo.getNombre()) ||
            h.getValor().contains(objetivo.getNombre()));
    }

    private List<Regla> buscarReglasQueGeneran(Objetivo objetivo, List<Regla> reglas) {
        List<Regla> reglasQueGeneran = new ArrayList<>();

        for (Regla regla : reglas) {
            for (String accion : regla.getAcciones()) {
                if (accion.contains(objetivo.getNombre())) {
                    reglasQueGeneran.add(regla);
                    break;
                }
            }
        }

        return reglasQueGeneran;
    }

    private boolean demostrarCondiciones(List<String> condiciones, List<Hecho> hechos,
                                       List<Regla> reglas, Set<String> visitados) {
        for (String condicion : condiciones) {
            if (!demostrarCondicion(condicion, hechos, reglas, visitados)) {
                return false;
            }
        }
        return true;
    }

    private boolean demostrarCondicion(String condicion, List<Hecho> hechos,
                                     List<Regla> reglas, Set<String> visitados) {
        for (Hecho hecho : hechos) {
            if (condicion.contains(hecho.getPremisaNombre()) &&
                condicion.contains(hecho.getValor())) {
                return true;
            }
        }

        Objetivo subObjetivo = new Objetivo();
        subObjetivo.setNombre(extraerObjetivoDeCondicion(condicion));

        return demostrarObjetivo(subObjetivo, hechos, reglas, visitados);
    }

    private String extraerObjetivoDeCondicion(String condicion) {
        String[] partes = condicion.split("=");
        return partes.length > 0 ? partes[0].trim() : condicion;
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
        return explicacion.toString();
    }
}