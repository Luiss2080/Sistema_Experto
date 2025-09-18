package persistencia.dao;

import modelo.entidades.Usuario;
import java.util.*;

public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public boolean crear(Usuario usuario) {
        return DataStore.crearUsuario(usuario);
    }

    @Override
    public boolean actualizar(Usuario usuario) {
        // Por simplicidad, no implementamos actualización de usuarios
        return false;
    }

    @Override
    public boolean eliminar(int id) {
        // Por simplicidad, no implementamos eliminación de usuarios
        return false;
    }

    @Override
    public Usuario buscarPorCredenciales(String nombreUsuario, String contrasena) {
        return DataStore.buscarUsuarioPorCredenciales(nombreUsuario, contrasena);
    }

    @Override
    public Usuario buscarPorId(int id) {
        return DataStore.buscarUsuarioPorId(id);
    }

    @Override
    public List<Usuario> listarTodos() {
        // Por simplicidad, no implementamos listar todos los usuarios
        return new ArrayList<>();
    }
}