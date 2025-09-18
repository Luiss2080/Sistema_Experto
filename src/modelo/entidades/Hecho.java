package modelo.entidades;

import java.io.Serializable;

public class Hecho implements Serializable {
    private static final long serialVersionUID = 1L;
    private int premisaId;
    private String premisaNombre;
    private String valor;
    private double certeza;
    private String fuente;

    public Hecho() {
        this.certeza = 1.0;
        this.fuente = "usuario";
    }

    public Hecho(int premisaId, String premisaNombre, String valor) {
        this();
        this.premisaId = premisaId;
        this.premisaNombre = premisaNombre;
        this.valor = valor;
    }

    public void establecerValor(String valor) {
        this.valor = valor;
    }

    public void actualizarCerteza(double nuevaCerteza) {
        this.certeza = Math.max(0.0, Math.min(1.0, nuevaCerteza));
    }

    public int getPremisaId() {
        return premisaId;
    }

    public void setPremisaId(int premisaId) {
        this.premisaId = premisaId;
    }

    public String getPremisaNombre() {
        return premisaNombre;
    }

    public void setPremisaNombre(String premisaNombre) {
        this.premisaNombre = premisaNombre;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public double getCerteza() {
        return certeza;
    }

    public void setCerteza(double certeza) {
        this.certeza = certeza;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    @Override
    public String toString() {
        return premisaNombre + " = " + valor + " (certeza: " + certeza + ")";
    }
}