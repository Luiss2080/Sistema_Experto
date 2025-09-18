package persistencia.dao;

import modelo.entidades.Sesion;
import java.util.List;

public interface SesionDAO {
    boolean crear(Sesion sesion);
    boolean actualizar(Sesion sesion);
    boolean eliminar(int id);
    Sesion buscarPorId(int id);
    List<Sesion> buscarPorUsuario(int usuarioId);
    List<Sesion> obtenerHistorial(int usuarioId);
    List<Sesion> listarPorBase(int baseConocimientoId);
}