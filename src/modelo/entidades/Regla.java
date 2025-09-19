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
        if (condiciones.isEmpty()) {
            return false;
        }

        for (String condicion : condiciones) {
            if (!evaluarCondicionIndividual(condicion, hechos)) {
                return false;
            }
        }
        return true;
    }

    private boolean evaluarCondicionIndividual(String condicion, List<Hecho> hechos) {
        condicion = condicion.trim();

        // Buscar operadores de comparación
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

                // Manejo especial para valores booleanos
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
                        // El valor del hecho no es numérico
                        return false;
                    }
                }
            }
        } catch (NumberFormatException e) {
            // El valor esperado no es numérico
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