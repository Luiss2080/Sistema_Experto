package persistencia.dao;

import modelo.entidades.Sesion;
import java.util.*;

public class SesionDAOImpl implements SesionDAO {

    @Override
    public boolean crear(Sesion sesion) {
        return DataStore.crearSesion(sesion);
    }

    @Override
    public boolean actualizar(Sesion sesion) {
        // Por simplicidad, no implementamos actualización de sesiones
        return false;
    }

    @Override
    public boolean eliminar(int id) {
        // Por simplicidad, no implementamos eliminación de sesiones
        return false;
    }

    @Override
    public Sesion buscarPorId(int id) {
        // Por simplicidad, no implementamos búsqueda por ID de sesiones
        return null;
    }

    @Override
    public List<Sesion> buscarPorUsuario(int usuarioId) {
        return DataStore.buscarSesionesPorUsuario(usuarioId);
    }

    @Override
    public List<Sesion> obtenerHistorial(int usuarioId) {
        return DataStore.obtenerHistorialUsuario(usuarioId);
    }

    @Override
    public List<Sesion> listarPorBase(int baseConocimientoId) {
        return DataStore.listarSesionesPorBase(baseConocimientoId);
    }
}