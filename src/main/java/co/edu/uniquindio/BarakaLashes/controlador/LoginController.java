package co.edu.uniquindio.BarakaLashes.controlador;

import co.edu.uniquindio.BarakaLashes.modelo.Usuario;
import co.edu.uniquindio.BarakaLashes.repositorio.UsuarioRepositorio;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UsuarioRepositorio usuarioRepositorio;

    @GetMapping("/login")
    public String mostrarLogin(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            if ("no_autenticado".equals(error)) {
                model.addAttribute("error", "Debes iniciar sesión para acceder a esa página");
            } else {
                model.addAttribute("error", "Credenciales inválidas");
            }
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        try {
            log.info("Intento de login para: {}", email);

            // Buscar usuario por email
            Optional<Usuario> usuarioOpt = usuarioRepositorio.findByEmail(email);

            if (usuarioOpt.isEmpty()) {
                log.warn("Usuario no encontrado: {}", email);
                model.addAttribute("error", "Usuario no encontrado");
                return "login";
            }

            Usuario usuario = usuarioOpt.get();

            // Comparar contraseña directamente (sin encriptar)
            if (!password.equals(usuario.getPassword())) {
                log.warn("Contraseña incorrecta para: {}", email);
                model.addAttribute("error", "Contraseña incorrecta");
                return "login";
            }

            // Guardar información del usuario en la sesión
            session.setAttribute("usuarioEmail", usuario.getEmail());
            session.setAttribute("usuarioId", usuario.getIdUsuario());
            session.setAttribute("usuarioNombre", usuario.getNombre());
            session.setAttribute("usuarioAutenticado", true);

            log.info("✅ Login exitoso para: {}", email);

            return "redirect:/home";

        } catch (Exception e) {
            log.error("Error en login: {}", e.getMessage(), e);
            model.addAttribute("error", "Error al iniciar sesión");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        log.info("Usuario cerrando sesión: {}", session.getAttribute("usuarioEmail"));
        session.invalidate();
        return "redirect:/login?logout=true";
    }
}