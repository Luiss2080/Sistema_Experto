package modelo.reglas;

import modelo.entidades.*;
import java.util.List;

public class ReglasInferencia {

    public static boolean aplicarModusPonens(Regla regla, List<Hecho> hechos) {
        if (regla.getCondiciones().size() != 1) {
            return false;
        }

        String condicion = regla.getCondiciones().get(0);

        for (Hecho hecho : hechos) {
            if (condicion.contains(hecho.getPremisaNombre()) &&
                condicion.contains(hecho.getValor())) {
                return true;
            }
        }
        return false;
    }

    public static boolean aplicarModusTollens(Regla regla, List<Hecho> hechos) {
        if (regla.getAcciones().size() != 1) {
            return false;
        }

        String accion = regla.getAcciones().get(0);
        boolean accionNegada = false;

        for (Hecho hecho : hechos) {
            if (hecho.getValor().contains("NO " + accion) ||
                hecho.getValor().contains("false")) {
                accionNegada = true;
                break;
            }
        }

        return accionNegada;
    }

    public static boolean aplicarSilogismoHipotetico(Regla regla1, Regla regla2) {
        for (String accion1 : regla1.getAcciones()) {
            for (String condicion2 : regla2.getCondiciones()) {
                if (accion1.equals(condicion2)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean aplicarResolucion(Regla regla1, Regla regla2) {
        for (String condicion1 : regla1.getCondiciones()) {
            for (String condicion2 : regla2.getCondiciones()) {
                if (sonComplementarias(condicion1, condicion2)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean sonComplementarias(String condicion1, String condicion2) {
        if (condicion1.startsWith("NO ") && !condicion2.startsWith("NO ")) {
            return condicion1.substring(3).equals(condicion2);
        }
        if (condicion2.startsWith("NO ") && !condicion1.startsWith("NO ")) {
            return condicion2.substring(3).equals(condicion1);
        }
        return false;
    }
}