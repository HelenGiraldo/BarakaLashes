package co.edu.uniquindio.BarakaLashes.servicio.Implementaciones;

import co.edu.uniquindio.BarakaLashes.DTO.LoginDTO;
import co.edu.uniquindio.BarakaLashes.DTO.RegistroDTO;
import co.edu.uniquindio.BarakaLashes.modelo.RolUsuario;
import co.edu.uniquindio.BarakaLashes.modelo.Usuario;
import co.edu.uniquindio.BarakaLashes.repositorio.UsuarioRepositorio;
import co.edu.uniquindio.BarakaLashes.servicio.AuthServicio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServicioImpl implements AuthServicio {

    private final UsuarioRepositorio usuarioRepositorio;

    // Cédula especial para convertir usuario en ADMIN al registrarse
    private static final String ADMIN_CEDULA = "1000000000";

    @Override
    public Usuario login(LoginDTO loginDTO) {
        log.info("Intentando login para: {}", loginDTO.getEmail());

        // Buscar usuario en la base de datos
        Optional<Usuario> usuarioOptional = usuarioRepositorio.findByEmail(loginDTO.getEmail());

        if (usuarioOptional.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        Usuario usuario = usuarioOptional.get();

        // Verificar contraseña (en producción usaríamos BCrypt)
        if (!usuario.getPassword().equals(loginDTO.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        log.info("Login exitoso para: {} {} - Rol: {}",
                usuario.getNombre(), usuario.getApellido(), usuario.getRol());
        return usuario;
    }

    @Override
    public Usuario registrar(RegistroDTO registroDTO) {
        log.info("Registrando nuevo usuario: {}", registroDTO.getEmail());

        // Validaciones
        if (usuarioRepositorio.existsByEmail(registroDTO.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        if (usuarioRepositorio.existsByCedula(registroDTO.getCedula())) {
            throw new RuntimeException("La cédula ya está registrada");
        }

        if (!registroDTO.getPassword().equals(registroDTO.getConfirmarPassword())) {
            throw new RuntimeException("Las contraseñas no coinciden");
        }

        if (registroDTO.getPassword().length() < 6) {
            throw new RuntimeException("La contraseña debe tener al menos 6 caracteres");
        }

        // Determinar rol: Si usa la cédula especial, es ADMIN, sino USUARIO
        RolUsuario rol = ADMIN_CEDULA.equals(registroDTO.getCedula()) ?
                RolUsuario.ADMIN : RolUsuario.USUARIO;

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setNombre(registroDTO.getNombre());
        usuario.setApellido(registroDTO.getApellido());
        usuario.setCedula(registroDTO.getCedula());
        usuario.setEmail(registroDTO.getEmail());
        usuario.setTelefono(registroDTO.getTelefono());
        usuario.setPassword(registroDTO.getPassword());
        usuario.setRol(rol);

        Usuario usuarioGuardado = usuarioRepositorio.save(usuario);

        if (rol == RolUsuario.ADMIN) {
            log.info("✅ ADMIN registrado exitosamente: {} {}",
                    usuarioGuardado.getNombre(), usuarioGuardado.getApellido());
        } else {
            log.info("✅ Usuario registrado exitosamente: {} {}",
                    usuarioGuardado.getNombre(), usuarioGuardado.getApellido());
        }

        return usuarioGuardado;
    }

    @Override
    public boolean validarCredenciales(String email, String password) {
        Optional<Usuario> usuarioOptional = usuarioRepositorio.findByEmail(email);
        return usuarioOptional.isPresent() &&
                usuarioOptional.get().getPassword().equals(password);
    }

    @Override
    public boolean existeUsuario(String email) {
        return usuarioRepositorio.existsByEmail(email);
    }

    @Override
    public boolean existeCedula(String cedula) {
        return usuarioRepositorio.existsByCedula(cedula);
    }
}