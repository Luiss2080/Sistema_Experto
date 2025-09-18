package persistencia.dao;

import modelo.entidades.BaseConocimiento;
import java.util.List;

public interface BaseConocimientoDAO {
    BaseConocimiento crear(BaseConocimiento base);
    boolean actualizar(BaseConocimiento base);
    boolean eliminar(int id);
    BaseConocimiento buscarPorId(int id);
    List<BaseConocimiento> listarPorUsuario(int usuarioId);
    List<BaseConocimiento> listarTodas();
}