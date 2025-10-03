package co.edu.uniquindio.BarakaLashes.servicio;

import co.edu.uniquindio.BarakaLashes.modelo.Usuario;
import co.edu.uniquindio.BarakaLashes.repositorio.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServicio {

    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder;

    public Usuario crearUsuario(Usuario usuario) {

        if (usuarioRepositorio.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con este email: " + usuario.getEmail());
        }
        if (usuarioRepositorio.existsByCedula(usuario.getCedula())) {
            throw new RuntimeException("Ya existe un usuario con esta cédula: " + usuario.getCedula());
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepositorio.save(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepositorio.findAll();
    }

    public Usuario obtenerUsuarioPorId(Integer id) {
        return usuarioRepositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioRepositorio.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
    }

    public Usuario obtenerUsuarioPorCedula(String cedula) {
        return usuarioRepositorio.findByCedula(cedula)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con cédula: " + cedula));
    }

    public Usuario actualizarUsuario(Integer id, Usuario usuarioActualizado) {
        Usuario usuarioExistente = obtenerUsuarioPorId(id);

        // Validar que el nuevo email no esté en uso por otro usuario
        if (!usuarioExistente.getEmail().equals(usuarioActualizado.getEmail()) &&
                usuarioRepositorio.existsByEmail(usuarioActualizado.getEmail())) {
            throw new RuntimeException("El email " + usuarioActualizado.getEmail() + " ya está en uso por otro usuario");
        }

        // Validar que la nueva cédula no esté en uso por otro usuario
        if (!usuarioExistente.getCedula().equals(usuarioActualizado.getCedula()) &&
                usuarioRepositorio.existsByCedula(usuarioActualizado.getCedula())) {
            throw new RuntimeException("La cédula " + usuarioActualizado.getCedula() + " ya está en uso por otro usuario");
        }

        // Actualizar campos
        usuarioExistente.setNombre(usuarioActualizado.getNombre());
        usuarioExistente.setApellido(usuarioActualizado.getApellido());
        usuarioExistente.setCedula(usuarioActualizado.getCedula());
        usuarioExistente.setEmail(usuarioActualizado.getEmail());
        usuarioExistente.setTelefono(usuarioActualizado.getTelefono());
        usuarioExistente.setNegocio(usuarioActualizado.getNegocio());

        return usuarioRepositorio.save(usuarioExistente);
    }

    public void eliminarUsuario(Integer id) {
        if (!usuarioRepositorio.existsById(id)) {
            throw new RuntimeException("Usuario a eliminar no encontrado con ID: " + id);
        }
        usuarioRepositorio.deleteById(id);
    }

    public boolean existeUsuarioPorEmail(String email) {
        return usuarioRepositorio.existsByEmail(email);
    }

    public boolean existeUsuarioPorCedula(String cedula) {
        return usuarioRepositorio.existsByCedula(cedula);
    }
}