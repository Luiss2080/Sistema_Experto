package modelo.entidades;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Sesion implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private int usuarioId;
    private int baseConocimientoId;
    private List<Hecho> hechos;
    private List<String> conclusiones;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    public Sesion() {
        this.fechaInicio = LocalDateTime.now();
        this.hechos = new ArrayList<>();
        this.conclusiones = new ArrayList<>();
    }

    public Sesion(int usuarioId, int baseConocimientoId) {
        this();
        this.usuarioId = usuarioId;
        this.baseConocimientoId = baseConocimientoId;
    }

    public void iniciarSesion() {
        this.fechaInicio = LocalDateTime.now();
        this.hechos.clear();
        this.conclusiones.clear();
    }

    public void registrarHecho(Hecho hecho) {
        this.hechos.add(hecho);
    }

    public List<String> obtenerConclusiones() {
        return new ArrayList<>(conclusiones);
    }

    public void finalizarSesion() {
        this.fechaFin = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getBaseConocimientoId() {
        return baseConocimientoId;
    }

    public void setBaseConocimientoId(int baseConocimientoId) {
        this.baseConocimientoId = baseConocimientoId;
    }

    public List<Hecho> getHechos() {
        return hechos;
    }

    public void setHechos(List<Hecho> hechos) {
        this.hechos = hechos;
    }

    public List<String> getConclusiones() {
        return conclusiones;
    }

    public void setConclusiones(List<String> conclusiones) {
        this.conclusiones = conclusiones;
    }

    public void agregarConclusion(String conclusion) {
        this.conclusiones.add(conclusion);
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }
}