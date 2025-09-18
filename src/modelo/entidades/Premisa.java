package modelo.entidades;

import java.io.Serializable;

public class Premisa implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String nombre;
    private String descripcion;
    private TipoDato tipo;
    private String valorDefecto;

    public enum TipoDato {
        BOOLEANA, NUMERICA, STRING
    }

    public Premisa() {}

    public Premisa(String nombre, String descripcion, TipoDato tipo) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
    }

    public boolean validarTipo(String valor) {
        try {
            switch (tipo) {
                case BOOLEANA:
                    return valor.equalsIgnoreCase("true") || valor.equalsIgnoreCase("false");
                case NUMERICA:
                    Double.parseDouble(valor);
                    return true;
                case STRING:
                    return valor != null && !valor.trim().isEmpty();
                default:
                    return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void establecerValor(String valor) {
        if (validarTipo(valor)) {
            this.valorDefecto = valor;
        } else {
            throw new IllegalArgumentException("Valor no válido para el tipo " + tipo);
        }
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public TipoDato getTipo() {
        return tipo;
    }

    public void setTipo(TipoDato tipo) {
        this.tipo = tipo;
    }

    public String getValorDefecto() {
        return valorDefecto;
    }

    public void setValorDefecto(String valorDefecto) {
        this.valorDefecto = valorDefecto;
    }
}