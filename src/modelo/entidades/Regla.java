package modelo.entidades;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Regla implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String nombre;
    private List<String> condiciones;
    private List<String> acciones;
    private double factorCerteza;
    private int prioridad;

    public Regla() {
        this.condiciones = new ArrayList<>();
        this.acciones = new ArrayList<>();
        this.factorCerteza = 1.0;
        this.prioridad = 1;
    }

    public Regla(String nombre) {
        this();
        this.nombre = nombre;
    }

    public boolean evaluarCondiciones(List<Hecho> hechos) {
        for (String condicion : condiciones) {
            boolean cumplida = false;
            for (Hecho hecho : hechos) {
                if (condicion.contains(hecho.getPremisaNombre()) &&
                    condicion.contains(hecho.getValor())) {
                    cumplida = true;
                    break;
                }
            }
            if (!cumplida) {
                return false;
            }
        }
        return true;
    }

    public List<String> ejecutarAcciones() {
        return new ArrayList<>(acciones);
    }

    public double calcularCerteza(List<Hecho> hechos) {
        double certezaTotal = factorCerteza;
        for (Hecho hecho : hechos) {
            certezaTotal *= hecho.getCerteza();
        }
        return Math.min(certezaTotal, 1.0);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<String> getCondiciones() {
        return condiciones;
    }

    public void setCondiciones(List<String> condiciones) {
        this.condiciones = condiciones;
    }

    public void agregarCondicion(String condicion) {
        this.condiciones.add(condicion);
    }

    public List<String> getAcciones() {
        return acciones;
    }

    public void setAcciones(List<String> acciones) {
        this.acciones = acciones;
    }

    public void agregarAccion(String accion) {
        this.acciones.add(accion);
    }

    public double getFactorCerteza() {
        return factorCerteza;
    }

    public void setFactorCerteza(double factorCerteza) {
        this.factorCerteza = factorCerteza;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }
}