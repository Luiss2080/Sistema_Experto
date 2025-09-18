package modelo.entidades;

import java.io.Serializable;

public class Objetivo implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String nombre;
    private String descripcion;
    private String tipoRespuesta;
    private String explicacion;
    private String criterio;
    private String conclusion;

    public Objetivo() {}

    public Objetivo(String nombre, String descripcion, String tipoRespuesta) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipoRespuesta = tipoRespuesta;
    }

    public void definirCriterios(String criterio) {
        this.criterio = criterio;
    }

    public void establecerConclusion(String conclusion) {
        this.conclusion = conclusion;
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

    public String getTipoRespuesta() {
        return tipoRespuesta;
    }

    public void setTipoRespuesta(String tipoRespuesta) {
        this.tipoRespuesta = tipoRespuesta;
    }

    public String getExplicacion() {
        return explicacion;
    }

    public void setExplicacion(String explicacion) {
        this.explicacion = explicacion;
    }

    public String getCriterio() {
        return criterio;
    }

    public void setCriterio(String criterio) {
        this.criterio = criterio;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }
}