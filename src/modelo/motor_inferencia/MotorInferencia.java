package modelo.motor_inferencia;

import modelo.entidades.*;
import java.util.List;

public interface MotorInferencia {
    List<String> ejecutar(List<Hecho> hechos, List<Regla> reglas, List<Objetivo> objetivos);
    void establecerEstrategia(EstrategiaInferencia estrategia);
    List<String> obtenerConclusiones();
    String obtenerExplicacion();
}