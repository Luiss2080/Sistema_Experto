package modelo.motor_inferencia;

import modelo.entidades.*;
import java.util.*;

public class ForwardChaining implements MotorInferencia {
    private EstrategiaInferencia estrategia;
    private List<String> conclusiones;
    private StringBuilder explicacion;

    public ForwardChaining() {
        this.estrategia = EstrategiaInferencia.AMPLITUD_PRIMERO;
        this.conclusiones = new ArrayList<>();
        this.explicacion = new StringBuilder();
    }

    @Override
    public List<String> ejecutar(List<Hecho> hechos, List<Regla> reglas, List<Objetivo> objetivos) {
        conclusiones.clear();
        explicacion.setLength(0);
        explicacion.append("Iniciando encadenamiento hacia adelante...\n");

        List<Hecho> baseDatos = new ArrayList<>(hechos);
        boolean nuevosHechos = true;

        while (nuevosHechos) {
            nuevosHechos = false;
            List<Regla> reglasOrdenadas = ordenarReglas(reglas);

            for (Regla regla : reglasOrdenadas) {
                if (regla.evaluarCondiciones(baseDatos)) {
                    List<String> acciones = regla.ejecutarAcciones();

                    for (String accion : acciones) {
                        if (!yaExiste(accion, baseDatos)) {
                            Hecho nuevoHecho = crearHechoDesdeAccion(accion, regla, baseDatos);
                            baseDatos.add(nuevoHecho);
                            conclusiones.add(accion);
                            nuevosHechos = true;

                            explicacion.append("Regla aplicada: ").append(regla.getNombre())
                                     .append(" -> ").append(accion).append("\n");
                        }
                    }
                }
            }
        }

        explicacion.append("Proceso completado. Conclusiones encontradas: ")
                   .append(conclusiones.size()).append("\n");
        return new ArrayList<>(conclusiones);
    }

    private List<Regla> ordenarReglas(List<Regla> reglas) {
        List<Regla> ordenadas = new ArrayList<>(reglas);

        switch (estrategia) {
            case PRIORIDAD_REGLAS:
                ordenadas.sort((r1, r2) -> Integer.compare(r2.getPrioridad(), r1.getPrioridad()));
                break;
            case FACTOR_CERTEZA:
                ordenadas.sort((r1, r2) -> Double.compare(r2.getFactorCerteza(), r1.getFactorCerteza()));
                break;
            default:
                break;
        }

        return ordenadas;
    }

    private boolean yaExiste(String accion, List<Hecho> hechos) {
        return hechos.stream().anyMatch(h -> h.getValor().equals(accion));
    }

    private Hecho crearHechoDesdeAccion(String accion, Regla regla, List<Hecho> hechos) {
        Hecho hecho = new Hecho();
        hecho.setPremisaNombre("inferido");
        hecho.setValor(accion);
        hecho.setCerteza(regla.calcularCerteza(hechos));
        hecho.setFuente("inferencia");
        return hecho;
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