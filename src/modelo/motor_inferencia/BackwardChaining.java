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
        // Usar la misma lógica de evaluación que las reglas
        if (evaluarCondicionDirecta(condicion, hechos)) {
            return true;
        }

        // Si no se puede evaluar directamente, intentar demostrar como subobjetivo
        String premisaObjetivo = extraerPremisaDeCondicion(condicion);
        if (premisaObjetivo != null && !visitados.contains(premisaObjetivo)) {
            Objetivo subObjetivo = new Objetivo();
            subObjetivo.setNombre(premisaObjetivo);
            return demostrarObjetivo(subObjetivo, hechos, reglas, visitados);
        }

        return false;
    }

    private boolean evaluarCondicionDirecta(String condicion, List<Hecho> hechos) {
        condicion = condicion.trim();

        // Usar la misma lógica de evaluación de condiciones que en Regla
        if (condicion.contains(" = ")) {
            String[] partes = condicion.split(" = ");
            if (partes.length == 2) {
                String premisa = partes[0].trim();
                String valorEsperado = partes[1].trim();
                return verificarIgualdad(premisa, valorEsperado, hechos);
            }
        } else if (condicion.contains(" > ")) {
            String[] partes = condicion.split(" > ");
            if (partes.length == 2) {
                String premisa = partes[0].trim();
                String valorEsperado = partes[1].trim();
                return verificarMayorQue(premisa, valorEsperado, hechos);
            }
        } else if (condicion.contains(" < ")) {
            String[] partes = condicion.split(" < ");
            if (partes.length == 2) {
                String premisa = partes[0].trim();
                String valorEsperado = partes[1].trim();
                return verificarMenorQue(premisa, valorEsperado, hechos);
            }
        } else if (condicion.contains(" >= ")) {
            String[] partes = condicion.split(" >= ");
            if (partes.length == 2) {
                String premisa = partes[0].trim();
                String valorEsperado = partes[1].trim();
                return verificarMayorIgualQue(premisa, valorEsperado, hechos);
            }
        } else if (condicion.contains(" <= ")) {
            String[] partes = condicion.split(" <= ");
            if (partes.length == 2) {
                String premisa = partes[0].trim();
                String valorEsperado = partes[1].trim();
                return verificarMenorIgualQue(premisa, valorEsperado, hechos);
            }
        }

        return false;
    }

    private boolean verificarIgualdad(String premisa, String valorEsperado, List<Hecho> hechos) {
        for (Hecho hecho : hechos) {
            if (hecho.getPremisaNombre().equals(premisa)) {
                String valorHecho = hecho.getValor().toLowerCase().trim();
                String valorEsp = valorEsperado.toLowerCase().trim();

                if (valorEsp.equals("true") || valorEsp.equals("false")) {
                    return normalizarBooleano(valorHecho).equals(valorEsp);
                }

                return valorHecho.equals(valorEsp);
            }
        }
        return false;
    }

    private boolean verificarMayorQue(String premisa, String valorEsperado, List<Hecho> hechos) {
        return compararNumericamente(premisa, valorEsperado, hechos, ">");
    }

    private boolean verificarMenorQue(String premisa, String valorEsperado, List<Hecho> hechos) {
        return compararNumericamente(premisa, valorEsperado, hechos, "<");
    }

    private boolean verificarMayorIgualQue(String premisa, String valorEsperado, List<Hecho> hechos) {
        return compararNumericamente(premisa, valorEsperado, hechos, ">=");
    }

    private boolean verificarMenorIgualQue(String premisa, String valorEsperado, List<Hecho> hechos) {
        return compararNumericamente(premisa, valorEsperado, hechos, "<=");
    }

    private boolean compararNumericamente(String premisa, String valorEsperado, List<Hecho> hechos, String operador) {
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

    private String extraerPremisaDeCondicion(String condicion) {
        condicion = condicion.trim();

        if (condicion.contains(" = ")) {
            return condicion.split(" = ")[0].trim();
        } else if (condicion.contains(" > ")) {
            return condicion.split(" > ")[0].trim();
        } else if (condicion.contains(" < ")) {
            return condicion.split(" < ")[0].trim();
        } else if (condicion.contains(" >= ")) {
            return condicion.split(" >= ")[0].trim();
        } else if (condicion.contains(" <= ")) {
            return condicion.split(" <= ")[0].trim();
        }

        return null;
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