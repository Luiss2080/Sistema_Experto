package persistencia.dao;

import modelo.entidades.Usuario;
import java.util.List;

public interface UsuarioDAO {
    boolean crear(Usuario usuario);
    boolean actualizar(Usuario usuario);
    boolean eliminar(int id);
    Usuario buscarPorCredenciales(String nombreUsuario, String contrasena);
    Usuario buscarPorId(int id);
    List<Usuario> listarTodos();
}