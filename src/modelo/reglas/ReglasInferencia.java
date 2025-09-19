package modelo.reglas;

import modelo.entidades.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Implementación de reglas de inferencia clásicas para sistemas expertos
 * Incluye Modus Ponens, Modus Tollens, Silogismo Hipotético y Resolución
 */
public class ReglasInferencia {

    /**
     * Aplica Modus Ponens: Si P entonces Q, P es verdadero, por lo tanto Q es verdadero
     */
    public static boolean aplicarModusPonens(Regla regla, List<Hecho> hechos) {
        // Usar el método de evaluación de condiciones mejorado de Regla
        return regla.evaluarCondiciones(hechos);
    }

    /**
     * Aplica Modus Tollens: Si P entonces Q, Q es falso, por lo tanto P es falso
     */
    public static boolean aplicarModusTollens(Regla regla, List<Hecho> hechos) {
        // Verificar si las conclusiones de la regla son falsas
        for (String accion : regla.getAcciones()) {
            if (esAccionNegada(accion, hechos)) {
                // Si la conclusión es falsa, podemos inferir que las premisas son falsas
                return true;
            }
        }
        return false;
    }

    /**
     * Aplica Silogismo Hipotético: Si P entonces Q, Si Q entonces R, por lo tanto Si P entonces R
     */
    public static boolean aplicarSilogismoHipotetico(Regla regla1, Regla regla2, List<Hecho> hechos) {
        // Verificar si alguna acción de la primera regla coincide con alguna condición de la segunda
        for (String accion1 : regla1.getAcciones()) {
            for (String condicion2 : regla2.getCondiciones()) {
                if (accionCoincideConCondicion(accion1, condicion2)) {
                    // Si regla1 se puede aplicar, entonces regla2 también podría aplicarse
                    if (regla1.evaluarCondiciones(hechos)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Aplica el principio de Resolución para detección de contradicciones
     */
    public static boolean aplicarResolucion(Regla regla1, Regla regla2, List<Hecho> hechos) {
        // Buscar condiciones complementarias entre las dos reglas
        for (String condicion1 : regla1.getCondiciones()) {
            for (String condicion2 : regla2.getCondiciones()) {
                if (sonCondicionesComplementarias(condicion1, condicion2)) {
                    // Si encontramos condiciones complementarias, verificar si podemos resolverlas
                    return resolverCondicionesComplementarias(regla1, regla2, condicion1, condicion2, hechos);
                }
            }

            // También verificar si las condiciones de una regla contradicen las acciones de otra
            for (String accion2 : regla2.getAcciones()) {
                if (condicionContraiceAccion(condicion1, accion2)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Aplica encadenamiento hacia adelante con múltiples reglas
     */
    public static List<Hecho> aplicarEncadenamientoAdelante(List<Regla> reglas, List<Hecho> hechosIniciales) {
        List<Hecho> hechos = new ArrayList<>(hechosIniciales);
        boolean nuevoHecho = true;

        while (nuevoHecho) {
            nuevoHecho = false;

            for (Regla regla : reglas) {
                if (aplicarModusPonens(regla, hechos)) {
                    // Generar nuevos hechos a partir de las acciones de la regla
                    List<Hecho> nuevosHechos = generarHechosDesdeAcciones(regla, hechos);
                    for (Hecho nuevoHechoGenerado : nuevosHechos) {
                        if (!existeHecho(nuevoHechoGenerado, hechos)) {
                            hechos.add(nuevoHechoGenerado);
                            nuevoHecho = true;
                        }
                    }
                }
            }
        }

        return hechos;
    }

    private static boolean esAccionNegada(String accion, List<Hecho> hechos) {
        // Extraer el nombre de la premisa de la acción (formato: "premisa = valor")
        if (accion.contains(" = ")) {
            String[] partes = accion.split(" = ");
            if (partes.length == 2) {
                String premisa = partes[0].trim();
                String valorEsperado = partes[1].trim();

                for (Hecho hecho : hechos) {
                    if (hecho.getPremisaNombre().equals(premisa)) {
                        String valorHecho = hecho.getValor().toLowerCase().trim();
                        String valorEsp = valorEsperado.toLowerCase().trim();

                        // Verificar si el valor es opuesto al esperado
                        if (valorEsp.equals("true") && valorHecho.equals("false")) {
                            return true;
                        } else if (valorEsp.equals("false") && valorHecho.equals("true")) {
                            return true;
                        } else if (!valorHecho.equals(valorEsp)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private static boolean accionCoincideConCondicion(String accion, String condicion) {
        // Normalizar formato de acción y condición para comparación
        accion = accion.trim();
        condicion = condicion.trim();

        // Si ambas tienen el mismo formato de premisa = valor, comparar
        if (accion.contains(" = ") && condicion.contains(" = ")) {
            return accion.equals(condicion);
        }

        return false;
    }

    private static boolean sonCondicionesComplementarias(String condicion1, String condicion2) {
        condicion1 = condicion1.trim();
        condicion2 = condicion2.trim();

        // Verificar si una condición es la negación de la otra
        if (condicion1.contains(" = ") && condicion2.contains(" = ")) {
            String[] partes1 = condicion1.split(" = ");
            String[] partes2 = condicion2.split(" = ");

            if (partes1.length == 2 && partes2.length == 2) {
                String premisa1 = partes1[0].trim();
                String valor1 = partes1[1].trim();
                String premisa2 = partes2[0].trim();
                String valor2 = partes2[1].trim();

                // Misma premisa pero valores opuestos
                if (premisa1.equals(premisa2)) {
                    if ((valor1.equals("true") && valor2.equals("false")) ||
                        (valor1.equals("false") && valor2.equals("true"))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static boolean resolverCondicionesComplementarias(Regla regla1, Regla regla2,
                                                            String condicion1, String condicion2,
                                                            List<Hecho> hechos) {
        // Si encontramos condiciones complementarias y ambas reglas pueden aplicarse,
        // hay una contradicción potencial
        List<Hecho> hechosTemporal1 = new ArrayList<>(hechos);
        List<Hecho> hechosTemporal2 = new ArrayList<>(hechos);

        // Simular la aplicación de cada regla
        boolean regla1Aplicable = regla1.evaluarCondiciones(hechosTemporal1);
        boolean regla2Aplicable = regla2.evaluarCondiciones(hechosTemporal2);

        return regla1Aplicable && regla2Aplicable;
    }

    private static boolean condicionContraiceAccion(String condicion, String accion) {
        // Verificar si una condición contradice directamente una acción
        condicion = condicion.trim();
        accion = accion.trim();

        if (condicion.contains(" = ") && accion.contains(" = ")) {
            String[] partesCondicion = condicion.split(" = ");
            String[] partesAccion = accion.split(" = ");

            if (partesCondicion.length == 2 && partesAccion.length == 2) {
                String premisaCondicion = partesCondicion[0].trim();
                String valorCondicion = partesCondicion[1].trim();
                String premisaAccion = partesAccion[0].trim();
                String valorAccion = partesAccion[1].trim();

                // Misma premisa pero valores opuestos
                if (premisaCondicion.equals(premisaAccion)) {
                    if ((valorCondicion.equals("true") && valorAccion.equals("false")) ||
                        (valorCondicion.equals("false") && valorAccion.equals("true"))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static List<Hecho> generarHechosDesdeAcciones(Regla regla, List<Hecho> hechosExistentes) {
        List<Hecho> nuevosHechos = new ArrayList<>();

        for (String accion : regla.getAcciones()) {
            if (accion.contains(" = ")) {
                String[] partes = accion.split(" = ");
                if (partes.length == 2) {
                    String premisa = partes[0].trim();
                    String valor = partes[1].trim();

                    Hecho nuevoHecho = new Hecho();
                    nuevoHecho.setPremisaNombre(premisa);
                    nuevoHecho.setValor(valor);
                    nuevoHecho.setCerteza(regla.calcularCerteza(hechosExistentes));
                    nuevoHecho.setFuente("inferencia");

                    nuevosHechos.add(nuevoHecho);
                }
            }
        }

        return nuevosHechos;
    }

    private static boolean existeHecho(Hecho nuevoHecho, List<Hecho> hechos) {
        for (Hecho hecho : hechos) {
            if (hecho.getPremisaNombre().equals(nuevoHecho.getPremisaNombre()) &&
                hecho.getValor().equals(nuevoHecho.getValor())) {
                return true;
            }
        }
        return false;
    }
}