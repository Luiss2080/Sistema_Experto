package persistencia.dao;

import modelo.entidades.BaseConocimiento;
import java.util.*;

public class BaseConocimientoDAOImpl implements BaseConocimientoDAO {

    @Override
    public BaseConocimiento crear(BaseConocimiento base) {
        return DataStore.crearBase(base);
    }

    @Override
    public boolean actualizar(BaseConocimiento base) {
        return DataStore.actualizarBase(base);
    }

    @Override
    public boolean eliminar(int id) {
        return DataStore.eliminarBase(id);
    }

    @Override
    public BaseConocimiento buscarPorId(int id) {
        return DataStore.buscarBasePorId(id);
    }

    @Override
    public List<BaseConocimiento> listarPorUsuario(int usuarioId) {
        return DataStore.listarBasesPorUsuario(usuarioId);
    }

    @Override
    public List<BaseConocimiento> listarTodas() {
        return DataStore.listarTodasLasBases();
    }
}