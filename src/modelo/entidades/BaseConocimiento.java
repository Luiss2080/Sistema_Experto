package modelo.entidades;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BaseConocimiento implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String nombre;
    private String descripcion;
    private LocalDateTime fechaCreacion;
    private int usuarioCreador;
    private List<Regla> reglas;
    private List<Premisa> premisas;
    private List<Objetivo> objetivos;

    public BaseConocimiento() {
        this.fechaCreacion = LocalDateTime.now();
        this.reglas = new ArrayList<>();
        this.premisas = new ArrayList<>();
        this.objetivos = new ArrayList<>();
    }

    public BaseConocimiento(String nombre, String descripcion, int usuarioCreador) {
        this();
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.usuarioCreador = usuarioCreador;
    }

    public void agregarRegla(Regla regla) {
        this.reglas.add(regla);
    }

    public void eliminarRegla(Regla regla) {
        this.reglas.remove(regla);
    }

    public boolean validarConsistencia() {
        for (int i = 0; i < reglas.size(); i++) {
            for (int j = i + 1; j < reglas.size(); j++) {
                Regla regla1 = reglas.get(i);
                Regla regla2 = reglas.get(j);

                if (regla1.getCondiciones().equals(regla2.getCondiciones()) &&
                    !regla1.getAcciones().equals(regla2.getAcciones())) {
                    return false;
                }
            }
        }
        return true;
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public int getUsuarioCreador() {
        return usuarioCreador;
    }

    public void setUsuarioCreador(int usuarioCreador) {
        this.usuarioCreador = usuarioCreador;
    }

    public List<Regla> getReglas() {
        return reglas;
    }

    public void setReglas(List<Regla> reglas) {
        this.reglas = reglas;
    }

    public List<Premisa> getPremisas() {
        return premisas;
    }

    public void setPremisas(List<Premisa> premisas) {
        this.premisas = premisas;
    }

    public List<Objetivo> getObjetivos() {
        return objetivos;
    }

    public void setObjetivos(List<Objetivo> objetivos) {
        this.objetivos = objetivos;
    }
}