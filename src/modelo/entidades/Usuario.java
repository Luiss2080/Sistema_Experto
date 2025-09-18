package modelo.entidades;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String nombreUsuario;
    private String contrasena;
    private TipoUsuario tipoUsuario;
    private LocalDateTime fechaRegistro;

    public enum TipoUsuario {
        ADMINISTRADOR, CLIENTE
    }

    public Usuario() {
        this.fechaRegistro = LocalDateTime.now();
    }

    public Usuario(String nombreUsuario, String contrasena, TipoUsuario tipoUsuario) {
        this();
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.tipoUsuario = tipoUsuario;
    }

    public boolean validarCredenciales(String usuario, String contrasena) {
        return this.nombreUsuario.equals(usuario) && this.contrasena.equals(contrasena);
    }

    public String obtenerPermisos() {
        return tipoUsuario == TipoUsuario.ADMINISTRADOR ? "FULL" : "READ";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}